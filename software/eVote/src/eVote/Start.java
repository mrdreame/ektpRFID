/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eVote;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.Database;

/**
 *
 * @author dr
 */
public class Start {
    static Scan scan = new Scan();
    static Database db = new Database();
    static SerialPort chosenPort;
    static SerialPort[] ports;
    
    static JFrame window;
    static JComboBox<String> portlist;
    static JButton conn, back;
    static JLabel labelA, labelB;  
    
    public void forcedReturn(){        
        conn.setText("Connect");
        back.setEnabled(false);
        portlist.setEnabled(true);
        
        serialCheck(ports);
        dbCheck(true);
        
        window.setVisible(true);
    }   
    
    public static boolean serialCheck(SerialPort[] ports){
        boolean check = false;
        if(ports.length == 0){
            labelB.setText("Check Device Connection");
            portlist.setEnabled(false);
        } else {
            labelB.setText("Device OK");
            check = true;
        } return check;
    }
     
    public static boolean dbCheck(boolean check){  
        boolean dbcheck = false;
        if(db.check()){
            labelA.setText("Database OK");            
            if(check){
                dbcheck = true;                
            }
        } else {
            labelA.setText("Check Database Connection");            
        } return dbcheck;
    }
    
    public static void start(){
        window = new JFrame();
        window.setTitle("Select serial port");
        window.setSize(300, 200);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //create a drop-down box and connect button, and place them on top of the window
        portlist = new JComboBox<String>();
        conn = new JButton("Connect");        
        conn.setEnabled(false);        
        back = new JButton("Back");
        back.setEnabled(false);        
        JPanel topPanel = new JPanel();        
        topPanel.add(portlist);
        topPanel.add(conn);
        topPanel.add(back);                   
        
        labelA = new JLabel();                
        JPanel centerPanel = new JPanel();                        
        centerPanel.add(labelA);        

        labelB = new JLabel();
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(labelB);
        
        window.add(topPanel, BorderLayout.NORTH);
        window.add(centerPanel, BorderLayout.CENTER);
        window.add(bottomPanel, BorderLayout.SOUTH);
        
        //populate the drop-down box
        ports = SerialPort.getCommPorts();
        if(serialCheck(ports)){
            for(int i = 0; i < ports.length; i++){
                portlist.addItem(ports[i].getSystemPortName());            
            }           
        }    
        
        if(dbCheck(serialCheck(ports))){
            conn.setEnabled(true);
        }
        
        //configure the connect button and use another thread to listen for data
        conn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0){
                if(portlist.getSelectedItem() != null){
                    if(conn.getText().equals("Connect")){
                        //attempt to connect
                        chosenPort = SerialPort.getCommPort(portlist.getSelectedItem().toString());
                        conn.setText("Disconnect");
                        portlist.setEnabled(false);
                        back.setEnabled(true);
                        labelB.setText("Device Connection OK");
                                        
                        scan.scan(chosenPort);
                        window.dispose();
                    } else {
                        //disconnect from the serial port
                        chosenPort.closePort();
                        back.setEnabled(false);
                        portlist.setEnabled(true);
                        conn.setText("Connect");
                    }
                }
            }
        });
        
        back.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0){                                
                scan.window.setVisible(true);
                window.dispose();
            }
        });                  
    }
    
     
    public static void main(String[] args){
         start();
         window.setVisible(true);
    }
}