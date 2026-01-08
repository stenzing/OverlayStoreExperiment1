from random import randint
from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    wait_time = between(0.1, 2.0)

    @task
    def test01(self):
        rand = randint(1,500000)
        with self.client.get("/01.02.03a/path%06d" % rand, catch_response=True, name="Volume A") as response:
            response.success()

    @task
    def test02(self):
        rand = randint(1,500000)
        with self.client.get("/01.02.03b/path%06d" % rand, catch_response=True, name="Volume B") as response:
            response.success()
