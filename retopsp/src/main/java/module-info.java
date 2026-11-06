module com.retopsp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    exports com.retopsp.cliente;
    opens com.retopsp.cliente to javafx.fxml;
}