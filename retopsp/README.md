# Servidor de chat concurrente (PSP)

Proyecto desarrollado para la asignatura de **Programación de Servicios y Procesos (PSP)**. Consiste en una arquitectura cliente-servidor basada en Sockets de Java que permite la comunicación bidireccional en tiempo real entre múltiples clientes, soportando mensajes públicos, privados y un sistema robusto de gestión de hilos y monitorización.

---

## Autores
* [Rebeca López, Asier Solaguren y Etna Guarino / Grupo 2]

---

## Características principales

1. **Modelo de comunicaciones (Sockets):**
   * Conexión persistente mediante `ServerSocket` y `Socket`.
   * Intercambio de objetos serializables (`ObjectInputStream` / `ObjectOutputStream`) para el envío estructurado de mensajes (`Mensaje`).
   * Soporte para **mensajes públicos** (difusión a todos los clientes conectados) y **mensajes privados** (dirigidos a un usuario específico).

2. **Gestión de hilos (Servidor):**
   * **Concurrencia:** el servidor acepta múltiples clientes simultáneamente utilizando un bucle de aceptación (`accept()`) que instancia un hilo independiente (`HiloManejador`) por cada cliente.
   * **Estructuras Concurrentes:** uso de `ConcurrentHashMap` para administrar la lista de clientes conectados de forma segura ante accesos concurrentes.
   * **Control de Excedentes:** límite máximo de conexiones concurrentes configurado (hasta 10 clientes), rechazando de forma controlada el exceso de conexiones.

3. **Monitorización del Servidor:**
   * Implementación de un hilo independiente (`HiloMonitor`) que recopila y muestra estadísticas periódicas en la consola del servidor (clientes conectados, tiempo activo y último mensaje registrado).

---

## Estructura del Servidor

* `Servidor.java`: clase principal que inicializa el puerto de escucha, arranca el hilo monitor y gestiona el bucle infinito de conexiones entrantes.
* `HiloManejador.java`: hilo encargado de la comunicación bidireccional con cada cliente individual, procesando sus peticiones y gestionando su desconexión segura.
* `HiloMonitor.java`: hilo secundario dedicado exclusivamente a la monitorización y volcado de estadísticas del servidor en tiempo real.

## Estructura del cliente
* `App.java`: punto de entrada de la aplicación cliente con la interfaz gráfica (JavaFX/FXML).
* `ChatController.java`: controlador que gestiona los eventos de la interfaz (botones, listas de usuarios).
* `HiloEnviar.java`: hilo encargo de enviar las peticiones y mensajes al servidor sin congelar la ventana.
* `HiloRecibir.java`: hilo que recibe los mensajes en segundo plano y actualiza la interfaz gráfica en tiempo real.
* `Mensaje.java`: objeto de transferencia de datos serializable que empaqueta la información intercambiada entre el cliente y el servidor (emisor, receptor, tipo de mensaje, texto y notificaciones del sistema).

---

## Cómo ejecutar el proyecto

1. **Compilar y arrancar el Servidor:**
   * Ejecuta primero la clase `Servidor.java` desde tu entorno de desarrollo.
   * Verás en consola el mensaje de que el servidor se ha iniciado correctamente en el puerto correspondiente.

2. **Arrancar los clientes:**
   * Ejecuta la clase principal del cliente (`App.java` o la interfaz gráfica) tantas veces como usuarios quieras simular para probar la concurrencia y el chat.