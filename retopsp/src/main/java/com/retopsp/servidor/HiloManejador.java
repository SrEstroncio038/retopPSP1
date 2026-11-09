package com.retopsp.servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import com.retopsp.cliente.modelo.Mensaje;

public class HiloManejador extends Thread {
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String nombreUsuario;
    private ConcurrentHashMap<String, ObjectOutputStream> clientesConectados;

    public HiloManejador(Socket socket, ConcurrentHashMap<String, ObjectOutputStream> clientesConectados) {
        this.socket = socket;
        this.clientesConectados = clientesConectados;
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            entrada = new ObjectInputStream(socket.getInputStream());
            nombreUsuario = (String) entrada.readObject();
            if (clientesConectados.size() >= 10) {
                salida.writeObject(new Mensaje("SISTEMA", null, true, "Servidor lleno. Conexión rechazada.", true));
                salida.flush();
                salida.reset();
                socket.close();
                System.out.println("Conexión rechazada para " + nombreUsuario + ": Servidor lleno.");
                return;
            }
            clientesConectados.put(nombreUsuario, salida);
            System.out.println(nombreUsuario + " se ha conectado.");
            Mensaje conexion = new Mensaje("SISTEMA", "TODOS", false, nombreUsuario + " se ha conectado", true);
            for (ObjectOutputStream clienteOut : clientesConectados.values()) {
                clienteOut.writeObject(conexion);
                clienteOut.flush();
                clienteOut.reset();
            }
            while (true) {
                Mensaje msj = (Mensaje) entrada.readObject();
                
                HiloMonitor.registrarUltimoMensaje(msj.getEmisor(), msj.getMensaje()); 

                if (msj.isNotificacion() && "DESCONECTAR".equals(msj.getMensaje())) {
                    break;
                }
                if (msj.isPrivado()) {
                    ObjectOutputStream destinoOut = clientesConectados.get(msj.getReceptor());
                    if (destinoOut != null) {
                        destinoOut.writeObject(msj);
                        destinoOut.flush();
                        destinoOut.reset();
                    }
                    if (!msj.getEmisor().equals(msj.getReceptor())) {
                        salida.writeObject(msj);
                        salida.flush();
                        salida.reset();
                    }
                } else {
                    for (ObjectOutputStream clienteOut : clientesConectados.values()) {
                        clienteOut.writeObject(msj);
                        clienteOut.flush();
                        clienteOut.reset();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado: " + nombreUsuario);
        } finally {
            if (nombreUsuario != null && clientesConectados.containsKey(nombreUsuario)) {
                clientesConectados.remove(nombreUsuario);
                System.out.println(nombreUsuario + " se ha desconectado.");
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}