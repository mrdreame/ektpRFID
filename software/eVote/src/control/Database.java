/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author dr
 */
public class Database {
    public Connection con;
    public Statement stm;    
    
    String classname = "com.mysql.jdbc.Driver";
    String database = "evote";
    
    String url = "jdbc:mysql://localhost:3305/"+database;
    String user = "root";
    String pass = "1234";        
    
    public boolean check(){
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            return true;
        } catch(ClassNotFoundException | SQLException e){}        
        return false;
    }
    
    public ResultSet selectPeserta(String nuid){
        ResultSet rs = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT * FROM peserta WHERE uid = '"+ nuid +"'";
            rs = stm.executeQuery(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return rs;
    }
    
    public ResultSet selectPresensi(String nuid){
        ResultSet rs = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT uid FROM presensi WHERE uid = '"+ nuid +"'";
            rs = stm.executeQuery(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return rs;
    }
    
    public void insertPresensi(String nuid, String date){        
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "INSERT INTO `presensi`(`uid`, `time`) VALUES ('"+nuid+"','"+date+"')";
            stm.executeUpdate(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }        
    }
    
    public void updatePresensi(String nuid, String date){        
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "UPDATE `presensi` SET `time`='"+ date +"' WHERE `uid`='"+ nuid +"'";
            stm.executeUpdate(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }        
    }
    
    public void pemilu(String nuid, char opt){        
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "UPDATE `peserta` SET `hadir`='1' WHERE uid='"+ nuid +"'";
            stm.executeUpdate(sql);
            
            sql = "INSERT INTO `pemilu`(`pilihan`) VALUES ('"+opt+"')";
            stm.executeUpdate(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }        
    }
    
    public boolean checkPresensi(String nuid){        
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT 1 FROM presensi WHERE uid = '"+ nuid +"'";
            ResultSet rs = stm.executeQuery(sql);            
            if(rs.next()){
                return true;
            }
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return false;
    }
    
    public ResultSet adminSelectPeserta(){
        ResultSet rs = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT nama FROM peserta WHERE hadir='0' ORDER BY nama ASC";
            rs = stm.executeQuery(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return rs;
    }
    
    public ResultSet adminSumPemilu(char opt){
        ResultSet rs = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT SUM(pilihan = '"+ opt +"') FROM pemilu";
            rs = stm.executeQuery(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return rs;
    }
    
    public ResultSet adminCount(String count, String table){
        ResultSet rs = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT COUNT("+ count +") FROM "+ table;
            rs = stm.executeQuery(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return rs;
    }
    
    public ResultSet adminSumPeserta(String line){
        ResultSet rs = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT SUM(hadir "+ line +") FROM peserta";
            rs = stm.executeQuery(sql);            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return rs;
    }
    
    public String selectSuperuser(){
        String nik = null;
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "SELECT * FROM peserta WHERE hadir='4'";
            ResultSet rs = stm.executeQuery(sql);

            rs.next();
            nik = rs.getString("nik");
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }
        return nik;
    }
    
    public void restartPemilu(){        
        try{
            Class.forName(classname);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            
            String sql = "TRUNCATE pemilu";
            stm.executeUpdate(sql);
                                   
            sql = "TRUNCATE presensi";
            stm.executeUpdate(sql);
                
            sql = "UPDATE peserta SET hadir='0' WHERE hadir='1'";
            stm.executeUpdate(sql);             
            
        } catch(ClassNotFoundException | SQLException e){
            e.getMessage();
        }        
    }
}