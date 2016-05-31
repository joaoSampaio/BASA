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
#define DHTPIN D2     // what digital pin we're connected to
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
  
  server.on("/temp", [](){  // if you add this subdirectory to your webserver call, you get text below :)
    gettemperature();       // read sensor
    webString="Temperature: "+String((int)temp_f)+" F";   // Arduino has a hard time with float to string
    server.send(200, "text/plain", webString);            // send to someones browser when asked
  });

  server.on("/description.xml", [](){  // if you add this subdirectory to your webserver call, you get text below :)
    SSDP.schema(server.client());
  });

  server.on("/reset", [](){  // if you add this subdirectory to your webserver call, you get text below :)
    wifiManager.resetSettings();
  });

  server.on("/humidity", [](){  // if you add this subdirectory to your webserver call, you get text below :)
      gettemperature();           // read sensor
      webString="Humidity: "+String((int)humidity)+"%";
      server.send(200, "text/plain", webString);               // send to someones browser when asked
    });

  server.on("/data", action);

  server.on("/setserver", setServerBASA);

  
  server.begin();
  updateLights();

  Serial.printf("Starting SSDP...\n");
  SSDP.setSchemaURL("data");
  SSDP.setHTTPPort(80);
  SSDP.setName("Philips hue clone");
  SSDP.setSerialNumber("001788102201");
  SSDP.setURL("index.html");
  SSDP.setModelName("Philips hue bridge 2012");
  SSDP.setModelNumber("929000226503");
  SSDP.setModelURL("http://www.meethue.com");
  SSDP.setManufacturer("Royal Philips Electronics");
  SSDP.setManufacturerURL("http://www.philips.com");
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

    // send a reply, to the IP address and port that sent us the packet we received
//    Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
//    Udp.write(ReplyBuffer);
//    Udp.endPacket();
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
    
//    http.begin("http://192.168.137.180:5001/broadcast");
//    http.addHeader("Content-Type", "text/html");
//
//    String payload = "teste1";
//    int httpCode = http.POST(message);
//    Serial.println(message);
//    if (httpCode != 200) {
//      Serial.println("not successful");
//      } else {
//      String returnvalue = http.getString();
//      Serial.println("successful:" + returnvalue);   
//      }
//    http.end();

}

  
 
void loop(void)
{

  String input = Serial.readString();
  if (input.equals("liga")) {
        Serial.write("server -> Online\n");
        Serial.print("status ");
        Serial.println(WiFi.status());
        digitalWrite(BUILTIN_LED, LOW);
        Serial.write("\nLOW:");
        Serial.print(LOW);
        Serial.write("\nHIGH:");
        Serial.print(HIGH);
  }
  if (input.equals("on")) {       
        digitalWrite(BUILTIN_LED, HIGH);      
  }
  if (input.equals("off")) {     
        digitalWrite(BUILTIN_LED, LOW);
  }

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
        
            if(String(key) == "light1")
              light1 = it->value;

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

void updateLights(){

//led is reversed
  if(light1 == 1)
    digitalWrite(BUILTIN_LED, LOW);
  else
    digitalWrite(BUILTIN_LED, HIGH);
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





