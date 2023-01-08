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
    
    public void config(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = "jdbc:mysql://localhost:3305/test";
            String user = "root";
            String pass = "1234";
            
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Connection interrupted: "+ e.getMessage(), "Database Connection Interrupted", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
}
