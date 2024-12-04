from locust import HttpUser, task, between
import itertools
import time
# request post를 위한 import
from requests import request

# 모든 사용자가 동일한 목표 시간에 동기화하도록 설정
target_time = time.time() + 60  # 10분 후 목표 시간 설정

class TicketBuyer(HttpUser):
    wait_time = between(1, 5)  # 각 요청 사이의 대기 시간

    # 좌석 번호를 순차적으로 할당하는 제너레이터 (1부터 1000까지)
    seat_numbers = itertools.cycle(range(1, 1001))
            # 로그인 및 토큰 획득
    response = request("POST", "http://localhost:8080/login/token", data={
        "userName": "yshong", "password": "yshong"})

    token = response.json().get("access_token")

    def on_start(self):
        # 각 사용자별 고유 좌석 번호 설정
        self.seat_number = next(self.seat_numbers)

    @task
    def buy_ticket(self):
        headers = {"Authorization": f"Bearer {self.token}"}
        
        # 좌석 조회
        self.client.get("/seat/buy", headers=headers)
        
        # 좌석 구매
        self.client.put(f"/seat/buy?seatNumber={self.seat_number}", headers=headers)
        
        # 구매 후 사용자 종료
        self.environment.runner.quit()
