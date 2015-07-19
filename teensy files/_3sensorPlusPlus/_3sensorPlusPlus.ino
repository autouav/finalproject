
#define numOfImmSen  3
#define numOfMaxSen 1
#define sizeOfCount 10

// pin on Tennsy
const int sensorPin[] = {A0, A1, A2};
const int maxSensorPin[] = {3};

// Arrays to value
double ImmSenVal[numOfImmSen];
double MaxSenVal[numOfMaxSen];

// Arrays to average
double aveOfImmSen[numOfImmSen];
double aveOfMaxSen[numOfMaxSen];

void setup() {  
 Serial.begin(9600);
 Serial1.begin(9600);
 pinMode(maxSensorPin[0], INPUT);
}

void loop() {
 for (int i=0; i<numOfImmSen; i++) {
   ImmSenVal[i] = 0;
 }
 for (int i=0; i<numOfMaxSen; i++) {
   aveOfMaxSen[i] = 0;
 }
 
 for (int i=0; i<4; i++) {
   MaxSenVal[0] = pulseIn(maxSensorPin[0], HIGH);
   aveOfMaxSen[0] = aveOfMaxSen[0] + MaxSenVal[0];
 }
 aveOfMaxSen[0] = (aveOfMaxSen[0]/147/4)*2.54;
 
 for (int i=0; i<sizeOfCount; i++) {
   for (int j=0; j<numOfImmSen; j++) {
     ImmSenVal[j] = analogRead(sensorPin[j] ); 
     aveOfImmSen[j] = aveOfImmSen[j] + ImmSenVal[j];
   }
 }
 
 for (int i=0; i<numOfImmSen; i++) {
   aveOfImmSen[i] = aveOfImmSen[i] / sizeOfCount;
 }
 
 for (int i=0; i<numOfImmSen; i++) {
     Serial1.print((int)aveOfImmSen[i]);
     Serial1.print(",");
     Serial.print((int)aveOfImmSen[i]);
     Serial.print(",\t");
 }
 for (int i=0; i<numOfMaxSen; i++) {
   Serial.print((int)aveOfMaxSen[i]);
   Serial.print(",\t");
   Serial1.print((int)aveOfMaxSen[i]);
   Serial1.print(",");
 }
 Serial1.println();
 Serial.println();
 
 delay(20);
}
