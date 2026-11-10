package com.retopsp.servidor;

import java.io.IOException;
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
    private ConcurrentHashMap<String, ObjectOutputStream> clientesConectados; //hashMap para hilos

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
                LoggerServidor.log("[ERROR] Conexión rechazada para " + nombreUsuario + ": servidor lleno"); // para guardar esto
                return;
            }
            clientesConectados.put(nombreUsuario, salida);
            LoggerServidor.log(nombreUsuario + " se ha conectado."); // para que se guarde en el logger
            Mensaje conexion = new Mensaje("SISTEMA", "TODOS", false, nombreUsuario + " se ha conectado", true);
            for (ObjectOutputStream clienteOut : clientesConectados.values()) {
                clienteOut.writeObject(conexion);
                clienteOut.flush();
                clienteOut.reset();
            }
            while (true) {
                Mensaje msj = (Mensaje) entrada.readObject();
                LoggerServidor.log("[RECIBIDO] " + msj.getEmisor() + ": " + msj.getMensaje()); // se registra en el log el mensaje recibido con detalles

                HiloMonitor.registrarUltimoMensaje(msj.getEmisor(), msj.getMensaje()); // Guarda el ultimo mensaje

                if (msj.isNotificacion() && "DESCONECTAR".equals(msj.getMensaje())) {
                    break; //Para desconectar se crea un mensaje con texto DESCONECTAR aqui lo chequea Y PONE NOTIFICACION TRUE PARA no confundirlo con el texto de un mensaje
                }
                if (msj.isPrivado()) {
                    LoggerServidor
                            .log("[PRIVADO] " + msj.getEmisor() + " -> " + msj.getReceptor() + ": " + msj.getMensaje()); // un registro para el log
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
                    LoggerServidor.log("[PUBLICO] " + msj.getEmisor() + ": " + msj.getMensaje()); // para registrar losmensajes publicos

                    for (ObjectOutputStream clienteOut : clientesConectados.values()) {
                        clienteOut.writeObject(msj);
                        clienteOut.flush();
                        clienteOut.reset();
                    }
                }
            }
        } catch (Exception e) {
            LoggerServidor.log("[ERROR] Cliente " + nombreUsuario + ": " + e.getMessage());
            System.out.println("Cliente desconectado: " + nombreUsuario);
        } finally {
            LoggerServidor.log("[DESCONEXION] " + nombreUsuario + " se ha desconectado");
            if (nombreUsuario != null && clientesConectados.containsKey(nombreUsuario)) {
                clientesConectados.remove(nombreUsuario);
                Mensaje desconexion = new Mensaje(
                        "SISTEMA",
                        "TODOS",
                        false,
                        nombreUsuario + " se ha desconectado",
                        true);

                for (ObjectOutputStream clienteOut : clientesConectados.values()) {
                    try {
                        clienteOut.writeObject(desconexion);
                        clienteOut.flush();
                        clienteOut.reset();
                    } catch (IOException e) {
                        LoggerServidor.log(
                                "[ERROR] No se pudo enviar mensaje de desconexión: "
                                        + e.getMessage());
                    }
                }
                try {
                    entrada.close();
                    salida.close();
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}