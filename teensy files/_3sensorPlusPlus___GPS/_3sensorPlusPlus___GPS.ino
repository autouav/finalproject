#include <TinyGPS++.h>
#include <SoftwareSerial.h>
/*
   This sample code demonstrates the normal use of a TinyGPS++ (TinyGPSPlus) object.
   It requires the use of SoftwareSerial, and assumes that you have a
   4800-baud serial GPS device hooked up on pins 4(rx) and 3(tx).
*/
static const int RXPin = 9, TXPin = 10;
static const uint32_t GPSBaud = 9600;

// The TinyGPS++ object
TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);

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

double latLocation = 0;
double lngLocation = 0;

void setup()
{
  Serial.begin(9600);
  ss.begin(GPSBaud);
  
  Serial1.begin(9600);
  pinMode(maxSensorPin[0], INPUT);
}

void loop()
{
  static const double LONDON_LAT = 51.508131, LONDON_LON = -0.128002;

  //printInt(gps.satellites.value(), gps.satellites.isValid(), 5);
  //printInt(gps.hdop.value(), gps.hdop.isValid(), 5);
  printFloat(gps.location.lat(), gps.location.isValid(), 11, 6);
  printFloatLng(gps.location.lng(), gps.location.isValid(), 12, 6);
  //printInt(gps.location.age(), gps.location.isValid(), 5);
  //printDateTime(gps.date, gps.time);
  //printFloat(gps.altitude.meters(), gps.altitude.isValid(), 7, 2);
  //printFloat(gps.course.deg(), gps.course.isValid(), 7, 2);
  //printFloat(gps.speed.kmph(), gps.speed.isValid(), 6, 2);
  //printStr(gps.course.isValid() ? TinyGPSPlus::cardinal(gps.course.value()) : "*** ", 6);

  unsigned long distanceKmToLondon =
    (unsigned long)TinyGPSPlus::distanceBetween(
      gps.location.lat(),
      gps.location.lng(),
      LONDON_LAT, 
      LONDON_LON) / 1000;
  //printInt(distanceKmToLondon, gps.location.isValid(), 9);

  double courseToLondon =
    TinyGPSPlus::courseTo(
      gps.location.lat(),
      gps.location.lng(),
      LONDON_LAT, 
      LONDON_LON);

  //printFloat(courseToLondon, gps.location.isValid(), 7, 2);

  const char *cardinalToLondon = TinyGPSPlus::cardinal(courseToLondon);

  //printStr(gps.location.isValid() ? cardinalToLondon : "*** ", 6);

  //printInt(gps.charsProcessed(), true, 6);
  //printInt(gps.sentencesWithFix(), true, 10);
  //printInt(gps.failedChecksum(), true, 9);
  //Serial.println();
  
  smartDelay(1000);

  if (millis() > 5000 && gps.charsProcessed() < 10)
    Serial.println(F("No GPS data received: check wiring"));
}

// This custom version of delay() ensures that the gps object
// is being "fed".
static void smartDelay(unsigned long ms)
{
  int count = 1;
  unsigned long start = millis();
  do 
  {
    while (ss.available())
      gps.encode(ss.read());
    if ((millis() - (start + count*20)) < ms) {
      sensors_loop();
      count++;
    }
  } while (millis() - start < ms);
}

static void printFloat(float val, bool valid, int len, int prec)
{
  if (!valid)
  {
    while (len-- > 1) {
      //Serial.print('*');
      //Serial1.print('*');
    }
    //Serial.print(' ');
    //Serial1.print(' ');
  }
  else
  {
    //Serial.print(val, prec);
    //Serial1.print(val, prec);
    latLocation = val;
    int vi = abs((int)val);
    int flen = prec + (val < 0.0 ? 2 : 1); // . and -
    flen += vi >= 1000 ? 4 : vi >= 100 ? 3 : vi >= 10 ? 2 : 1;
    //for (int i=flen; i<len; ++i)
      //Serial.print(' ');
      //Serial1.print(' ');
  }
  smartDelay(0);
}

static void printFloatLng(float val, bool valid, int len, int prec)
{
  if (!valid)
  {
    while (len-- > 1) {
      //Serial.print('*');
      //Serial1.print('*');
    }
    //Serial.print(' ');
    //Serial1.print(' ');
  }
  else
  {
    //Serial.print(val, prec);
    //Serial1.print(val, prec);
    lngLocation = val;
    int vi = abs((int)val);
    int flen = prec + (val < 0.0 ? 2 : 1); // . and -
    flen += vi >= 1000 ? 4 : vi >= 100 ? 3 : vi >= 10 ? 2 : 1;
    //for (int i=flen; i<len; ++i)
      //Serial.print(' ');
      //Serial1.print(' ');
  }
  smartDelay(0);
}

static void printInt(unsigned long val, bool valid, int len)
{
  char sz[32] = "*****************";
  if (valid)
    sprintf(sz, "%ld", val);
  sz[len] = 0;
  for (int i=strlen(sz); i<len; ++i)
    sz[i] = ' ';
  if (len > 0) 
    sz[len-1] = ' ';
  Serial.print(sz);
  Serial1.print(sz);
  smartDelay(0);
}

static void printDateTime(TinyGPSDate &d, TinyGPSTime &t)
{
  if (!d.isValid())
  {
    Serial.print(F("********** "));
  }
  else
  {
    char sz[32];
    sprintf(sz, "%02d/%02d/%02d ", d.month(), d.day(), d.year());
    Serial.print(sz);
  }
  
  if (!t.isValid())
  {
    Serial.print(F("******** "));
  }
  else
  {
    char sz[32];
    sprintf(sz, "%02d:%02d:%02d ", t.hour(), t.minute(), t.second());
    Serial.print(sz);
  }

  printInt(d.age(), d.isValid(), 5);
  smartDelay(0);
}

static void printStr(const char *str, int len)
{
  int slen = strlen(str);
  for (int i=0; i<len; ++i)
    Serial.print(i<slen ? str[i] : ' ');
  smartDelay(0);
}

void sensors_loop() {
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
 
 Serial1.print(latLocation,6);
 Serial.print(latLocation,6);
 
 Serial1.print(" ");
 Serial.print( " ");
 
 Serial1.print(lngLocation,6);
 Serial.print(lngLocation,6);
 
 Serial1.println();
 Serial.println();
}
