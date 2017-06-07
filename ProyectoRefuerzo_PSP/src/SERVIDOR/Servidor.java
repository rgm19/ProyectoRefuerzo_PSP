/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVIDOR;

import java.net.ServerSocket;

/**
 *
 * @author ruben
 */
public class Servidor {
    public static void main (String[]args){
        System.out.println("+-----------------------------------------+");
        System.out.println("|-------      SERVIDOR PUBLICO     -------|");
        System.out.println("|-------           (V 1.0)         -------|");
        System.out.println("+-----------------------------------------+");

        int num=1;
        
        try(
                ServerSocket ss = new ServerSocket(15000);
            ){
                while(true){
                    HiloServidor hs = new HiloServidor(ss.accept(), num);
                    Thread hilo = new Thread(hs);
                    hilo.start();
                    num++;
                }
        }catch(Exception e){
            System.err.println("Error -> "+ e.getMessage());
        }
    }
}
