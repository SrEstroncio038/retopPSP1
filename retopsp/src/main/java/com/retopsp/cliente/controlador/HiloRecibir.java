package com.retopsp.cliente.controlador;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.retopsp.cliente.modelo.Mensaje;

import javafx.application.Platform;

public class HiloRecibir extends Thread {
    private ObjectInputStream entrada;
    private ChatController controlador;
    private boolean activo = true;

    public HiloRecibir(ObjectInputStream entrada, ChatController controlador) {
        this.entrada = entrada;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje msj = (Mensaje) entrada.readObject();
                Platform.runLater(() -> {
                    if (msj.isPrivado()) {
                        controlador.escribirMensajePrivado(msj);
                    } else {
                        controlador.escribirMensaje(msj);
                    }
                });
            }
        } catch (ClassNotFoundException | IOException e) {
            if (activo) {
                Platform.runLater(() -> {
                    controlador.mostrarErrorConexion("Conexión finalizada o cerrada por el servidor.");
                    controlador.cerrarConexion();
                });
            }
        }
    }

    public void detener() {
        activo = false;
        try {
            if (entrada != null)
                entrada.close();
        } catch (IOException e) {
        }
    }
}