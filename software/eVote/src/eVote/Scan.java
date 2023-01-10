/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package eVote;

import com.fazecast.jSerialComm.SerialPort;

import control.Config;
import control.MD5;

import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JPasswordField;

/**
 *
 * @author dr
 */
public class Scan {
    static Connection con;
    static Statement stat;
    static ResultSet rs;
    static String sql;            
                
    static JFrame window;
    /**
     * @param args the command line arguments
     */
    public static void main() {
        // TODO code application logic here
        initComponents();
        
        //initiate connection to database
        Config db = new Config();
        db.config();
        con = db.con;
        stat = db.stm;
        
        //show the window
        window.setVisible(true);
    }
    
    //create and configure the window
    public static void initComponents(){
        window = new JFrame();               
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);        
        window.setLayout(new BorderLayout(10, 10));
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setUndecorated(true);
        
        //set text for information on top panel
        JLabel text = new JLabel("Tempelkan e-KTP pada perangkat");        
        text.setFont(new Font("Serif", Font.PLAIN, 50));
        JPanel APanel = new JPanel();                              
        APanel.add(text);                       
        
        JPanel BPanel = new JPanel();                
        
        //empty panel for layouting
        JPanel CPanel = new JPanel();
        CPanel.setPreferredSize(new Dimension(500, 500));
                
        //place all the panel created
        window.add(APanel, BorderLayout.CENTER);
        window.add(BPanel, BorderLayout.SOUTH);   
        window.add(CPanel, BorderLayout.NORTH);        
    }
    
    public static void scan(SerialPort chosenPort){
        //attempt to connect to the serial port        
        chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        chosenPort.openPort();                                                                   
                    
        //create a new thread for sending data to arduino
        Thread thread = new Thread(){
            @Override public void run(){
                //delay after connecting,so bootloader can finish properly
                try{
                    Thread.sleep(1000);
                } catch(Exception e){}                
                
                MD5 md5 = new MD5();                
                Scanner scanner = new Scanner(chosenPort.getInputStream());     
                
                if(chosenPort.isOpen()){
                    while(scanner.hasNextLine()){                    
                        try{                                    
                            String line = md5.getMD5(scanner.nextLine());                                        
                        
                            java.util.Date dt = new java.util.Date();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = sdf.format(dt);
                            
                            sql = "SELECT * FROM rfiddatabase WHERE uid='"+ line +"'";
                            rs = stat.executeQuery(sql);                            
                            if(rs.next()){                                
                                String nik = rs.getString("nik");
                                String nama = rs.getString("nama");
                                int hadir = rs.getInt("hadir");
                                if(hadir > 1){
                                    window.dispose();
                                    
                                    JPanel panel = new JPanel();
                                    JLabel label = new JLabel("Enter Password: ");
                                    JPasswordField pass = new JPasswordField(10);
                                    panel.add(label);
                                    panel.add(pass);
                                    
                                    String[] opt = new String[]{"Ok", "Cancel"};
                                    int opts = JOptionPane.showOptionDialog(null, panel, "Admin Access", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opt, opt[1]);
                                    if(opts == 0){
                                        char[] password = pass.getPassword();
                                        String passWord = new String(password);
                                        if(md5.getMD5(passWord).equals(nik)){
                                            if(hadir == 2){
                                                new admin().setVisible(true);
                                            } else if(hadir == 3){
                                                int a = JOptionPane.showConfirmDialog(null, "You're aboout to exit\nAre you sure?", "Program terminating", JOptionPane.WARNING_MESSAGE);
                                                if(a == JOptionPane.YES_OPTION){
                                                    Start start = new Start();
                                                    JFrame startwindow = start.window;
                                                    startwindow.setVisible(true);
                                                } else {
                                                    window.setVisible(true);
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Access Denied.");
                                                window.setVisible(true);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Password incorrect.\nAccess Denied.");
                                            window.setVisible(true);
                                        }
                                    } else {                                        
                                        window.setVisible(true);
                                    }
                                } else if(hadir == 0){
                                    sql = "SELECT uid FROM presensi WHERE uid='"+ line +"'";
                                    rs = stat.executeQuery(sql);
                                    
                                    if(!rs.next()){
                                        sql = "INSERT INTO `presensi`(`uid`, `time`) VALUES ('"+line+"','"+date+"')";                        
                                        stat.executeUpdate(sql);
                                                                
                                        new EVote().setVisible(true);
                                        window.dispose();
                                    } else {
                                        String nix = JOptionPane.showInputDialog(null, "Input NIK");
                                        if(md5.getMD5(nix).equals(nik)){                                            
                                            sql = "UPDATE `presensi` SET `time`='"+ date +"' WHERE `uid`='"+ line +"'";                        
                                            stat.executeUpdate(sql);
                                                                
                                            new EVote().setVisible(true);
                                            window.dispose();
                                        }                                        
                                    }
                                } else {                                    
                                    JOptionPane.showMessageDialog(null, "Hai, "+ nama +".\nAnda telah memilih, harap kembali");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "e-KTP tidak terdaftar");
                            }                                                                                           
                        } catch(Exception e){            
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    } 
                } else {
                    JOptionPane.showMessageDialog(null, "Device Connection Interrupted.");
                    
                    Start start = new Start();
                    JFrame startwindow = start.window;
                    startwindow.setVisible(true);

                    window.dispose();                                                               
                }
            } 
        };
        
        thread.start();           
        main();
    }      
}
