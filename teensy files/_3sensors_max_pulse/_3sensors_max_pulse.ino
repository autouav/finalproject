
#define numOfSen  3
#define Dis 200
#define countMaxDanger 10
#define danger 8

const int sensorPin[] = {A0, A1, A2};
const int maxSensor = 3;
int sensorVal[numOfSen];
int matrixD[numOfSen][countMaxDanger];
long distance1, sensor1;

void setup() {  
 Serial.begin(9600);
 Serial1.begin(9600);
 pinMode(maxSensor, INPUT);
}

int sumDanger(int t) {
 int sum = 0;
 for (int i=0; i<countMaxDanger; i++) {
   if (matrixD[t][i] == 1) sum++;
 }
 return sum;
}

void loop() {
 //Serial.println("hello");
 sensor1 = 0;
 for (int i=0; i<4; i++) {
   distance1 = pulseIn(maxSensor, HIGH);
   sensor1 = sensor1 + distance1;
 }
 sensor1 = (sensor1/147/4)*2.54;
 
 for (int i=0; i<countMaxDanger; i++) {
   for (int j=0; j<numOfSen; j++) {
     sensorVal[j] = analogRead(sensorPin[j]); 
     if (sensorVal[j] > Dis) matrixD[j][i] = 1;
     else matrixD[j][i] = 0; 
   }
 }
 for (int i=0; i<numOfSen; i++) {
   if (sumDanger(i) >= danger) {
     Serial1.print("1");
     Serial1.print(",");
     Serial.print("1");
     Serial.print(",");
   }
   else {
     Serial1.print("0");
     Serial1.print(",");
     Serial.print("0");
     Serial.print(",");
   }
 }
 Serial.print(sensor1);
 Serial1.print(sensor1);
 Serial1.println();
 Serial.println();
 
 delay(20);
}
