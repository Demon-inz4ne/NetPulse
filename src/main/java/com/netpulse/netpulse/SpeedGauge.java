
package com.netpulse.netpulse;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class SpeedGauge extends Region {

    private final Canvas canvas = new Canvas(320, 290);
    private final Color accent;
    private final String title;

    private final DoubleProperty speed =
            new SimpleDoubleProperty(0);

    private double maxSpeed = 100;
    private Timeline animation;

    public SpeedGauge(String title, Color accent) {
        this.title = title;
        this.accent = accent;

        getChildren().add(canvas);
        setPrefSize(320, 290);

        speed.addListener((obs, oldValue, newValue) -> draw());

        widthProperty().addListener((obs, oldValue, newValue) -> resize());
        heightProperty().addListener((obs, oldValue, newValue) -> resize());

        draw();
    }

    private void resize() {
        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) return;

        canvas.setWidth(width);
        canvas.setHeight(height);
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.clearRect(0, 0, w, h);

        double cx = w / 2;
        double cy = h * 0.52;
        double radius = Math.min(w * 0.37, h * 0.36);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFill(accent);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 17));
        gc.fillText(title, cx, 27);

        // Background arc
        gc.setStroke(Color.web("#292C43"));
        gc.setLineWidth(15);
        gc.strokeArc(
                cx - radius, cy - radius,
                radius * 2, radius * 2,
                210, -240,
                javafx.scene.shape.ArcType.OPEN
        );

        double value = Math.max(0, Math.min(speed.get(), maxSpeed));
        double progress = value / maxSpeed;

        // Active arc
        gc.setStroke(accent);
        gc.setLineWidth(15);
        gc.strokeArc(
                cx - radius, cy - radius,
                radius * 2, radius * 2,
                210, -240 * progress,
                javafx.scene.shape.ArcType.OPEN
        );

        // Scale labels
        gc.setFill(Color.web("#8995A5"));
        gc.setFont(Font.font("Consolas", 11));

        for (int i = 0; i <= 5; i++) {
            double angle = Math.toRadians(210 - i * 48);
            double labelRadius = radius - 26;

            double x = cx + Math.cos(angle) * labelRadius;
            double y = cy - Math.sin(angle) * labelRadius;

            gc.fillText(
                    String.format("%.0f", maxSpeed * i / 5),
                    x, y
            );
        }

        // Needle
        double angle = Math.toRadians(210 - 240 * progress);
        double nx = cx + Math.cos(angle) * (radius - 35);
        double ny = cy - Math.sin(angle) * (radius - 35);

        gc.setStroke(Color.web("#DCE5EC"));
        gc.setLineWidth(4);
        gc.strokeLine(cx, cy, nx, ny);

        gc.setFill(accent);
        gc.fillOval(cx - 6, cy - 6, 12, 12);

        // Speed value
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 31));
        gc.fillText(String.format("%.2f", speed.get()), cx, h - 65);

        gc.setFill(Color.web("#8995A5"));
        gc.setFont(Font.font("Consolas", 14));
        gc.fillText("Mbps", cx, h - 35);
    }

    public void setSpeed(double newSpeed) {
        if (animation != null) {
            animation.stop();
        }

        newSpeed = Math.max(0, newSpeed);

        if (newSpeed > maxSpeed) {
            maxSpeed = Math.ceil(newSpeed / 100.0) * 100;
        }

        animation = new Timeline(
                new KeyFrame(
                        Duration.millis(700),
                        new KeyValue(
                                speed,
                                newSpeed,
                                Interpolator.EASE_BOTH
                        )
                )
        );

        animation.play();
    }
}
