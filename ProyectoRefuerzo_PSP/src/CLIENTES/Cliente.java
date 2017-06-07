/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENTES;

import SERVIDOR.HiloServidor;
import TRASPASO.Fichero;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 *
 * @author ruben
 */
public class Cliente {
    public static void main(String[]args){
        InetAddress ip =null;
        String cadena;
        int puerto=0;
        File file = new File("confidencial.descifrado");
        
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
            
            int borrar=0;
            System.out.println("Recibiendo archivo...");
            Fichero fichero;
            Object aux;
            do{
                aux=ois.readObject();
                if(aux instanceof Fichero){
                    fichero =(Fichero)aux;
                    fos.write(fichero.getTrozo(),0,fichero.getBytesValidos());
                }else{
                    System.err.println("Mensaje no esperado "
                            + aux.getClass().getName());
                    break;
                }
            }while(!fichero.isUltimo());
            
           System.out.println("!Archivo Recibido! Descifrando...");
           desencriptar();
           System.out.println("!Archivo Descifrado! Calculando SHA-512...");
           String checkSumCliente=checkSum();

           System.out.println("!SHA-512 Calculado! Recibiendo suma del servidor...");
           String checkSumServidor=recibeCheckSum(ois);
           System.out.println("El checkSum de nuestro archivo es: " + checkSumCliente);
           System.out.println("El checkSum del archivo original es: " + checkSumServidor);
           
           if(checkSumCliente.equals(checkSumServidor)){
               System.out.println("La suma coincide, transmisión OK");
           }
           else{
               System.out.println("La suma NO coincide, ha habido algun error en la transmisión!!!!!!!");
           }
            
            
            
            
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
    public static void desencriptar() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException{
   
        String pass="password1234";
        byte [] clave1;
        byte [] claveFinal;
        KeySpec ks;
        SecretKeyFactory skf;
        SecretKey sk;
        Cipher cifrar;
        File des = new File("confidencial.descifrado");
        
        //pasamos clave a array de bytes de 24
        clave1=pass.getBytes();
        claveFinal = copyOf(clave1, 24);
        if(!des.exists()){
            des.createNewFile();
        }
        
        try(
               FileOutputStream fos = new FileOutputStream("confidencial.txt");
               FileInputStream fis = new FileInputStream(des) 
               
                )
        {
            
            
                


            //------Generamos la clave----------------------------
            ks= new DESedeKeySpec(claveFinal);
            skf = SecretKeyFactory.getInstance("DESede");
            sk = skf.generateSecret(ks);
            //Ponemos Cpher en modo cifrar----------------------
            cifrar = Cipher.getInstance("DESede");
            cifrar.init(Cipher.DECRYPT_MODE, sk);
            CipherOutputStream cos = new CipherOutputStream(fos, cifrar);
            int lectura=0;
            byte [] bfrase= new byte[512];
            while((lectura=fis.read(bfrase))>0){
                cos.write(bfrase, 0, lectura);
                cos.flush();
            }
            cos.close();
            //Borramos el Auxiliar
            File f = new File("confidencial.des.lol");
            f.delete();
            
        } catch (InvalidKeyException ex) {
            System.err.println("Error mensaje: " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Error al con TripleDES ");
        } catch (InvalidKeySpecException ex) {
            System.err.println("Error, mensaje: " + ex.getMessage());
        } catch (NoSuchPaddingException ex) {
             System.err.println("Error, mensaje: " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//------------------------------------------------------------------------------
    public static String checkSum(){
            byte [] shasum = null;
            byte [] btexto = new byte[1024];
            MessageDigest md=null;
            String sumaFichero=null;

            try(
                    FileInputStream fis= new FileInputStream("confidencial.txt");
                    BufferedInputStream bis = new BufferedInputStream(fis)
                    ) 
            {
                md = MessageDigest.getInstance("SHA-256");
                int lectura=0;
                while((lectura=bis.read(btexto))>0){
                    md.update(btexto, 0, lectura);
                }
                shasum=md.digest();
                sumaFichero=Base64.getEncoder().encodeToString(shasum);
            } catch (NoSuchAlgorithmException ex) {
                System.err.println("Hilo, estamos en checksum abriendo SHA256, error= " + ex.getMessage());
            } catch (IOException ex) {
                System.err.println("Estamos en hilo cheksum, leyendo fichero error=" + ex.getMessage());
            }
            return sumaFichero;
    }    
//------------------------------------------------------------------------------
    public static String recibeCheckSum(ObjectInputStream ois){
    Mensajes ms = new Mensajes();
    String cad=null;
    Object aux;
        try {
            aux=ois.readObject();
            if(aux instanceof Mensajes){
                ms=(Mensajes)aux;
                cad=ms.getTexto();
            }
            else{
                System.out.println("Formato checksum servidor NO esperado!!!! "); 
            }
            
        } catch (IOException ex) {
           System.err.println("Error al recibir checksum del server, " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
           System.out.println("Formato checksum servidor NO esperado!!!! " + ex.getMessage());
        }
        return cad;
    }
//------------------------------------------------------------------------------    
}
