
module com.netpulse.netpulse {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens com.netpulse.netpulse to javafx.fxml;
    exports com.netpulse.netpulse;
}
