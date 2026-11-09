package com.retopsp.servidor;

import java.util.concurrent.ConcurrentHashMap;

public class HiloMonitor extends Thread{
    private ConcurrentHashMap<String, ?> clientesConectados;
    private long tiempoInicio;
    private static String ultimoMensaje = "Ninguno todavía";

    public HiloMonitor(ConcurrentHashMap<String, ?> clientesConectados) {
        this.clientesConectados = clientesConectados;
        this.tiempoInicio = System.currentTimeMillis();
    }

    public static void registrarUltimoMensaje(String emisor, String texto) {
        ultimoMensaje = emisor + ": " + texto;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(15000);

                long tiempoActivoSegundos = (System.currentTimeMillis() - tiempoInicio) / 1000;

                System.out.println("\n---MONITOR DE SISTEMA ---");
                System.out.println("Clientes conectados: " + clientesConectados.size());
                System.out.println("Tiempo activo: " + tiempoActivoSegundos + " segundos");
                System.out.println("Último mensaje: " + ultimoMensaje);

            } catch (InterruptedException e) {
                System.out.println("Hilo monitor interrumpido.");
                break;
            }
        }
    }
}
