package com.retopsp.cliente.controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.retopsp.cliente.modelo.Mensaje;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ChatController {
    @FXML
    private Button conectar;

    @FXML
    private ListView<String> chat;

    @FXML
    private Button desconectar;

    @FXML
    private Button enviar;

    @FXML
    private Label estado;

    @FXML
    private TextField ip;

    @FXML
    private TextField mensaje;

    @FXML
    private HBox p;

    @FXML
    private CheckBox privado;

    @FXML
    private TextField puerto;

    @FXML
    private TextField receptor;

    @FXML
    private TextField user;

    private ObservableList<String> mensajes;
    private Socket socket;
    private ObjectOutputStream salida;
    private HiloRecibir hiloRecibir;

    @FXML
    void initialize() {
        mensajes = FXCollections.observableArrayList();
        desconectar.setDisable(true);
    }

    @FXML
    void conectar() {
        String direccionIp;
        if (ip.getText().isEmpty()) { 
            direccionIp = "127.0.0.1"; // si esta vacio 
        } else {
            direccionIp = ip.getText();
        }
        int numPuerto;
        try {
            if (puerto.getText().isEmpty()) {
                numPuerto = 5050;
            } else {
                numPuerto = Integer.parseInt(puerto.getText());
            }
        } catch (NumberFormatException e) {
            numPuerto = 5050; //si pone algo que no son numeros 
        }
        try {
            socket = new Socket(direccionIp, numPuerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush(); // para limpiar la salida y evitar errores
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String nombreUsuario = user.getText();
            salida.writeObject(nombreUsuario);
            salida.flush();
            hiloRecibir = new HiloRecibir(entrada, this);
            hiloRecibir.start();
            desconectar.setDisable(false);
            conectar.setDisable(true);
            user.setDisable(true);
            ip.setDisable(true);
            puerto.setDisable(true);
            estado.setText("Conectado");
            chat.setItems(mensajes);
            mensajes.add("Conectado con éxito.");
        } catch (IOException e) {
            mostrarErrorConexion("No se pudo conectar al servidor. Comprueba la IP y el puerto.");
            e.printStackTrace();
        }
    }

    @FXML
    public void enviarMensaje() {
        Mensaje msj = new Mensaje(user.getText(), receptor.getText(), privado.isSelected(), mensaje.getText(),
                false);
        Thread hiloEnvio = new Thread(new HiloEnviar(salida, msj));
        hiloEnvio.start();

        mensaje.clear();
    }

    @FXML
    void desconectar() {
        try {
            Mensaje msjDesconexion = new Mensaje(user.getText(), "SERVIDOR", false, "DESCONECTAR", true);
            salida.writeObject(msjDesconexion);
            salida.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarConexion();
            mensajes.add("Te has desconectado del servidor.");
        }
    }

    public void cerrarConexion() {
        try {
            hiloRecibir.detener();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            desconectar.setDisable(true);
            conectar.setDisable(false);
            user.setText("");
            ip.setText("");
        }
    }

    public void escribirMensaje(Mensaje m) {
        String texto = m.getEmisor() + "-->" + m.getReceptor() + ": " + m.getMensaje();
        mensajes.add(texto);
        chat.setItems(mensajes);
    }

    public void escribirMensajePrivado(Mensaje m) {
        String texto = "Mensaje privado de " + m.getEmisor() + ": " + m.getMensaje();
        mensajes.add(texto);
        chat.setItems(mensajes);
    }

    public void mostrarErrorConexion(String error) {
        Alert alerta = new Alert(AlertType.ERROR);
        String texto = (error);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}
