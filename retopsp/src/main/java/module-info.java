module com.retopsp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    exports com.retopsp.cliente.controlador;
    exports com.retopsp.cliente.modelo;
    exports com.retopsp.servidor;
    opens com.retopsp.cliente.controlador to javafx.fxml;
}