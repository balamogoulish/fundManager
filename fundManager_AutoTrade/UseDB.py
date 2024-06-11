import yaml
import cx_Oracle
import datetime
import requests

apiAddr = "10.0.20.233"

with open('C:/Users/balam/VsCodeProject/FundManager/koreainvestment-autotrade-main/koreainvestment-autotrade-main/config.yaml', encoding='UTF-8') as f:
    _cfg = yaml.load(f, Loader=yaml.FullLoader)
DISCORD_WEBHOOK_URL = _cfg['DISCORD_WEBHOOK_URL']

def send_message(msg):
    """디스코드 메세지 전송"""
    now = datetime.datetime.now()
    message = {"content": f"[{now.strftime('%Y-%m-%d %H:%M:%S')}] {str(msg)}"}
    requests.post(DISCORD_WEBHOOK_URL, data=message)
    print(message)

#매일 주식이 시작될 때 fund 테이블을 불러와 추가함
def insert_fund_everyday():
    # Set up Oracle connection
    connection = cx_Oracle.connect(f"c##jsjeon113/jsjeon0718@{apiAddr}:1521/xe")
    cursor = connection.cursor()

    try:
        # Calculate fund_origin
        cursor.execute("""
            SELECT SUM(user_origin)
            FROM (
                SELECT user_origin, ROW_NUMBER() OVER (PARTITION BY user_index ORDER BY check_date DESC) as rn
                FROM check_gain
            ) 
            WHERE rn = 1
        """)
        fund_origin_result = cursor.fetchone()

        # Calculate fund_money
        cursor.execute("""
            SELECT SUM(user_money)
            FROM (
                SELECT user_money, ROW_NUMBER() OVER (PARTITION BY user_index ORDER BY invest_date DESC) as rn
                FROM INVEST
            ) 
            WHERE rn = 1
        """)
        fund_money_result = cursor.fetchone()


        if fund_origin_result and fund_money_result:
            fund_origin = fund_origin_result[0]
            fund_money = fund_money_result[0]
            least_value = 0.4 * fund_money

            # Insert a new row into the Fund table
            cursor.execute("""
                INSERT INTO Fund (fund_money, fund_output, fund_gain, fund_origin, least)
                VALUES (:1, NULL, NULL, :2, :3)
            """, (fund_money, fund_origin, least_value))

            # Commit the transaction
            connection.commit()

            send_message(f"총 펀드 원금: {fund_origin}")
            send_message(f"총 펀드 투자금: {fund_money}")
            send_message(f"총 펀드 실투자금: {fund_money*0.6}")
            send_message(f"총 펀드 잔여금: {least_value}")
            return fund_money
        else:
            print("Error retrieving data for calculations.")
            return None

    finally:
        # Close the cursor and connection
        cursor.close()
        connection.close()


#장이 끝나고 펀드 정산
def update_fund_finally(output_cash, total_cash):
    # Set up Oracle connection
    connection = cx_Oracle.connect(f"c##jsjeon113/jsjeon0718@{apiAddr}:1521/xe")
    cursor = connection.cursor()

    try:
        # Retrieve the current value of least
        cursor.execute("SELECT least FROM Fund WHERE fund_date = (SELECT MAX(fund_date) FROM FUND)")
        least_result = cursor.fetchone()

        if least_result:
            least_value = least_result[0]

            # Calculate total_fund_gain
            fund_gain = ((output_cash + total_cash*0.4 - total_cash) / total_cash) * 100

            # Update the Fund table
            cursor.execute("""
                UPDATE Fund
                SET fund_gain = :fund_gain, fund_output = :output_cash
                WHERE fund_date = (SELECT MAX(fund_date) FROM FUND)
            """, fund_gain=fund_gain, output_cash=output_cash)

            #Update Invest, Check table
            update_user_money_and_insert_check_gain(total_cash, fund_gain)
            
            # Commit the transaction
            connection.commit()
            send_message("===오늘의 투자 결과===")
            send_message(f"총 펀드 게인: {fund_gain}")
            send_message(f"총 펀드 주식 output: {output_cash+total_cash*0.4}")
            send_message(f"총 펀드 보유금액: {output_cash+least_value}")
        else:
            send_message("투자 결과 업데이트에 실패했습니다. 다시 시도해 주세요.")
    except Exception as e:
        print(f"Error updating Fund table: {e}")

    finally:
        # Close the cursor and connection
        cursor.close()
        connection.close()

