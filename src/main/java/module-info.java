
module com.netpulse.netpulse {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;

    opens com.netpulse.netpulse to javafx.fxml;
    exports com.netpulse.netpulse;
}
