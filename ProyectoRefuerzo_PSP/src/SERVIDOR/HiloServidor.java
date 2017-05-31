/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVIDOR;

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
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 *
 * @author ruben
 */
public class HiloServidor implements Serializable{
    int ncli;
    Socket con;
    ObjectInputStream OIS;
    ObjectOutputStream OOS;
    FileInputStream FIS;
    FileOutputStream FOS;
    BufferedInputStream BIS;
    String suma =null;
    

    public HiloServidor(Socket con, int ncli) {
        this.ncli = ncli;
        this.con = con;
    }
    
    public void run() throws IOException{
        File fichero = new File("confidencial.txt");
        File fichero_des = new File("confidencial_des"+ncli);
        if(!fichero_des.exists()){
            fichero.createNewFile();
        }
        
        
        try(
                FileInputStream fis = new FileInputStream(fichero);
                ObjectInputStream ois = new ObjectInputStream(fis);
                
                FileOutputStream fos = new FileOutputStream(fichero_des);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                
                BufferedInputStream bis = new BufferedInputStream(fis);
            ){
        
            FIS=fis;
            OIS=ois;
            FOS=fos;
            OOS=oos;
            BIS=bis;
            
            System.out.println("Nueva peticion llegada de cliente"+ncli);
            checkSum();
            System.out.println("El resultado de la suma es = "+suma);
            System.out.println("Cifrando fichero...");
            cifrarFichero();
            System.out.println("Fichero cifrado. Enviando...");
            mandarFichero(fichero_des);
            
            
            
        }catch(Exception e){
            System.err.println("Error -> "+e.getMessage());
        }
    }
//------------------------------------------------------------------------------
    private void checkSum() {
        byte[] fileBytes = new byte[1024];
        MessageDigest md = null;
        int read =0;
        
        try{
            md = MessageDigest.getInstance("SHA-256");
            while((read = BIS.read(fileBytes)) >0){
                md.update(fileBytes,0,read);
            }
            
            byte[] hexa  = md.digest();
            String result="";
            
            for(byte aux: hexa){
                int b =aux & 0xff;
                if(Integer.toHexString(b).length() ==1) result +="0";
                result += Integer.toHexString(b);
            }
            
            suma = result;
            
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
        byte[] encriptado = new byte[1024];
        
        try{
            byte [] bclaveprovisional = clave.getBytes("UTF8");
            byte [] bclave =copyOf(bclaveprovisional, 24);
            ks = new DESedeKeySpec(bclave);
            skf = SecretKeyFactory.getInstance("DESede");
            clave_priv = skf.generateSecret(ks);
            
            cifrar = Cipher.getInstance("DESede");
            cifrar.init(Cipher.ENCRYPT_MODE, clave_priv);
            CipherOutputStream cos = new CipherOutputStream(FOS, cifrar);
            int aux=0;
            while((aux = BIS.read(encriptado)) > 0){
                cos.write(encriptado,0,aux);
                cos.flush();
            }
            
        }catch(Exception e){
            System.out.println("Error -> "+e.getMessage());
        }
        
    }
//------------------------------------------------------------------------------
    private void mandarFichero(File fichero_des) {
        
    }
}