def update_user_money_and_insert_check_gain(total_cash, fund_gain):
    # Oracle 연결 설정
    connection = cx_Oracle.connect(f"c##jsjeon113/jsjeon0718@{apiAddr}:1521/xe")
    cursor = connection.cursor()

    try:
        # 각 user_index에 대해 가장 최근 invest_date를 가진 user_index 및 user_money 검색
        cursor.execute("""
            SELECT user_index, user_money
            FROM (
                SELECT user_index, user_money, ROW_NUMBER() OVER (PARTITION BY user_index ORDER BY invest_date DESC) as rn
                FROM invest
            ) WHERE rn = 1
        """)
        
        user_data = cursor.fetchall()
        if user_data:
            for user_index, user_money in user_data:
                # user_gain 계산
                user_gain = user_money / total_cash * fund_gain

                # user_money 업데이트
                updated_user_money = user_money * (1 + user_gain / 100)

                # invest 테이블에 삽입
                cursor.execute("""
                    INSERT INTO invest (user_index, user_money, inout_money)
                    VALUES (:user_index, :updated_user_money, '0')
                """, user_index=user_index, updated_user_money=round(updated_user_money,3))
                
                #완료!!

                # user_index의 가장 최근 check_date에 대한 origin_money 검색
                cursor.execute("""
                    SELECT user_origin
                    FROM (
                        SELECT user_index, user_origin, ROW_NUMBER() OVER (PARTITION BY user_index ORDER BY check_date DESC) as rn
                        FROM check_gain WHERE user_index= :user_index
                    ) WHERE rn = 1
                """, user_index=user_index)
                
                user_origin = cursor.fetchone()
                user_origin_result=user_origin[0]
                
                # check_gain 테이블에 삽입
                cursor.execute("""
                    INSERT INTO check_gain (user_index, user_origin, user_gain)
                    VALUES (:user_index, :user_origin, :user_gain)
                """, user_index=user_index, user_origin=user_origin_result, user_gain=user_gain)
                
            # 트랜잭션 커밋
            connection.commit()
            send_message("===사용자 이익 및 자금 삽입이 완료되었습니다===")
        else:
            send_message("===사용자 이익 삽입 실패. 다시 시도해주세요===")
    except Exception as e:
        print(f"테이블 업데이트 오류: {e}")
    finally:
        # 커서와 연결 닫기
        cursor.close()
        connection.close()

# try:
#     total_cash = insert_fund_everyday()
#     real_input_cash = total_cash*0.6
#     buy_qty = real_input_cash // 1000 #몫
#     least_of_real_input_cash = real_input_cash -(buy_qty*1000) #나머지
#     send_message(f"===1000원에 매수를 시도합니다===")
#     send_message(f"매수 수량: {buy_qty}")
#     send_message(f"실매수금: {real_input_cash}")
#     send_message(f"잔여금: {least_of_real_input_cash}")

#     real_input_cash = least_of_real_input_cash + (2000*buy_qty)
#     send_message(f"===2000원에 매도를 시도합니다===")
#     send_message(f"매도 수량: {buy_qty}")
#     send_message(f"실매도금: {real_input_cash}")

#     insert_fund_everyday()
#     update_fund_finally(17453934.7674, 29011291.279)
# except Exception as e:
#     send_message(f"[오류 발생]{e}")
