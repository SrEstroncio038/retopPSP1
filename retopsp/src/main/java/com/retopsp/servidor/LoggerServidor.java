package com.retopsp.servidor;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoggerServidor {

    public static synchronized void log(String texto) {
        try (FileWriter log = new FileWriter("servidor.log", true)) { //se crea

            log.write( //para escribir en el log lo sigiente:
                "[" + LocalDateTime.now() + "] "
                + texto
                + "\n"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}