#include <SoftwareSerial.h> 
SoftwareSerial MyBlue(19, 18);

String msg;

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);
  Serial.begin(9600);
  MyBlue.begin(9600);
}

void loop() {
  if (MyBlue.available()) {
    Serial.write(MyBlue.read());
    digitalWrite(LED_BUILTIN, HIGH);
    delay(1000);
    digitalWrite(LED_BUILTIN, LOW);
    delay(1000);
  }

  if (Serial.available()) {
    int readVal = Serial.read();
    MyBlue.write(readVal);
    digitalWrite(LED_BUILTIN, HIGH);
  }
  else{
    digitalWrite(LED_BUILTIN, LOW);  
  }
}
