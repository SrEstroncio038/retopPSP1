package com.retopsp.servidor;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private static final int PUERTO = 5000;
    private static ConcurrentHashMap<String, ObjectOutputStream> clientesConectados = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR DE CHAT INICIADO EN PUERTO " + PUERTO + " ===");
        HiloMonitor monitor = new HiloMonitor(clientesConectados);
        monitor.start();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = serverSocket.accept();
                HiloManejador manejador = new HiloManejador(socket, clientesConectados);
                manejador.start();
            }
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}