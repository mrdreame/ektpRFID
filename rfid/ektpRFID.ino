#include <SPI.h>
#include <MFRC522.h>
#include <LiquidCrystal_I2C.h>

#define PIN_RST     7
#define PIN_SS      6 //PIN SDA / NSS

#define redLED      9
#define greLED      8

MFRC522 rfid(PIN_SS, PIN_RST); //initialize MFRC522 object
LiquidCrystal_I2C lcd(0x27, 16, 2); //initialize lcd object

byte nuidPICC[4]; //init array of NUID

String serialPrint ="";
String lcdPrint ="";

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600); //initialize serial comm to PC
  SPI.begin();        //init  SPI bus
  rfid.PCD_Init(); //init mfrc522
  rfid.PCD_DumpVersionToSerial();  // Show details of PCD - MFRC522 Card Reader details

  
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0,0);
  lcd.print("Scan PICC");
  lcd.setCursor(0,1);
  lcd.print("to see UID");

  pinMode(greLED, OUTPUT);
  pinMode(redLED, OUTPUT);

  digitalWrite(redLED, HIGH);
  digitalWrite(greLED, LOW);

  Serial.println(F("Scan PICC to see UID"));  
}

void loop() {
  // put your main code here, to run repeatedly:

  //new card check
  if(!rfid.PICC_IsNewCardPresent()){
    return;
  }

  //tag num unreceived
  if(!rfid.PICC_ReadCardSerial()){
    return;
  }

  if(rfid.uid.uidByte[0] != nuidPICC[0] ||
    rfid.uid.uidByte[1] != nuidPICC[1] ||
    rfid.uid.uidByte[2] != nuidPICC[2] ||
    rfid.uid.uidByte[3] != nuidPICC[3] ){
      Serial.println(F("A new card detected."));
        lcd.setCursor(0,0);
        lcd.clear();
        lcd.setCursor(0,1);
        lcd.clear();

      //Store NUID into nuidPICC array
      for (byte i = 0; i < 4; i++){
        nuidPICC[i] = rfid.uid.uidByte[i];
      }

      Serial.print(F("PICC type: "));
      MFRC522::PICC_Type piccType = rfid.PICC_GetType(rfid.uid.sak);
      Serial.println(rfid.PICC_GetTypeName(piccType));      

      serialPrint = hex(rfid.uid.uidByte, rfid.uid.size);
      lcdPrint = hexe(rfid.uid.uidByte, rfid.uid.size);
            
      Serial.print("UID: " + serialPrint);
      Serial.println();

      lcd.setCursor(0,0);
      lcd.print("UID:");
      lcd.setCursor(0,1);
      lcd.print(lcdPrint);

      digitalWrite(redLED, LOW);
      digitalWrite(greLED, HIGH);
      delay(100);
      digitalWrite(greLED, LOW);         
      delay(100);
      digitalWrite(greLED, HIGH);
      delay(100);
      digitalWrite(greLED, LOW);         
      delay(100);
      digitalWrite(greLED, HIGH);
      delay(100);
      digitalWrite(redLED, HIGH);
      digitalWrite(greLED, LOW);         
    } else {
      Serial.println(F("Card read previously."));
      
      lcd.clear();
      lcd.setCursor(0,0);      
      lcd.print("Card read");
      
      lcd.setCursor(1,1);      
      lcd.print("previously.");

      delay(1000);

      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("UID:");
      lcd.setCursor(0,1);
      lcd.print(lcdPrint);
    }

    rfid.PICC_HaltA(); //Halt PICC

    rfid.PCD_StopCrypto1(); //Stop encryption on PCD
}

String hex(byte *buffer, byte bufferSize){
  String idTag = "";
  char buff[3];
  for(byte i = 0; i < bufferSize; i++) {
    sprintf(buff, "%02X ", buffer[i]);
    idTag += buff;  
  } return idTag;
}

String hexe(byte *buffer, byte bufferSize){
  String idTag = "";
  char buff[3];
  for(byte i = 0; i < bufferSize; i++) {
    sprintf(buff, "%02X", buffer[i]);
    idTag += buff;  
  } return idTag;
}
