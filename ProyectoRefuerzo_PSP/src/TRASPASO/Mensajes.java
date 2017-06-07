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
public class Mensajes implements java.io.Serializable {
    private String texto;

    public Mensajes() {
    }

    public Mensajes(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return "Mensajes{" + "texto=" + texto + '}';
    }
    
     
}
