/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DBKon;

import java.sql.Connection;
import java.sql.DriverManager;
//logo peta
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.net.URL;
import java.awt.Image;

/**
 *
 * @author asus
 */
public class Koneksi {
    public Connection con;
    public  Koneksi() {
       String id, pass, driver, url;
       id = "root";
       pass = "";
       driver = "com.mysql.cj.jdbc.Driver";
       url = "jdbc:mysql://localhost:3306/db_eventmanagement?useSSL=false&serverTimezone=UTC";
       
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
            e.printStackTrace();
        }
        
        
    }
    //ngeset iconnya jadi logo aplikasi kita
    public static void setAppIcon(JFrame frame) {
        try {
            URL iconURL = Koneksi.class.getResource("/DBKon/logoPeta.png"); 
            if (iconURL != null) {
                Image icon = new ImageIcon(iconURL).getImage();
                frame.setIconImage(icon);
            } else {
                System.out.println("Logo logoPeta.png tidak ditemukan di package DBKon!");
            }
        } catch (Exception e) {
            System.out.println("Gagal memasang icon: " + e.getMessage());
        }
    }
    
    public static void main (String args[]){
            Koneksi kon = new Koneksi();
        }
}