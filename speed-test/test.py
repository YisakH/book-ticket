from locust import HttpUser, task, SequentialTaskSet, between, events
from requests.structures import CaseInsensitiveDict
import time
import itertools
from datetime import datetime, timedelta

class TicketPurchaseScenario(SequentialTaskSet):
    token = None
    seat_number = None
    target_time = None

    def on_start(self):
        # 각 사용자별 고유 좌석 번호를 설정
        self.seat_number = next(self.user.seat_numbers)
        self.target_time = datetime.now() + timedelta(minutes=1)  # 1분 후 집중 요청 시간 설정

    @task
    def login(self):
        response = self.client.post("/login/token", data={
            "userName": "yshong",
            "password": "yshong"
        })
        # 응답이 성공했는지 확인
        if response.status_code == 200:
            self.token = response.json().get("access_token")
        if not self.token:
            # 토큰이 없을 때 오류 처리
            print("Error: access_token not found in response")
            print()
            self.interrupt()  # 사용자 시나리오 중단
        
        # 로그인 후 대기 상태로 전환하여 타겟 시간을 기다림
        while datetime.now() < self.target_time:
            time.sleep(1)  # 1초마다 타겟 시간이 되었는지 확인

    @task
    def reserve_seat(self):
        headers = CaseInsensitiveDict()
        headers["Authorization"] = f"Bearer {self.token}"
        self.client.get("/seat/buy", headers=headers)
        
        
        headers = CaseInsensitiveDict()
        headers["Authorization"] = f"Bearer {self.token}"
        self.client.put(f"/seat/buy?seatNumber={self.seat_number}", headers=headers)
        self.interrupt()  # 구매 후 시나리오 종료

class TicketPurchaseUser(HttpUser):
    tasks = [TicketPurchaseScenario]
    wait_time = between(1, 2)

    # 좌석 번호 순차 할당 제너레이터
    seat_numbers = itertools.cycle(range(1, 10001))  # 1번부터 10000번까지 순차적으로

    def on_start(self):
        # 사용자 시작 시 고유 좌석 번호 생성기 설정
        self.seat_numbers = TicketPurchaseUser.seat_numbers
