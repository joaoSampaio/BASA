/* DHTServer - ESP8266 Webserver with a DHT sensor as an input
 
   Based on ESP8266Webserver, DHTexample, and BlinkWithoutDelay (thank you)
 
   Version 1.0  5/3/2014  Version 1.0   Mike Barela for Adafruit Industries
*/
//#include <DHT.h>
//#define DHTTYPE DHT22
//#define DHTPIN  2
#include <WiFiManager.h>
#include <DNSServer.h>   
#include "lib/ESP8266SSDP2.h"
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <pins_arduino.h>
#include <DHT.h>
//#include <ESP8266HTTPClient.h>
#include <ESP8266HTTPClient.h>

char packetBuffer[255]; //buffer to hold incoming packet
WiFiUDP Udp;
HTTPClient http;

#define CH1 D8     // what digital pin we're connected to
#define CH2 D7     // what digital pin we're connected to
#define CH3 D6     // what digital pin we're connected to
#define CH4 D5     // what digital pin we're connected to


#define DHTPIN D4     // what digital pin we're connected to
#define DHTTYPE DHT11   // DHT 11
ESP8266WebServer server(80);
WiFiManager wifiManager;
WiFiClient client;
DHT dht(DHTPIN, DHTTYPE);

String serverUrls[10] = {};

float humidity, temp_f;  // Values read from sensor
String webString="";     // String to display
unsigned long previousMillis = 0;        // will store last temp was read
const long interval = 2000;              // interval at which to read sensor

int light1, light2;
 
void handle_root() {
  server.send(200, "text/plain", "Hello from the weather esp8266, read from /temp or /humidity");
  delay(100);
}
 
