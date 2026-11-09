# ⚡ NetPulse

**A modern desktop network monitoring and speed-testing application built with JavaFX.**

NetPulse brings essential connection details and real-time network speed measurements into one clean, dark-themed dashboard. Check your public IP and approximate location, measure HTTP latency, and watch your download and upload speeds on animated gauges.

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-2F74C0)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active%20Development-00C896)

## ✨ Features

- **Connection overview** — displays network connection information and status.
- **Public IP and location** — retrieves public IP, approximate city/country, and ISP information using [ipwho.is](https://ipwho.is/).
- **HTTP latency** — measures response latency to a remote endpoint.
- **Download speed test** — measures throughput with a real network transfer, showing live and final Mbps values.
- **Upload speed test** — measures outbound throughput with a real network transfer, showing live and final Mbps values.
- **Animated speed gauges** — separate cyan and purple JavaFX gauges, featuring needles, arcs, and dynamically scaled values.
- **Responsive feedback** — asynchronous operations keep the interface usable and show error states when a request fails.

## 🖥️ Preview

NetPulse uses a dark, neon-accented desktop interface.

> **Screenshot coming soon.** A dashboard screenshot will be added here in a future documentation update.

## 🛠️ Tech Stack

| Technology | Purpose |
| --- | --- |
| Java 17 | Application language |
| JavaFX 21 | Desktop UI, FXML, and graphics |
| Maven | Dependency management and build |
| Gson | JSON parsing |
| JUnit 5 | Test dependencies |

### Network services

- [ipwho.is](https://ipwho.is/) — public IP and approximate location details
- [Cloudflare](https://www.cloudflare.com/) — network transfer endpoints for speed testing
- [Google](https://www.google.com/) — HTTP latency check endpoint

## 🚀 Getting Started

### Prerequisites

- **JDK 17 or newer** (JDK 17 is the project's configured compilation target)
- **Apache Maven** (or an IDE with Maven support)
- An active internet connection for live network tests

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Demon-inz4ne/NetPulse.git
   cd NetPulse
   ```

2. Run the JavaFX application:

   ```bash
   mvn clean javafx:run
   ```

   You can also import the project as a **Maven project** in IntelliJ IDEA and run its JavaFX Maven configuration.

### How to use

1. Launch NetPulse.
2. View the available connection, public IP, location, and ISP details.
3. Run a latency check to see the HTTP response time.
4. Start the download or upload test and follow the animated Mbps gauges.
5. Review the final displayed results.

> Buttons and labels may differ slightly as the interface evolves.

## 📏 Measurement Notes

NetPulse is intended as a useful desktop connection diagnostic tool, not a laboratory-grade benchmark.

- **Speed** is calculated from transferred data and elapsed time; results may vary due to Wi-Fi quality, mobile hotspots, server load, routing, and background traffic.
- **Latency** is an HTTP request measurement, **not** an ICMP ping or an in-game ping reading.
- **Location** is IP-based and approximate; it does not identify an exact physical address.
- Tests use third-party endpoints and require internet access. Their availability and behavior can affect results.
- Running speed tests consumes network data.

## 🗺️ Roadmap

- [x] JavaFX dashboard and dark UI
- [x] Public IP, location, and ISP display
- [x] HTTP latency measurement
- [x] Download and upload speed measurements
- [x] Animated speed gauges
- [ ] Gaming Analyzer (game-specific latency insights)
- [ ] Jitter and packet-loss measurements
- [ ] Additional diagnostics and polish

**Gaming Analyzer is planned for a future update; it is not part of the current release.**

## ✅ Testing

The application completed an eight-check functional test pass during development. The Maven configuration also includes JUnit 5 dependencies; this README does not claim that an automated test suite is currently implemented.

To run any available Maven tests:

```bash
mvn test
```

## 🤝 Contributing

Suggestions, bug reports, and improvements are welcome. Open an issue or propose a pull request with a clear description of the change.

## 👨‍💻 Author

Created by **Janith Geeshan** — [@Demon-inz4ne](https://github.com/Demon-inz4ne).

---

If you find NetPulse useful, consider giving the repository a ⭐.
