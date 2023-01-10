/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

/**
 *
 * @author dr
 */
public class Config {
    public Connection con;
    public Statement stm;
    
    String classname = "com.mysql.jdbc.Driver";
    String database = "test";
    
    String url = "jdbc:mysql://localhost:3305/"+database;
    String user = "root";
    String pass = "1234";
    
    public void config(){
        try{
            Class.forName(classname);                        
            
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Connection interrupted: "+ e.getMessage(), "Database Connection Interrupted", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
    
    public boolean check(){
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            return true;
        } catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }        
        return false;
    }
}
