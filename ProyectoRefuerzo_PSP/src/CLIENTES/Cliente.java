/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENTES;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        
        try{
            ip =InetAddress.getByName(args[0]);
            
            puerto = Integer.parseInt(args[1]);
        }catch(Exception e){
            System.out.println("Error -> "+e.getMessage());
        }
        
        try(
            Socket sc = new Socket(ip, puerto);
            BufferedReader ENT = new BufferedReader(new InputStreamReader(sc.getInputStream()));    
            ){
            
            System.out.println("Archivo recibido del SERVIDOR");
            recibirArchivo();
            
            
            
            
            
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
