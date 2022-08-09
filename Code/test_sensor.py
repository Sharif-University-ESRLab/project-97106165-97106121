import RPi.GPIO as GPIO
import time
import statistics

trigger_pin = 4
echo_pin = 17
number_of_samples = 5
sample_sleep = 0.1
calibration1 = 30
calibration2 = 1750
time_out = 0.5

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

GPIO.setup(trigger_pin, GPIO.OUT)
GPIO.setup(echo_pin, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)

samples_list = []
stack = []

def timer_call(channel):
    now = time.monotonic()
    stack.append(now)

def trigger():
    GPIO.output(trigger_pin, GPIO.HIGH)
    time.sleep(0.0001)
    GPIO.output(trigger_pin, GPIO.LOW)

def check_distance():
    samples_list.clear()
    
    while len(samples_list) < number_of_samples:
        
        trigger()
        
        while len(stack) < 2 :
            start = time.monotonic()
            while time.monotonic() < start + time_out:
                pass
            trigger()
            
        if len(stack)== 2:
            samples_list.append(stack.pop() - stack.pop())
            
        elif len(stack) > 2:
            stack.clear()
        
        time.sleep(sample_sleep)
        
    return statistics.median(samples_list)*1000000*calibration1/calibration2
            

GPIO.add_event_detect(echo_pin, GPIO.BOTH, callback = timer_call)

for i in range(100):
    print(round(check_distance(), 1))


