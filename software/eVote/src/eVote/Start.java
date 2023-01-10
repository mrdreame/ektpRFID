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

import control.Config;

/**
 *
 * @author dr
 */
public class Start {
    static SerialPort chosenPort;
    static JFrame window;
    
    public static void main(String[] args){
        window = new JFrame();
        window.setTitle("Select serial port");
        window.setSize(300, 200);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //create a drop-down box and connect button, and place them on top of the window
        JComboBox<String> portlist = new JComboBox<String>();
        JButton conn = new JButton("Connect");        
        conn.setEnabled(false);        
        JButton back = new JButton("Back");
        back.setEnabled(false);        
        JPanel topPanel = new JPanel();        
        topPanel.add(portlist);
        topPanel.add(conn);
        topPanel.add(back);                   
        
        JLabel labelA = new JLabel();                
        JPanel centerPanel = new JPanel();                        
        centerPanel.add(labelA);        

        JLabel labelB = new JLabel();
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(labelB);
        
        window.add(topPanel, BorderLayout.NORTH);
        window.add(centerPanel, BorderLayout.CENTER);
        window.add(bottomPanel, BorderLayout.SOUTH);
        
        //populate the drop-down box
        SerialPort[] ports = SerialPort.getCommPorts();        
        boolean check = false; //to disabling connection button
        if(ports.length == 0){
            labelB.setText("Check Device Connection");
            portlist.setEnabled(false);
        } else {
            for(int i = 0; i < ports.length; i++){
                portlist.addItem(ports[i].getSystemPortName());            
            }
            check = true;
        }
        
        Config conf = new Config();
        if(conf.check()){
            labelA.setText("Database OK");
            if(check){
                conn.setEnabled(true);
            }
        } else {
            labelA.setText("Check Database Connection");
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
                
                        Scan test = new Scan();
                        test.scan(chosenPort);
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
                Scan scan = new Scan();
                JFrame scanwindow = scan.window;
                scanwindow.setVisible(true);
                window.dispose();
            }
        });
        
        //show the window
        window.setVisible(true);
    }
}
