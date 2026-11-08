
package com.netpulse.netpulse;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class HelloController {

    @FXML private Button checkIpButton;
    @FXML private Label ipAddressLabel;
    @FXML private Label locationLabel;
    @FXML private Button pingButton;
    @FXML private Label pingLabel;
    @FXML private Label statusLabel;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @FXML
    private void handleIpLookup() {
        checkIpButton.setDisable(true);
        ipAddressLabel.setText("IP ADDRESS: Loading...");
        locationLabel.setText("LOCATION / ISP: Fetching data...");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ipwho.is/"))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        httpClient.sendAsync(request,
                        HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, error) -> {

                    Platform.runLater(() -> {
                        checkIpButton.setDisable(false);

                        if (error != null ||
                                response == null ||
                                response.statusCode() != 200) {
                            showError();
                            return;
                        }

                        try {
                            JsonObject json = JsonParser.parseString(response.body())
                                    .getAsJsonObject();

                            if (!json.has("success")
                                    || !json.get("success").getAsBoolean()) {
                                showError();
                                return;
                            }

                            String ip = getValue(json, "ip");
                            String country = getValue(json, "country");
                            String city = getValue(json, "city");

                            ipAddressLabel.setText("IP ADDRESS: " + ip);

                            locationLabel.setText(
                                    "LOCATION: " + city + ", " + country
                            );

                            statusLabel.setText("● STATUS: ONLINE");

                        } catch (Exception e) {
                            showError();
                        }
                    });
                });
    }

    private String getValue(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return "Unknown";
        }

        try {
            return json.get(key).getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void showError() {
        ipAddressLabel.setText("IP ADDRESS: Unavailable");
        locationLabel.setText("LOCATION: Lookup failed");
        statusLabel.setText("● STATUS: LOOKUP FAILED");
    }


    @FXML
    private void handlePingTest() {
        pingButton.setDisable(true);
        pingLabel.setText("LATENCY: Testing...");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.google.com/"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        long startTime = System.nanoTime();

        httpClient.sendAsync(
                request,
                HttpResponse.BodyHandlers.discarding()
        ).whenComplete((response, error) -> {

            long endTime = System.nanoTime();
            long latency = (endTime - startTime) / 1_000_000;

            Platform.runLater(() -> {
                pingButton.setDisable(false);

                if (error != null || response == null) {
                    pingLabel.setText("LATENCY: Test failed");
                    return;
                }

                pingLabel.setText("HTTP LATENCY: " + latency + " ms");
            });
        });
    }


    @FXML
    private StackPane downloadGaugeContainer;

    @FXML
    private StackPane uploadGaugeContainer;

    @FXML
    private Button startSpeedTestButton;

    @FXML
    private Label speedStatusLabel;

    private SpeedGauge downloadGauge;
    private SpeedGauge uploadGauge;

    @FXML
    private void initialize() {
        downloadGauge = new SpeedGauge(
                "DOWNLOAD",
                Color.web("#22D3EE")
        );

        uploadGauge = new SpeedGauge(
                "UPLOAD",
                Color.web("#A78BFA")
        );

        downloadGaugeContainer.getChildren().add(downloadGauge);
        uploadGaugeContainer.getChildren().add(uploadGauge);
    }

    @FXML
    private void handleSpeedTest() {

        startSpeedTestButton.setDisable(true);
        downloadGauge.setSpeed(0);
        uploadGauge.setSpeed(0);

        speedStatusLabel.setText(
                "DOWNLOAD TEST RUNNING..."
        );

        Thread worker = new Thread(() -> {

            try {
                double download = speedTestService.testDownload(
                        mbps -> Platform.runLater(
                                () -> downloadGauge.setSpeed(mbps)
                        )
                );

                Platform.runLater(() -> {
                    downloadGauge.setSpeed(download);
                    speedStatusLabel.setText(
                            "UPLOAD TEST RUNNING..."
                    );
                });

                double upload = speedTestService.testUpload(
                        mbps -> Platform.runLater(
                                () -> uploadGauge.setSpeed(mbps)
                        )
                );

                Platform.runLater(() -> {
                    uploadGauge.setSpeed(upload);

                    speedStatusLabel.setText(
                            String.format(
                                    "TEST COMPLETE // DOWN %.2f Mbps | UP %.2f Mbps",
                                    download, upload
                            )
                    );

                    startSpeedTestButton.setDisable(false);
                });

            } catch (Exception error) {
                Platform.runLater(() -> {
                    speedStatusLabel.setText(
                            "TEST FAILED // " + error.getMessage()
                    );
                    startSpeedTestButton.setDisable(false);
                });
            }
        });

        worker.setDaemon(true);
        worker.start();
    }


    private final SpeedTestService speedTestService =
            new SpeedTestService();

}
