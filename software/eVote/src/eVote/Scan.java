/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package eVote;

import com.fazecast.jSerialComm.SerialPort;

import control.Database;
import control.MD5;

import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;

import java.sql.ResultSet;


/**
 *
 * @author dr
 */
public class Scan {  
    static Database db = new Database();
    static Start start = new Start();
    
    static ResultSet rs;
    static JFrame window;
    /**
     * @param args the command line arguments
     */
    
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

        //show all components
        window.setVisible(true);
    }
    
    public static void scan(SerialPort chosenPort){
        //attempt to connect to the serial port        
        chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        chosenPort.openPort();  
        
        //establish connection to database
        
                    
        //create a new thread for sending data to arduino
        Thread thread = new Thread(){
            @Override public void run(){
                //delay after connecting,so bootloader can finish properly
                try{
                    Thread.sleep(1000);
                } catch(Exception e){}                
                
                MD5 md5 = new MD5();//encrypt every need before attempting to connect to database and to match the database                
                Scanner scanner = new Scanner(chosenPort.getInputStream());
                PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
                                
                if(chosenPort.isOpen()){
                    while(scanner.hasNextLine()){                                        
                        try{                                    
                            String line = md5.getMD5(scanner.nextLine());                                        
                        
                            java.util.Date dt = new java.util.Date();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = sdf.format(dt);
                                                        
                            rs = db.selectPeserta(line);                            
                            if(rs.next()){                                
                                String nik = rs.getString("nik");
                                String nama = rs.getString("nama");
                                int hadir = rs.getInt("hadir");                                                                                                                                
                                
                                if(hadir > 1){
                                    output.print("3" + nama);
                                    output.flush();
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
                                                new admin();
                                            } else if(hadir == 3){
                                                int a = JOptionPane.showConfirmDialog(null, "You're aboout to exit\nAre you sure?", "Program terminating", JOptionPane.WARNING_MESSAGE);
                                                if(a == JOptionPane.YES_OPTION){                                                                                                                                                           
                                                    start.window.setVisible(true);
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
                                    rs = db.selectPresensi(line);                                    
                                    if(!rs.next()){
                                        output.print("0" + nama);
                                        output.flush();
                                        db.insertPresensi(line, date);
                                                                
                                        new EVote(line);
                                        window.dispose();
                                    } else {
                                        output.print("1" + nama);
                                        output.flush();
                                        String nix = JOptionPane.showInputDialog(null, "Input NIK");
                                        if(md5.getMD5(nix).equals(nik)){                                                                                        
                                            db.updatePresensi(line, date);
                                            new EVote(line);
                                            window.dispose();
                                        } else {
                                            JOptionPane.showMessageDialog(null, "NIK salah\nHubungi petugas terdekat", "NIK Salah", JOptionPane.WARNING_MESSAGE);
                                        }                                     
                                    }
                                } else {
                                    output.print("2" + nama);
                                    output.flush();
                                    JOptionPane.showMessageDialog(null, "Hai, "+ nama +".\nAnda telah memilih, harap kembali", "Pemilihan Selesai", JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                output.print("null");
                                output.flush();
                                JOptionPane.showMessageDialog(null, "e-KTP tidak terdaftar\nSilakan kembali dan laporkan kepada petugas.", "e-KTP tidak terdaftar", JOptionPane.WARNING_MESSAGE);
                            }
                            try{Thread.sleep(100);} catch(Exception e){}
                        } catch(Exception e){            
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    } 
                } else {
                    JOptionPane.showMessageDialog(null, "Device Connection Interrupted.");                    
                    start.forcedReturn();

                    window.dispose();                                                               
                }
            } 
        };
        
        thread.start();           
        initComponents();
    }      
}