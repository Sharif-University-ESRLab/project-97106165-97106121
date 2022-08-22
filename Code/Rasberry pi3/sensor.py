import RPi.GPIO as GPIO
import time
import statistics

trigger_pin_sen1 = 4
echo_pin_sen1 = 17

trigger_pin_sen2 = 27
echo_pin_sen2 = 22

trigger_pin_sen3 = 23
echo_pin_sen3 = 24

trigger_pin_sen4 = 20
echo_pin_sen4 = 21

number_of_samples = 5
sample_sleep = 0.1
calibration1 = 30
calibration2 = 1750
time_out = 0.5

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

GPIO.setup(trigger_pin_sen1, GPIO.OUT)
GPIO.setup(trigger_pin_sen2, GPIO.OUT)
GPIO.setup(trigger_pin_sen3, GPIO.OUT)
GPIO.setup(trigger_pin_sen4, GPIO.OUT)


GPIO.setup(echo_pin_sen1, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)
GPIO.setup(echo_pin_sen2, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)
GPIO.setup(echo_pin_sen3, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)
GPIO.setup(echo_pin_sen4, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)


samples_list_sen1 = []
stack_sen1 = []

samples_list_sen2 = []
stack_sen2 = []

samples_list_sen3 = []
stack_sen3 = []

samples_list_sen4 = []
stack_sen4 = []

def timer_call_sen1(channel):
    now = time.monotonic()
    stack_sen1.append(now)

def timer_call_sen2(channel):
    now = time.monotonic()
    stack_sen2.append(now)

def timer_call_sen3(channel):
    now = time.monotonic()
    stack_sen3.append(now)

def timer_call_sen4(channel):
    now = time.monotonic()
    stack_sen4.append(now)

def trigger():
    GPIO.output(trigger_pin_sen1, GPIO.HIGH)
    GPIO.output(trigger_pin_sen2, GPIO.HIGH)
    GPIO.output(trigger_pin_sen3, GPIO.HIGH)
    GPIO.output(trigger_pin_sen4, GPIO.HIGH)
    time.sleep(0.0001)
    GPIO.output(trigger_pin_sen1, GPIO.LOW)
    GPIO.output(trigger_pin_sen2, GPIO.LOW)
    GPIO.output(trigger_pin_sen3, GPIO.LOW)
    GPIO.output(trigger_pin_sen4, GPIO.LOW)


def trigger_sen1():
    GPIO.output(trigger_pin_sen1, GPIO.HIGH)
    time.sleep(0.0001)
    GPIO.output(trigger_pin_sen1, GPIO.LOW)

def trigger_sen2():
    GPIO.output(trigger_pin_sen2, GPIO.HIGH)
    time.sleep(0.0001)
    GPIO.output(trigger_pin_sen2, GPIO.LOW)
    
def trigger_sen3():
    GPIO.output(trigger_pin_sen3, GPIO.HIGH)
    time.sleep(0.0001)
    GPIO.output(trigger_pin_sen3, GPIO.LOW)
    
def trigger_sen4():
    GPIO.output(trigger_pin_sen4, GPIO.HIGH)
    time.sleep(0.0001)
    GPIO.output(trigger_pin_sen4, GPIO.LOW)

def check_distance():
    samples_list_sen1.clear()
    samples_list_sen2.clear()
    samples_list_sen3.clear()
    samples_list_sen4.clear()
    
    while len(samples_list_sen1) < number_of_samples:
        
        trigger()
        
        while len(stack_sen1) < 2 :
            start = time.monotonic()
            while time.monotonic() < start + time_out:
                pass
            trigger_sen1()
            
        if len(stack_sen1)== 2:
            samples_list_sen1.append(stack_sen1.pop() - stack_sen1.pop())
            
        elif len(stack_sen1) > 2:
            stack_sen1.clear()
        


        while len(stack_sen2) < 2 :
            start = time.monotonic()
            while time.monotonic() < start + time_out:
                pass
            trigger_sen2()
            
        if len(stack_sen2)== 2:
            samples_list_sen2.append(stack_sen2.pop() - stack_sen2.pop())
            
        elif len(stack_sen2) > 2:
            stack_sen2.clear()

        while len(stack_sen3) < 2 :
            start = time.monotonic()
            while time.monotonic() < start + time_out:
                pass
            trigger_sen3()
            
        if len(stack_sen3)== 2:
            samples_list_sen3.append(stack_sen3.pop() - stack_sen3.pop())
            
        elif len(stack_sen3) > 2:
            stack_sen3.clear()



        while len(stack_sen4) < 2 :
            start = time.monotonic()
            while time.monotonic() < start + time_out:
                pass
            trigger_sen4()
            
        if len(stack_sen4)== 2:
            samples_list_sen4.append(stack_sen4.pop() - stack_sen4.pop())
            
        elif len(stack_sen4) > 2:
            stack_sen4.clear()


        time.sleep(sample_sleep)
        
    return [statistics.median(samples_list_sen1)*1000000*calibration1/calibration2, statistics.median(samples_list_sen2)*1000000*calibration1/calibration2, statistics.median(samples_list_sen3)*1000000*calibration1/calibration2, statistics.median(samples_list_sen4)*1000000*calibration1/calibration2]
            

GPIO.add_event_detect(echo_pin_sen1, GPIO.BOTH, callback = timer_call_sen1)
GPIO.add_event_detect(echo_pin_sen2, GPIO.BOTH, callback = timer_call_sen2)
GPIO.add_event_detect(echo_pin_sen3, GPIO.BOTH, callback = timer_call_sen3)
GPIO.add_event_detect(echo_pin_sen4, GPIO.BOTH, callback = timer_call_sen4)

while True:
    print("sen1: ", round(check_distance()[0], 1))
    print("sen2: ", round(check_distance()[1], 1))
    print("sen3: ", round(check_distance()[2], 1))
    print("sen4: ", round(check_distance()[3], 1))

