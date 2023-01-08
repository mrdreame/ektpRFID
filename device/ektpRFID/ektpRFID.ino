#include <SPI.h>
#include <MFRC522.h>
#include <LiquidCrystal.h>

//MFRC522 PIN
#define SDA     10
#define RST     9

//LED PIN
#define greLED  6
#define redLED  7

//Initialize both MFRC522(RFID)
MFRC522 rfid(SDA, RST);

byte nuidPICC[4]; //init array of NUID

String nuID ="";

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600); //initialize serial comm to PC
  Serial.setTimeout(50);
  SPI.begin();        //init  SPI bus
  rfid.PCD_Init(); //init mfrc522
  //rfid.PCD_DumpVersionToSerial();  // Show details of PCD - MFRC522 Card Reader details
  
  pinMode(greLED, OUTPUT);
  pinMode(redLED, OUTPUT);

  digitalWrite(redLED, HIGH);
  digitalWrite(greLED, LOW);
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
      //Store NUID into nuidPICC array
      for (byte i = 0; i < 4; i++){
        nuidPICC[i] = rfid.uid.uidByte[i];
      }  

      nuID = hexa(rfid.uid.uidByte, rfid.uid.size);
      Serial.println(nuID);   
      blinkLED();      
    }
    
    rfid.PICC_HaltA(); //Halt PICC
    rfid.PCD_StopCrypto1(); //Stop encryption on PCD
}

void blinkLED(){
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
      digitalWrite(greLED, LOW);
      digitalWrite(redLED, HIGH);        
}

String hexa(byte *buffer, byte bufferSize){
  String idTag = "";
  char buff[3];
  for(byte i = 0; i < bufferSize; i++) {
    sprintf(buff, "%02X", buffer[i]);
    idTag += buff;  
  } return idTag;
}
