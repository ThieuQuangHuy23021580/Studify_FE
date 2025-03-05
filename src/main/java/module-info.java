module controller.libraryapp {
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
    requires java.desktop;
//    requires org.mariadb.jdbc;
    requires com.google.gson;
    requires java.sql;

    opens controller to javafx.fxml;

    opens controller.FXML to javafx.fxml;
    exports controller;
    exports MainWeb;
    opens MainWeb to javafx.fxml;
    exports View;
    opens View to javafx.fxml;
}
