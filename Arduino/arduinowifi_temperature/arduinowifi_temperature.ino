/* DHTServer - ESP8266 Webserver with a DHT sensor as an input
 
   Based on ESP8266Webserver, DHTexample, and BlinkWithoutDelay (thank you)
 
   Version 1.0  5/3/2014  Version 1.0   Mike Barela for Adafruit Industries
*/
//#include <ESP8266WiFi.h>
//#include <WiFiClient.h>
//#include <ESP8266WebServer.h>
//#include <DHT.h>
//#define DHTTYPE DHT22
//#define DHTPIN  2
#include <ESP8266SSDP.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <pins_arduino.h>
#include <DHT.h>
 
const char* ssid     = "Connectify-tese";
const char* password = "poiuyt54321";
#define DHTPIN D2     // what digital pin we're connected to
#define DHTTYPE DHT11   // DHT 11
// the IP address for the shield:
//IPAddress ip(192, 168, 0, 200); 
IPAddress ip(192, 168, 215, 200); 
IPAddress gateway(192,215,0,1);
//IPAddress gateway(192,168,0,1);
IPAddress subnet(255,255,255,0); 
ESP8266WebServer server(80);

DHT dht(DHTPIN, DHTTYPE);
 
float humidity, temp_f;  // Values read from sensor
String webString="";     // String to display
// Generally, you should use "unsigned long" for variables that hold time
unsigned long previousMillis = 0;        // will store last temp was read
const long interval = 2000;              // interval at which to read sensor

int light1, light2;
 
void handle_root() {
  server.send(200, "text/plain", "Hello from the weather esp8266, read from /temp or /humidity");
  delay(100);
}
 
void setup(void)
{

//  pinMode(DHTPIN, OUTPUT);
//  pinMode(BUILTIN_LED, OUTPUT);  // initialize onboard LED as output
  pinMode(BUILTIN_LED, OUTPUT);  // initialize onboard LED as output
  
  // You can open the Arduino IDE Serial Monitor window to see what the code is doing
  Serial.begin(9600);  // Serial connection from ESP-01 via 3.3v console cable
//  dht.begin();           // initialize temperature sensor

  WiFi.disconnect(true);
  delay(500);
  // Connect to WiFi network
  WiFi.begin(ssid, password);
  WiFi.config(ip, gateway, subnet);
 
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("");
    Serial.print("status ");
    Serial.println(WiFi.status());
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("DHT Weather Reading Server");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
   
  server.on("/", home);
  
  server.on("/temp", [](){  // if you add this subdirectory to your webserver call, you get text below :)
    gettemperature();       // read sensor
    webString="Temperature: "+String((int)temp_f)+" F";   // Arduino has a hard time with float to string
    server.send(200, "text/plain", webString);            // send to someones browser when asked
  });
 
  server.on("/humidity", [](){  // if you add this subdirectory to your webserver call, you get text below :)
    gettemperature();           // read sensor
    webString="Humidity: "+String((int)humidity)+"%";
    server.send(200, "text/plain", webString);               // send to someones browser when asked
  });


  server.on("/action", [](){  // if you add this subdirectory to your webserver call, you get text below :)
    
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
      message += "{"+String(" \"light1\":") + light1 + String(", \"light2\":") + light2 +  String(", \"temperature\":") + temp_f+"}";
      
      server.send(200, "application/javascript", message);
      
     
  });
  
  server.begin();
  updateLights();





    Serial.printf("Starting SSDP...\n");
    SSDP.setSchemaURL("description.xml");
    SSDP.setHTTPPort(80);
    SSDP.setName("Philips hue clone");
    SSDP.setSerialNumber("001788102201");
    SSDP.setURL("index.html");
    SSDP.setModelName("Philips hue bridge 2012");
    SSDP.setModelNumber("929000226503");
    SSDP.setModelURL("http://www.meethue.com");
    SSDP.setManufacturer("Royal Philips Electronics");
    SSDP.setManufacturerURL("http://www.philips.com");
    SSDP.begin();

    Serial.printf("Ready!\n");






  
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
} 


void home(){

//  server.sendHeader("Content-Type", "text/html");
  server.sendHeader("Connection", "close");
//  server.sendHeader("Refresh", "5");
  String message = "";

  gettemperature();
  
  message += "<body>";
  message += "";
  message += "<table style=\"width:100%\">";
  message += "  <tr>";
  message += "    <td>Light 1</td>";
  message += "    <td>Light 2</td>    ";
  message += "    <td>Temperature</td>";
  message += "  </tr>";
  message += "  <tr>";
  message += "    <td>"+ String(light1) +"</td>";
  message += "    <td>"+ String(light2) +"</td>   ";
  message += "    <td>"+ String((int)temp_f) +"</td>";
  message += "  </tr>";
  message += "  ";
  message += "</table>";
  message += "";
  message += "<br>";
  message += "Send a json post to \/action";
  message += "<br>";
  message += "example {\"light1\": 0, \"light2\": 1}";
  message += "</body>";
  
  
  message += "</html>\n";


  server.send(200, "text/html", message);


  
}

void updateLights(){

//led is reversed
  if(light1 == 1)
    digitalWrite(BUILTIN_LED, LOW);
  else
    digitalWrite(BUILTIN_LED, HIGH);

  
}

 
void gettemperature() {

//  humidity = 25;
//  temp_f = 20;
  
  // Wait at least 2 seconds seconds between measurements.
  // if the difference between the current time and last time you read
  // the sensor is bigger than the interval you set, read the sensor
  // Works better than delay for things happening elsewhere also
  unsigned long currentMillis = millis();
// 
  if(currentMillis - previousMillis >= interval) {
    // save the last time you read the sensor 
    previousMillis = currentMillis;   


    // Reading temperature for humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (it's a very slow sensor)
    humidity = dht.readHumidity();          // Read humidity (percent)
    temp_f = dht.readTemperature();     // Read temperature as Fahrenheit
    Serial.print("temp: ");
    Serial.println(temp_f);
    Serial.print("Humidity: ");
    Serial.println(humidity);
    // Check if any reads failed and exit early (to try again).
    if (isnan(humidity) || isnan(temp_f)) {
      Serial.println("Failed to read from DHT sensor!");
      return;
    }
  }
}





