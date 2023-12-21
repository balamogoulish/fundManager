import requests
import json
import datetime
import time
import yaml
from time import sleep

import cx_Oracle
from UseDB import insert_fund_everyday, update_fund_finally



with open('C:/Users/balam/VsCodeProject/FundManager/koreainvestment-autotrade-main/koreainvestment-autotrade-main/config.yaml', encoding='UTF-8') as f:
    _cfg = yaml.load(f, Loader=yaml.FullLoader)
APP_KEY = _cfg['APP_KEY']
APP_SECRET = _cfg['APP_SECRET']
ACCESS_TOKEN = ""
CANO = _cfg['CANO']
ACNT_PRDT_CD = _cfg['ACNT_PRDT_CD']
DISCORD_WEBHOOK_URL = _cfg['DISCORD_WEBHOOK_URL']
URL_BASE = _cfg['URL_BASE']


#디스코드 메세지 전송
def send_message(msg):
    """디스코드 메세지 전송"""
    now = datetime.datetime.now()
    message = {"content": f"[{now.strftime('%Y-%m-%d %H:%M:%S')}] {str(msg)}"}
    requests.post(DISCORD_WEBHOOK_URL, data=message)
    print(message)

#필요 토큰 발급
def get_access_token():
    """토큰 발급"""
    headers = {"content-type":"application/json"}
    body = {"grant_type":"client_credentials",
    "appkey":APP_KEY, 
    "appsecret":APP_SECRET}
    PATH = "oauth2/tokenP"
    URL = f"{URL_BASE}/{PATH}"
    res = requests.post(URL, headers=headers, data=json.dumps(body))
    ACCESS_TOKEN = res.json()["access_token"]
    return ACCESS_TOKEN
    
#암호화
def hashkey(datas):
    """암호화"""
    PATH = "uapi/hashkey"
    URL = f"{URL_BASE}/{PATH}"
    headers = {
    'content-Type' : 'application/json',
    'appKey' : APP_KEY,
    'appSecret' : APP_SECRET,
    }
    res = requests.post(URL, headers=headers, data=json.dumps(datas))
    hashkey = res.json()["HASH"]
    return hashkey

#현재 가격 조회
def get_current_price(code="083650"):
    """현재가 조회"""
    PATH = "uapi/domestic-stock/v1/quotations/inquire-price"
    URL = f"{URL_BASE}/{PATH}"
    headers = {"Content-Type":"application/json", 
            "authorization": f"Bearer {ACCESS_TOKEN}",
            "appKey":APP_KEY,
            "appSecret":APP_SECRET,
            "tr_id":"FHKST01010100"}
    params = {
    "fid_cond_mrkt_div_code":"J",
    "fid_input_iscd":code,
    }
    res = requests.get(URL, headers=headers, params=params)
    return int(res.json()['output']['stck_prpr'])

#매수 목표 가격 조회
def get_target_price(code="083650"):
    """변동성 돌파 전략으로 매수 목표가 조회"""
    PATH = "uapi/domestic-stock/v1/quotations/inquire-daily-price"
    URL = f"{URL_BASE}/{PATH}"
    headers = {"Content-Type":"application/json", 
        "authorization": f"Bearer {ACCESS_TOKEN}",
        "appKey":APP_KEY,
        "appSecret":APP_SECRET,
        "tr_id":"FHKST01010400"}
    params = {
    "fid_cond_mrkt_div_code":"J",
    "fid_input_iscd":code,
    "fid_org_adj_prc":"1",
    "fid_period_div_code":"D"
    }
    res = requests.get(URL, headers=headers, params=params)
    stck_oprc = int(res.json()['output'][0]['stck_oprc']) #오늘 시가
    stck_hgpr = int(res.json()['output'][1]['stck_hgpr']) #전일 고가
    stck_lwpr = int(res.json()['output'][1]['stck_lwpr']) #전일 저가
    target_price = stck_oprc + (stck_hgpr - stck_lwpr) * 0.5
    return target_price

def get_lowest_price(code="083650"):
    """변동성 돌파 전략으로 매수 목표가 조회"""
    PATH = "uapi/domestic-stock/v1/quotations/inquire-daily-price"
    URL = f"{URL_BASE}/{PATH}"
    headers = {"Content-Type":"application/json", 
        "authorization": f"Bearer {ACCESS_TOKEN}",
        "appKey":APP_KEY,
        "appSecret":APP_SECRET,
        "tr_id":"FHKST01010400"}
    params = {
    "fid_cond_mrkt_div_code":"J",
    "fid_input_iscd":code,
    "fid_org_adj_prc":"1",
    "fid_period_div_code":"D"
    }
    res = requests.get(URL, headers=headers, params=params)
    stck_lwpr = int(res.json()['output'][1]['stck_lwpr']) #전일 저가
    return stck_lwpr

