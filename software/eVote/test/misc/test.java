/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misc;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

/**
 *
 * @author dr
 */
public class test {
    
    public static void main(String[] args){
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        if(serialPorts.length == 0){
            System.out.println("Check Device Connection.");
        } else {
            System.out.println("Port(s) Available:");
            for(int i = 0; i < serialPorts.length; i++){
                System.out.println(i+". "+serialPorts[i].getSystemPortName());
            }
        }
        System.out.print("Select port: ");
        Scanner scan = new Scanner(System.in);
        int a = scan.nextInt();
        
        SerialPort liveSerialPort = serialPorts[a];        
        liveSerialPort.openPort();
        while (liveSerialPort.isOpen()) {
            System.out.println("HERE opened port = " + liveSerialPort.getSystemPortName());                        
        }
    }
}