void setup(void)
{

  pinMode(CH1, OUTPUT);
  pinMode(CH2, OUTPUT);
  pinMode(CH3, OUTPUT);
  pinMode(CH4, OUTPUT);

  humidity = 50;
  temp_f = 20;
  pinMode(BUILTIN_LED, OUTPUT);  // initialize onboard LED as output
  
  // You can open the Arduino IDE Serial Monitor window to see what the code is doing
  Serial.begin(9600);  // Serial connection from ESP-01 via 3.3v console cable
  dht.begin();           // initialize temperature sensor
//  wifiManager.resetSettings();
  wifiManager.autoConnect("AutoConnectAP");

  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
   
  server.on("/", home);
  
  server.on("/description.xml", [](){ 
    SSDP.schema(server.client());
  });

  server.on("/reset", [](){  
    wifiManager.resetSettings();
  });

  server.on("/data", action);

  server.on("/setserver", setServerBASA);

  
  server.begin();
  updateLights();

  Serial.printf("Starting SSDP...\n");
  SSDP.setSchemaURL("data");
  SSDP.setHTTPPort(80);
  SSDP.setName("Arduino HVAC");
  SSDP.setSerialNumber("001788102201");
  SSDP.setURL("index.html");
  SSDP.setModelName("Arduino HVAC 2016");
  SSDP.setModelNumber("929000226503");
  SSDP.setModelURL("https://github.com/joaoSampaio/BASA");
  SSDP.setManufacturer("");
  SSDP.setManufacturerURL(");
  SSDP.setUUID("11111111-fca6-4070-85f4-1fbfb9add62c");
  SSDP.setDeviceType("urn:schemas-basa-pt:service:climate:1");
  SSDP.begin();

  Udp.begin(8089);

  Serial.printf("Ready!\n");
}




void receiveUDPBroadcast(){
  
  int packetSize = Udp.parsePacket();
  if (packetSize) {
    Serial.print("Received packet of size ");
    Serial.println(packetSize);
    Serial.print("From ");
    IPAddress remoteIp = Udp.remoteIP();
    Serial.print(remoteIp);
    Serial.print(", port ");
    Serial.println(Udp.remotePort());

    // read the packet into packetBufffer
    int len = Udp.read(packetBuffer, 255);
    if (len > 0) {
      packetBuffer[len] = 0;
    }
    Serial.println("Contents:");
    Serial.println(packetBuffer);
    
    Serial.println("ContentsHEX:");
    String hexValue = "";
    for(int i = 0; i< len; i++){
      Serial.print(String(packetBuffer[i], (unsigned char)HEX));
      hexValue = hexValue + String(packetBuffer[i], (unsigned char)HEX);
    }
    String s = String(packetBuffer);
    retransmitUDPToServer(hexValue);

  }
}



void retransmitUDPToServer(String message){
    //

    for(int i = 0; i< 10 ; i++){
      if(serverUrls[i] != NULL){
        Serial.println(serverUrls[i]);        
        http.begin(serverUrls[i]);
        http.addHeader("Content-Type", "text/html");
    
        String payload = "teste1";
        int httpCode = http.POST(message);
        Serial.println(message);
        if (httpCode != 200) {
          Serial.println("not successful");
          } else {
          String returnvalue = http.getString();
          Serial.println("successful:" + returnvalue);   
          }
        http.end();

      }
    }
}

  
void loop(void)
{
  server.handleClient();
  gettemperature();
  receiveUDPBroadcast();
} 


void action(){
      String message = "";
      if(server.args() == 1 ){
        StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.parseObject(server.arg(0));

        for(JsonObject::iterator it=root.begin(); it!=root.end(); ++it) 
        {
            // *it contains the key/value pair
            const char* key = it->key;
        
            // it->value contains the JsonVariant which can be casted as usual
            const char* value = it->value;

            int pinSelected = -1;
            Serial.print("key: " + String(key));

            String mKey = String(key) ;
            if(mKey == "ch1"){
              pinSelected = CH1;
            }else if(mKey == "ch2"){
              pinSelected = CH2;
            } else if(mKey == "ch3"){
              pinSelected = CH3;
            } else if(mKey == "ch4"){
              pinSelected = CH4;
            }
           
           
            if(pinSelected >= 0 ){
              if(String(value) == "1"){
                digitalWrite(pinSelected, HIGH);
              }
              else{
                digitalWrite(pinSelected, LOW);
              }              
            }

            if(String(key) == "light2")
              light2 = it->value;
        
        }       
        updateLights();    
//        int light1    = root["light1"];       
      }
      message += "{" + String("\"humidity\":") + humidity +  String(", \"temperature\":") + temp_f+"}";
      
      server.send(200, "application/javascript", message);    
}
//preciso de guardar offline
void setServerBASA(){
      Serial.println("setServerBASA");
      String message = "ok";
      if(server.args() == 1 ){
        StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.parseObject(server.arg(0));
        String url;
        for(JsonObject::iterator it=root.begin(); it!=root.end(); ++it) 
        {
            // *it contains the key/value pair
            const char* key = it->key;        
            const char* value = it->value;
            if(String(key) == "server")
              url = String(value);
        }       

        for(int i = 0; i< 10 ; i++){
          if(serverUrls[i] == NULL){
            serverUrls[i] = url;
            Serial.println("saving url");
            Serial.println(serverUrls[i]);
            break;
          }
          //se ja existir o url
          if(serverUrls[i].equals(url)){
            Serial.println("Already knew url");
            break;
          }

          Serial.println(i);
        }
      
      server.send(200, "application/javascript", message);    
    }
}


void home(){
  SSDP.schema(server.client());
}

 
void gettemperature() {

  unsigned long currentMillis = millis();
// 
  if(currentMillis - previousMillis >= interval) {
    // save the last time you read the sensor 
    previousMillis = currentMillis;   

    float humidity_temp, temperature_temp;


    // Reading temperature for humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (it's a very slow sensor)
    humidity_temp = dht.readHumidity();          // Read humidity (percent)
    temperature_temp = dht.readTemperature();     // Read temperature as Fahrenheit
    
    // Check if any reads failed and exit early (to try again).
    if(!isnan(humidity_temp))
      humidity = humidity_temp;

    if(!isnan(temperature_temp))
      temp_f = temperature_temp;
    
    if (isnan(humidity) || isnan(temp_f)) {
      //Serial.println("Failed to read from DHT sensor!");
      return;
    }
  }
}





