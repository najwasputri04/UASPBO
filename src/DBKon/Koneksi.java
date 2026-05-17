/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DBKon;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author asus
 */
public class Koneksi {
    Connection con;
    public  Koneksi() {
       String id, pass, driver, url;
       id = "root";
       pass = "";
       driver = "com.mysql.cj.jdbc.Driver";
       url = "jdbc:mysql://localhost:3306/db_eventmanagement?userTimezone=true&server=UTC";
       
        try {
            Class.forName(driver).newInstance();
            con =DriverManager.getConnection(url, id, pass);
            if (con==null){
                System.out.println("Koneksi gagal");
            }
            else{
                System.out.println("Koneksi berhasil");
            }
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        
    }
    
    public static void main (String args[]){
            Koneksi kon = new Koneksi();
        }
}