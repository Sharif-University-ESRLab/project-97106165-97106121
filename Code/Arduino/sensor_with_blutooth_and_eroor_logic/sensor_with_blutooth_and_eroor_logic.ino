#include <SoftwareSerial.h> 
SoftwareSerial MyBlue(19, 18);

String msg;


const unsigned int TRIG_PIN1=13;
const unsigned int ECHO_PIN1=12;
const unsigned int TRIG_PIN2=11;
const unsigned int ECHO_PIN2=10;
const unsigned int TRIG_PIN3=9;
const unsigned int ECHO_PIN3=8;
const unsigned int TRIG_PIN4=7;
const unsigned int ECHO_PIN4=6;
const unsigned int VCC2=5;
const unsigned int VCC3=4;
const unsigned int VCC4=3;
const unsigned int BAUD_RATE=9600;

void setup() {
  pinMode(TRIG_PIN1, OUTPUT);
  pinMode(ECHO_PIN1, INPUT);
  pinMode(TRIG_PIN2, OUTPUT);
  pinMode(ECHO_PIN2, INPUT);
  pinMode(TRIG_PIN3, OUTPUT);
  pinMode(ECHO_PIN3, INPUT);
  pinMode(TRIG_PIN4, OUTPUT);
  pinMode(ECHO_PIN4, INPUT);
  pinMode(VCC2, OUTPUT);
  pinMode(VCC3, OUTPUT);
  pinMode(VCC4, OUTPUT);
  digitalWrite(VCC2, HIGH);
  digitalWrite(VCC3, HIGH);
  digitalWrite(VCC4, HIGH);
  Serial.begin(BAUD_RATE);


  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);
  MyBlue.begin(9600);
}

void loop() {
  digitalWrite(TRIG_PIN1, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN1, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN1, LOW);
  

 const unsigned long duration1= pulseIn(ECHO_PIN1, HIGH);
 int distance1= duration1/29/2;
 if(duration1==0){
   Serial.println("Warning: no pulse from sensor1");
   } 
  else{
      if (distance1 < 50) {
        MyBlue.write("L");
        digitalWrite(LED_BUILTIN, HIGH);
      }
      Serial.print(duration1);
      Serial.print("s-sensor1 distance to nearest object:");
      Serial.print(distance1);
      Serial.println(" cm");
  }

  
   delay(2000);
  digitalWrite(TRIG_PIN2, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN2, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN2, LOW);
  

 const unsigned long duration2= pulseIn(ECHO_PIN2, HIGH);
 int distance2= duration2/29/2;
 if(duration2==0){
   Serial.println("Warning: no pulse from sensor2");
   } 
  else{
      if (distance2 < 50) {
        MyBlue.write("R");
        digitalWrite(LED_BUILTIN, HIGH);
      }
      Serial.print(duration2);
      Serial.print("s-sensor2 distance to nearest object:");
      Serial.print(distance2);
      Serial.println(" cm");
  }

  delay(2000);
  digitalWrite(TRIG_PIN3, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN3, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN3, LOW);
  

 const unsigned long duration3= pulseIn(ECHO_PIN3, HIGH);
 int distance3= duration3/29/2;
 if(duration3==0){
   Serial.println("Warning: no pulse from sensor3");
   } 
  else{
      if (distance1 < 50) {
        MyBlue.write("C");
        digitalWrite(LED_BUILTIN, HIGH);
      }
      Serial.print(duration3);
      Serial.print("s-sensor3 distance to nearest object:");
      Serial.print(distance3);
      Serial.println(" cm");
  }

  delay(2000);
  digitalWrite(TRIG_PIN4, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN4, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN4, LOW);
  

 const unsigned long duration4= pulseIn(ECHO_PIN4, HIGH);
 int distance4= duration4/29/2;
 if(duration4==0){
   Serial.println("Warning: no pulse from sensor4");
   } 
  else{
      if (distance1 < 58) {
        MyBlue.write("C");
        digitalWrite(LED_BUILTIN, HIGH);
      }
      Serial.print(duration4);
      Serial.print("s-sensor4 distance to nearest object:");
      Serial.print(distance4);
      Serial.println(" cm");
  }
 delay(1000);
 }
