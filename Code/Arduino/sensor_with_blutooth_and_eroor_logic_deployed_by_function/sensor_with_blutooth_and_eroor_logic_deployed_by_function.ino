#include <SoftwareSerial.h> 
SoftwareSerial MyBlue(19, 18);

String msg;


const unsigned int TRIG_PIN1=13;
const unsigned int ECHO_PIN1=12;
const unsigned int TRIG_PIN2=11;
const unsigned int ECHO_PIN2=10;
const unsigned int TRIG_PIN3=9;
const unsigned int ECHO_PIN3=8;
const unsigned int BAUD_RATE=9600;


void distance_detector(unsigned int TRIG_PIN, unsigned int ECHO_PIN, int sensor_num){
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  

 const unsigned long duration= pulseIn(ECHO_PIN, HIGH);
 int distance= duration/29/2;
 if(duration==0){
   Serial.print("Warning: no pulse from sensor");
   Serial.println(sensor_num);
   } 
  else{
      if (distance < 50) {
        if (sensor_num == 1)
          MyBlue.write('L');
        if (sensor_num == 2)
          MyBlue.write('R');
        if (sensor_num == 3)
          MyBlue.write('C');
        MyBlue.write('\n');
        digitalWrite(LED_BUILTIN, HIGH);
      }
      Serial.print(duration);
      Serial.print("s-sensor ");
      Serial.print(sensor_num);
      Serial.print(" distance to nearest object:");
      Serial.print(distance);
      Serial.println(" cm");
  }
}

void setup() {
  pinMode(TRIG_PIN1, OUTPUT);
  pinMode(ECHO_PIN1, INPUT);
  pinMode(TRIG_PIN2, OUTPUT);
  pinMode(ECHO_PIN2, INPUT);
  pinMode(TRIG_PIN3, OUTPUT);
  pinMode(ECHO_PIN3, INPUT);
  Serial.begin(BAUD_RATE);


  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);
  MyBlue.begin(9600);
}

void loop() {
  
  distance_detector(TRIG_PIN1, ECHO_PIN1, 1);

  delay(200);
  
  distance_detector(TRIG_PIN2, ECHO_PIN2, 2);

  delay(200);
  
  distance_detector(TRIG_PIN3, ECHO_PIN3, 3);

  delay(200);
 }
