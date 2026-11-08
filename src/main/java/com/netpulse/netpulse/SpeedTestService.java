
package com.netpulse.netpulse;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.function.DoubleConsumer;

public class SpeedTestService {

    private static final int DOWNLOAD_LIMIT = 5 * 1024 * 1024;

    public double testDownload(DoubleConsumer onProgress)
            throws Exception {

        String url = "https://speed.cloudflare.com/__down?bytes="
                + DOWNLOAD_LIMIT;

        HttpURLConnection connection =
                (HttpURLConnection) URI.create(url)
                        .toURL().openConnection();

        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty(
                "Accept-Encoding", "identity"
        );

        long totalBytes = 0;
        long startTime = System.nanoTime();
        long lastUpdate = startTime;

        try {
            int status = connection.getResponseCode();

            if (status != 200) {
                throw new IllegalStateException(
                        "Download failed: HTTP " + status
                );
            }

            try (InputStream input = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytes += bytesRead;

                    long now = System.nanoTime();
                    double elapsedSeconds =
                            (now - startTime) / 1_000_000_000.0;

                    if (now - lastUpdate >= 200_000_000L
                            && elapsedSeconds > 0) {

                        double currentMbps =
                                (totalBytes * 8.0)
                                        / elapsedSeconds
                                        / 1_000_000.0;

                        onProgress.accept(currentMbps);
                        lastUpdate = now;
                    }

                    if (totalBytes >= DOWNLOAD_LIMIT) {
                        break;
                    }
                }
            }

            double elapsedSeconds =
                    (System.nanoTime() - startTime)
                            / 1_000_000_000.0;

            if (totalBytes == 0 || elapsedSeconds <= 0) {
                throw new IllegalStateException(
                        "No download data received"
                );
            }

            double finalMbps =
                    (totalBytes * 8.0)
                            / elapsedSeconds
                            / 1_000_000.0;

            onProgress.accept(finalMbps);
            return finalMbps;

        } finally {
            connection.disconnect();
        }
    }


    public double testUpload(DoubleConsumer onProgress)
            throws Exception {

        final int uploadSize = 2 * 1024 * 1024;
        byte[] payload = new byte[uploadSize];

        HttpURLConnection connection =
                (HttpURLConnection) URI.create(
                        "https://speed.cloudflare.com/__up"
                ).toURL().openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setFixedLengthStreamingMode(uploadSize);
        connection.setRequestProperty(
                "Content-Type", "application/octet-stream"
        );

        long startTime = System.nanoTime();
        long totalBytes = 0;

        try {
            connection.connect();

            try (var output = connection.getOutputStream()) {
                int offset = 0;
                int chunkSize = 8192;

                while (offset < uploadSize) {
                    int length = Math.min(
                            chunkSize, uploadSize - offset
                    );

                    output.write(payload, offset, length);

                    offset += length;
                    totalBytes += length;

                    double elapsed =
                            (System.nanoTime() - startTime)
                                    / 1_000_000_000.0;

                    if (elapsed > 0) {
                        double mbps =
                                (totalBytes * 8.0)
                                        / elapsed / 1_000_000.0;

                        onProgress.accept(mbps);
                    }
                }
            }

            int status = connection.getResponseCode();

            if (status < 200 || status >= 300) {
                throw new IllegalStateException(
                        "Upload failed: HTTP " + status
                );
            }

            double seconds =
                    (System.nanoTime() - startTime)
                            / 1_000_000_000.0;

            double finalMbps =
                    (totalBytes * 8.0)
                            / seconds / 1_000_000.0;

            onProgress.accept(finalMbps);
            return finalMbps;

        } finally {
            connection.disconnect();
        }
    }

}
