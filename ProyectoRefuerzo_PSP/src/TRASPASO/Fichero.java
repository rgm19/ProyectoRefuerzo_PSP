/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TRASPASO;

/**
 *
 * @author usuario5
 */
public class Fichero implements java.io.Serializable {
    private String nombre;
    private boolean ultimo;
    private int bytesValidos=0;
    private byte[] trozo = new byte[longitud];
    private final static int longitud =10;

    public Fichero() {
    }

    public Fichero(String nombre, boolean ultimo) {
        this.nombre = nombre;
        this.ultimo = ultimo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isUltimo() {
        return ultimo;
    }

    public void setUltimo(boolean ultimo) {
        this.ultimo = ultimo;
    }

    public int getBytesValidos() {
        return bytesValidos;
    }

    public void setBytesValidos(int bytesValidos) {
        this.bytesValidos = bytesValidos;
    }

    public byte[] getTrozo() {
        return trozo;
    }

    public void setTrozo(byte[] trozo) {
        this.trozo = trozo;
    }

    @Override
    public String toString() {
        return "Fichero{" + "nombre=" + nombre + ", ultimo=" + ultimo + ", bytesValidos=" + bytesValidos + ", trozo=" + trozo + '}';
    }
    
    
    
}
