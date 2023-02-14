#include <SPI.h>
#include <MFRC522.h>
#include <LiquidCrystal.h>

#define SS 10
#define RST 9

const int rs = 2, en = 3, d4 = 4, d5 = 5, d6 = 6, d7 = 7;

const int gre = A2, yel = A1, red = A0;

MFRC522 rfid(SS, RST);
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

byte nuidPICC[4];
String nuid;

void setup() {
  // put your setup code here, to run once:
  SPI.begin();
  Serial.begin(9600);
  rfid.PCD_Init();
  lcd.begin(16, 2);
  pinMode(gre, OUTPUT);
  pinMode(yel, OUTPUT);
  pinMode(red, OUTPUT);       
}

void loop() {
  // put your main code here, to run repeatedly:  
  if(!rfid.PICC_IsNewCardPresent()){
    lcdPrint(" Awaiting", "  Scan e-KTP");
    return;
  }    

  if(!rfid.PICC_ReadCardSerial())        
    return;  

  if(rfid.uid.uidByte[0] != nuidPICC[0] ||
    rfid.uid.uidByte[1] != nuidPICC[1] ||
    rfid.uid.uidByte[2] != nuidPICC[2] ||
    rfid.uid.uidByte[3] != nuidPICC[3] ){                      
      //Store NUID into nuidPICC array
      for (byte i = 0; i < 4; i++){
        nuidPICC[i] = rfid.uid.uidByte[i];
      }
      nuid = hex(rfid.uid.uidByte, rfid.uid.size);
      Serial.println(nuid);
      
      String txt = Serial.readString();
      String code = txt.substring(0, 1);      
      String nama = txt.substring(1);
            
      if(code == "0"){
        lcdPrint("Silakan,", nama);   
        blip(gre, 3);                    
      } else if(code == "1"){
        lcdPrint("Presensi Ulang", nama);
        blip(yel, 3);
      } else if(code == "2"){
        lcdPrint(nama, "Telah memilih");
        blip(red, 3);
      } else if(code == "3"){
        lcdPrint("     "+nama, "     Access");
        blip(gre, 3);
        blip(yel, 3);
        blip(red, 3);
      } else {
        lcdPrint("e-KTP", "Tidak terdaftar");
        blip(red, 3);
      }
    } else {
      lcdPrint("e-KTP", "telah dibaca");
      blip(red, 3);
    }

    rfid.PICC_HaltA();
    rfid.PCD_StopCrypto1();

    delay(1000);            
}

String hex(byte *buffer, byte bufferSize){
  String tag;
  char buff[3];
  for(byte i = 0; i < bufferSize; i++){
    sprintf(buff, "%02X", buffer[i]);
    tag += buff;
  } return tag;
}

void blip(int blip, int repeat){
  for(int i = 0; i < repeat; i++){
    digitalWrite(blip, HIGH);
    delay(1000);
    digitalWrite(blip, LOW);
    delay(1000);
  }
}

void lcdPrint(String line1, String line2){   
  lcd.clear();

  lcd.setCursor(0, 0);
  lcd.print(line1);
  
  lcd.setCursor(0, 1);
  lcd.print(line2);
}
