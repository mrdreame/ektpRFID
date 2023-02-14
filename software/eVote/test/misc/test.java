/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

import com.fazecast.jSerialComm.SerialPort;
import control.Database;
import control.MD5;
import eVote.EVote;
import eVote.Start;
import eVote.admin;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *
 * @author dr
 */
public class test {
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
        Database db = new Database();       
        
        //show the window
        window.setVisible(true);
    }
    
    //create and configure the window
    public static void initComponents(){
        window = new JFrame();               
        window.setSize(300,200);      
        window.setLayout(new BorderLayout(10, 10));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        
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
                PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
                
                if(chosenPort.isOpen()){
                    while(scanner.hasNextLine()){                    
                        try{                                    
                            String line = md5.getMD5(scanner.nextLine());                                                                                            
                            String inNIK = JOptionPane.showInputDialog("Input NIK");
                            String name = JOptionPane.showInputDialog("Input Nama");
                            
                            String nik = md5.getMD5(inNIK);                                                       
                             
                            output.print(name);
                            output.flush();
                                                            
                            try{
                                Thread.sleep(100);
                            }catch(Exception e){}                                                                                                                                                                                      
                        } catch(Exception e){            
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    } 
                } else {
                    JOptionPane.showMessageDialog(null, "Device Connection Interrupted.");
                    
                    StartTest start = new StartTest();
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
