/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVIDOR;

import TRASPASO.Fichero;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import TRASPASO.Mensajes;

/**
 *
 * @author ruben
 */
public class HiloServidor implements Runnable{
    int ncli;
    Socket con;
    ObjectInputStream OIS;
    ObjectOutputStream OOS;
    FileInputStream FIS;
    FileOutputStream FOS;
    BufferedInputStream BIS;
    String suma =null;
    File fichero = new File("confidencial.txt");

    public HiloServidor(Socket con, int ncli) {
        this.ncli = ncli;
        this.con = con;
    }
    
    @Override
    public void run(){
        
        File ficheroDes = new File("confidencial_des"+ncli);
        try{
            if(!ficheroDes.exists()){
                fichero.createNewFile();
            }
        }catch(Exception e){
            System.out.println("Error -> "+e.getMessage());
        }    
        
        
        try(
                FileInputStream fis = new FileInputStream(fichero);
                ObjectInputStream ois = new ObjectInputStream(con.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(con.getOutputStream());        
                BufferedInputStream bis = new BufferedInputStream(fis);
            ){
        
            FIS=fis;
            OIS=ois;
            OOS=oos;
            BIS=bis;
            
            System.out.println("Nueva peticion llegada de cliente"+ncli);
            checkSum();
            System.out.println("El resultado de la suma es = "+suma);
            System.out.println("Cifrando fichero...");
            cifrarFichero();
            System.out.println("!Fichero cifrado!. Enviando...");
            mandarFile();
            System.out.println("!Fichero enviado!. Enviando suma... ");
            mandarSuma();
            System.out.println("!Suma enviada!. Enviando suma... ");
            System.out.println("***Procesos Finalizados***");
            
            
            
            
        }catch(Exception e){
            System.err.println("Error -> "+e.getMessage());
        }
    }
//------------------------------------------------------------------------------
    private void checkSum() {
        byte[] fileBytes = new byte[1024];
        byte[]sha =null;
        MessageDigest md = null;
        
        
        try{
            md = MessageDigest.getInstance("SHA-256");
            int read = 0;
            while((read = BIS.read(fileBytes)) > 0){
                md.update(fileBytes,0,read);
            }
            
            sha = md.digest();
            suma = Base64.getEncoder().encodeToString(sha);
            
        }catch(Exception ex){
            System.out.println("Error -> "+ex.getMessage());
        }
        
    }
//------------------------------------------------------------------------------    

    private void cifrarFichero() {
        String clave="password1234";
        KeySpec ks;
        SecretKeyFactory skf;
        Cipher cifrar;
        SecretKey clave_priv;
        byte[] encriptado = new byte[512];
        
        try(
               FileOutputStream fos = new FileOutputStream("confidencial.des"+ncli);
               FileInputStream fis = new FileInputStream("confidencial.txt") 
            
            )
        
        {
            byte [] bclaveprovisional = clave.getBytes("UTF8");
            byte [] bclave =copyOf(bclaveprovisional, 24);
            ks = new DESedeKeySpec(bclave);
            skf = SecretKeyFactory.getInstance("DESede");
            clave_priv = skf.generateSecret(ks);
            
            cifrar = Cipher.getInstance("DESede");
            cifrar.init(Cipher.ENCRYPT_MODE, clave_priv);
            CipherOutputStream cos = new CipherOutputStream(fos, cifrar);
            int aux=0;
            while((aux = fis.read(encriptado)) > 0){
                cos.write(encriptado,0,aux);
                cos.flush();
            }
            
        }catch(Exception e){
            System.out.println("Error -> "+e.getMessage());
        }
        
    }
//------------------------------------------------------------------------------
    private void mandarSuma() {
        Mensajes ms = new Mensajes();
        ms.setTexto(suma);
        
        //enviar la suma
        try{
            OOS.writeObject(ms);
            System.out.println("\t4.1.- CheckSum a mandar: " + ms.getTexto());
        }catch(Exception e){
            System.err.println("Error -> "+e.getMessage());
        }
    }
//------------------------------------------------------------------------------
    public void mandarFile(){
        int p=0;
        boolean enviadoUltimo=false;
        File file = new File("confidencial.des"+ncli);
        
        if(!file.exists()){
           System.err.println("No existe el fichero encriptado Copia!!!!!");
            System.exit(-1); 
        }
        
        try(
            FileInputStream fis = new FileInputStream(file);
            ){
            
            Fichero fichero = new Fichero();
            fichero.setNombre("confidencial.des"+ncli);
            int leidos=0;
            
            while((leidos=fis.read(fichero.getTrozo()))>0){
                
                fichero.setBytesValidos(leidos);
                if(leidos < Fichero.longitud){
                    fichero.setUltimo(true);
                    enviadoUltimo=true;
                }else{
                    fichero.setUltimo(false);
                }
                OOS.writeObject(fichero);
                if(fichero.isUltimo()){
                    break;
                }
                
                fichero = new Fichero();
                fichero.setNombre("confidencial.des"+ncli);
            }
        }catch(Exception e){}
        
        if(fichero.delete()){
            System.out.println("fichero borrado");
        }
    }
}
