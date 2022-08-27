const unsigned int TRIG_PIN=13;
const unsigned int ECHO_PIN=12;
const unsigned int BAUD_RATE=9600;

void distance_detector(unsigned int TRIG_PIN, unsigned int ECHO_PIN){
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  

 const unsigned long duration= pulseIn(ECHO_PIN, HIGH);
 int distance= duration/29/2;
 if(duration==0){
   Serial.print("Warning: no pulse from sensor");
   } 
  else{
      Serial.print(duration);
      Serial.print(" s-sensor distance to nearest object:");
      Serial.print(distance);
      Serial.println(" cm");
  }
}

void setup() {
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  Serial.begin(BAUD_RATE);
}

void loop() {
  
  distance_detector(TRIG_PIN, ECHO_PIN);

  delay(200);
  
 }
