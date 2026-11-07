package com.retopsp.cliente.controlador;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.retopsp.cliente.modelo.Mensaje;

public class HiloEnviar extends Thread {
    private ObjectOutputStream salida;
    private Mensaje mensaje;

    public ObjectOutputStream getSalida() {
        return salida;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public HiloEnviar(ObjectOutputStream salida, Mensaje mensaje) {
        this.salida = salida;
        this.mensaje = mensaje;
    }

    @Override
    public void run() {
        try {
            salida.writeObject(mensaje);
            salida.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
