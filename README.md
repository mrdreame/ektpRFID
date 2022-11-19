# ektpRFID
an attempt on e-voting

v1 based on arduino board (atleast on my side)
Project contain: 
1. Arduino Board (UNO personally)

~~2. Ethernet Shield (for connecting to localhost, if unneeded for u, sure)~~
3. MFRC522 (RFID-RC522 for RFID, duh)
4. some LED, one red and one green
5. LCD (connect it to I2C or makedo if u can)

side note if u use Ethernet Shield (W5100 for me personally)
since W5100 use port 10 (idk, just found it on the community and it kinda stressed me out before finding this solution)
change your pin for RST and SDA (since this is interchangeable)
to any pin you want

for this i use pin 6 and 7 for necessary RFID port
and pin 8 and 9 for led needs.
