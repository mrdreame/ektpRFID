/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package arduinoproject;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.PrintWriter;

import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.Config;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
/**
 *
 * @author dr
 */
public class Scan {
    static Connection con;
    static Statement stat;
    static ResultSet rs;
    static String sql;
    
    static SerialPort chosenPort = SerialPort.getCommPort("COM3");
    static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
            
    static JFrame window;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //create and configure the window
        window = new JFrame();               
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);        
        window.setLayout(new BorderLayout(10, 10));
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setUndecorated(true);
        
        //set text for information on top panel
        JLabel text = new JLabel("\n\n\n\nTempelkan e-KTP pada perangkat");        
        text.setFont(new Font("Serif", Font.PLAIN, 50));
        JPanel APanel = new JPanel();                              
        APanel.add(text);        
        
        //dynamic text after scan card on center
        JLabel uid = new JLabel("uid: ");
        JLabel nuid = new JLabel();
        JPanel BPanel = new JPanel();
        BPanel.add(uid);
        BPanel.add(nuid);
        
        //empty panel for layouting
        JPanel CPanel = new JPanel();
        CPanel.setPreferredSize(new Dimension(500, 500));
                
        //place all the panel created
        window.add(APanel, BorderLayout.CENTER);
        window.add(BPanel, BorderLayout.SOUTH);   
        window.add(CPanel, BorderLayout.NORTH);
        
        //initiate connection to database
        Config db = new Config();
        db.config();
        con = db.con;
        stat = db.stm;
        
        //attempt to connect to the serial port        
        chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        if(chosenPort.openPort()){
            System.out.println("Port Connected: "+chosenPort);
        }else{
            JOptionPane.showMessageDialog(null, "Please check cable connection to the device, if connected it should emit led light", "Device Connection Interrupted", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }                                                   
                    
        //create a new thread for sending data to arduino
        Thread thread = new Thread(){
            @Override public void run(){
                //delay after connecting,so bootloader can finish properly
                try{
                    Thread.sleep(1000);
                } catch(Exception e){}                
                
                Scanner scanner = new Scanner(chosenPort.getInputStream());                                                                                                                                                                                                
                while(scanner.hasNextLine()){
                    try{                                    
                        String line = scanner.nextLine();        
                        nuid.setText(line);      
                        
                        java.util.Date dt = new java.util.Date();
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = sdf.format(dt);
                        
                        if(nuid.getText().equals("20091920") || nuid.getText().equals("11A7BE26")){
                            window.dispose();
                            String pass = JOptionPane.showInputDialog(null, "Input Admin Password", "Admin Access", JOptionPane.WARNING_MESSAGE);
                            
                            if(pass == null || (pass != null && ("".equals(pass)))){
                                window.setVisible(true);
                            } else {
                                if(nuid.getText().equals("20091920") && pass.equals("admin")){                                
                                    int a = JOptionPane.showConfirmDialog(null, "You're aboout to exit\nAre you sure?", "Program terminating", JOptionPane.WARNING_MESSAGE);
                                    if(a == JOptionPane.YES_OPTION){
                                        System.exit(0);
                                    } else {
                                        nuid.setText("");
                                        window.setVisible(true);
                                    }
                                } else if(nuid.getText().equals("11A7BE26") && pass.equals("admin")){
                                    new admin().setVisible(true);//------------------------------------------------------------------------------------------------
                                } else {
                                    JOptionPane.showMessageDialog(null, "Password salah");
                                    nuid.setText("");
                                    window.setVisible(true);
                                }                      
                            }
                        } else {
                            sql = "SELECT * FROM rfiddatabase WHERE uid='"+line+"'";
                            rs = stat.executeQuery(sql);
                        
                            if(rs.next()){                                
                                if(rs.getInt("hadir") == 0){
                                    sql = "INSERT INTO `presensi`(`uid`, `time`) VALUES ('"+line+"','"+date+"')";                        
                                    stat.executeUpdate(sql);
                        
                                    new EVote().setVisible(true);
                                    window.dispose();
                                } else {
                                    String nama = rs.getString("nama");
                                    JOptionPane.showMessageDialog(null, "Hai, "+ nama +".\nAnda telah memilih, harap kembali");
                                }  
                            }                                                                                                    
                        }                                                
                    } catch(Exception e){            
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }                                                                
            }
        };
        thread.start();                                 
        
        //show the window
        window.setVisible(true);
    }      
}
