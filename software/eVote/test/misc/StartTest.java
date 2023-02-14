/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misc;

import com.fazecast.jSerialComm.SerialPort;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.Database;
import control.MD5;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author dr
 */
public class StartTest {
    static SerialPort chosenPort;
    static JFrame window;
    
    static Connection con;
    static ResultSet rs;
    static Statement stm;
    static String sql;
    
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
        
        Database conf = new Database();            
        
        if(conf.check()){
            labelA.setText("Database OK");
            if(check){
                conn.setEnabled(true);
            }
        } else {
            labelA.setText("Check Database Connection");
        }
        
        MD5 md5 = new MD5();
        JPanel panel = new JPanel();
        JLabel text = new JLabel("Enter Password: ");
        JPasswordField pass = new JPasswordField(10);
        
        panel.add(text);
        panel.add(pass);
        
        String[] opt = new String[]{"OK", "Cancel"};
        
        
        //configure the connect button and use another thread to listen for data
        conn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0){
                try{
                    sql = "SELECT * FROM peserta WHERE hadir='4'";
                    rs = stm.executeQuery(sql);
                    if(rs.next()){
                        String nik = rs.getString("nik");
                        int opts = JOptionPane.showOptionDialog(null, panel, "Admin Access", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opt, opt[1]);
                        if(opts == 0){
                            char[] password = pass.getPassword();
                            String passw = new String(password);
                            if(md5.getMD5(passw).equals(nik)){
                                if(portlist.getSelectedItem() != null){
                                    if(conn.getText().equals("Connect")){
                                        chosenPort = SerialPort.getCommPort(portlist.getSelectedItem().toString());
                                        conn.setText("Disconnect");
                                        portlist.setEnabled(false);
                                        back.setEnabled(true);
                                        
                                        test test = new test();
                                        test.scan(chosenPort);
                                        
                                        window.dispose();
                                    } else {
                                        chosenPort.closePort();
                                        back.setEnabled(false);
                                        portlist.setEnabled(true);
                                        conn.setText("Connect");
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Password Salah!");
                            }
                        }
                    }
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }                                       
        });
        
        back.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0){
                test scan = new test();
                JFrame scanwindow = scan.window;
                scanwindow.setVisible(true);
                window.dispose();
            }
        });
        
        //show the window
        window.setVisible(true);
    }
}
