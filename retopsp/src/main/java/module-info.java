module com.retopsp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.retopsp to javafx.fxml;
    exports com.retopsp;
}