def send_message(msg):
    """디스코드 메세지 전송"""
    now = datetime.datetime.now()
    message = {"content": f"[{now.strftime('%Y-%m-%d %H:%M:%S')}] {str(msg)}"}
    requests.post(DISCORD_WEBHOOK_URL, data=message)
    print(message)

# 자동매매 시작
try:
    ACCESS_TOKEN = get_access_token()
    symbol_list = ["083650"] # 매수 희망 종목 리스트
    
    lowest_yesterday = get_lowest_price()
    buy_target_price = lowest_yesterday*1.015
    sell_target_price = lowest_yesterday*1.025

    soldout = False
    total_cash = 0
    real_input_cash = 0
    
    buy_qty = 0
    least_of_real_input_cash =0

    send_message("========국내 주식 자동매매 프로그램을 시작합니다========")
    send_message(f"TOKEN: {ACCESS_TOKEN}")
    
    while True:
        t_now = datetime.datetime.now()
        t_9 = t_now.replace(hour=9, minute=0, second=0, microsecond=0)
        t_start = t_now.replace(hour=9, minute=0, second=10, microsecond=0)
        t_sell = t_now.replace(hour=15, minute=15, second=0, microsecond=0)
        t_exit = t_now.replace(hour=15, minute=15, second=10,microsecond=0)

        today = datetime.datetime.today().weekday()

        if today == 5 or today == 6:  # 토요일이나 일요일이면 자동 종료
            send_message("주말이므로 프로그램을 종료합니다.")
            break

        if t_9 < t_now < t_start: # 펀드 시작
            total_cash = insert_fund_everyday()
            real_input_cash = total_cash*0.6
            if total_cash is not None:
                send_message(f"===펀드 정보를 성공적으로 불러왔습니다===")
                soldout=True
                send_message(f"목표 매수 금액: {buy_target_price}")
                send_message(f"목표 매도 금액: {sell_target_price}")
                time.sleep(10)
            else:
                send_message(f"===펀드 정보를 불러오는데 실패했습니다===")
                break
        if t_start < t_now < t_sell :  # AM 09:10 ~ PM 03:15 : 매수
            current_price = get_current_price()
            if buy_target_price >= current_price and soldout == True:
                buy_qty = real_input_cash // current_price 
                if buy_qty > 0:
                    least_of_real_input_cash = real_input_cash - (buy_qty*current_price)
                    send_message(f"===목표가 달성({buy_target_price} > {current_price}) 매수를 시도합니다===")
                    send_message(f"매수 수량: {buy_qty}")
                    send_message(f"매수 가격: {current_price}")
                    send_message(f"잔여금: {least_of_real_input_cash}")
                    soldout = False
                    time.sleep(1)
            if sell_target_price <= current_price and soldout == False:
                real_input_cash = least_of_real_input_cash + (current_price*buy_qty)
                soldout = True
                send_message(f"===목표가 달성({sell_target_price} < {current_price}) 매도를 시도합니다===")
                send_message(f"매도 수량: {buy_qty}")
                send_message(f"매도 가격: {current_price}")
                send_message(f"실매도금: {real_input_cash}")
                time.sleep(1)
            if t_now.minute == 30 and t_now.second <= 10: 
                send_message("===정상적으로 실행 중입니다===")
                time.sleep(1)
            time.sleep(60)


        if t_sell < t_now < t_exit:  # PM 03:15 ~ PM 03:15:10 : 일괄 매도
            if soldout == False:
                sell_price = get_current_price()
                real_input_cash = least_of_real_input_cash + (sell_price*buy_qty)
                send_message(f"===일괄 매도를 시도합니다===")
                send_message(f"매도 수량: {buy_qty}")
                send_message(f"매도 가격: {sell_price}")
                send_message(f"실매도금: {real_input_cash}")
                time.sleep(10)

        if t_exit < t_now:  # PM 03:15:10 ~ :프로그램 종료
            update_fund_finally(real_input_cash, total_cash)
            send_message("========프로그램을 종료합니다========")
            break
except Exception as e:
    send_message(f"[오류 발생]{e}")
    time.sleep(1)
