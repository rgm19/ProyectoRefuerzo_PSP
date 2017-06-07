/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENTES;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author ruben
 */
public class Cliente {
    public static void main(String[]args){
        InetAddress ip =null;
        String cadena;
        int puerto=0;
        File file = new File("confidencial.des");
        
        try{
            ip =InetAddress.getByName(args[0]);
            
            puerto = Integer.parseInt(args[1]);
            
            
            if(!file.exists()){
                file.createNewFile();
            }
        }catch(Exception e){
            System.out.println("Error -> "+e.getMessage());
        }
        
        try(
            Socket sc = new Socket(ip, puerto);
            BufferedReader ENT = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
            FileOutputStream fos = new FileOutputStream(file)    
            ){
            
            System.out.println("Archivo recibido del SERVIDOR");
            recibirArchivo(); //recibo archivo cifrado
            
            //descifro el archivo recibido
            
            
            
            
            
            while(true){
                cadena=ENT.readLine();
                if(cadena.equals("exit") || cadena.equals("quit")){
                    System.out.println("Saliendo del servidor...");
                    System.exit(0);
                }
                
            }
        }catch(Exception e){
            System.out.println("Error -> "+e.getMessage());
        }
    }
//------------------------------------------------------------------------------
    private static void recibirArchivo() {
        
    }
}
