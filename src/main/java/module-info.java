module org.example.fitsyncui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;

    opens org.example.fitsyncui to javafx.fxml;
    opens org.example.fitsyncui.model to com.fasterxml.jackson.databind;

    exports org.example.fitsyncui;
    exports org.example.fitsyncui.model;
}
