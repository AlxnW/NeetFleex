module com.dev.neetfleexui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires jdk.compiler;
    requires javafx.media;
    requires java.net.http;
    requires java.logging;
    requires uk.co.caprica.vlcj.javafx;
    requires uk.co.caprica.vlcj;
    requires java.desktop;
    requires java.prefs;

    opens com.dev.neetfleexui.controllers to javafx.fxml;
    exports com.dev.neetfleexui;
    exports com.dev.neetfleexui.dto;
}