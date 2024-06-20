package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class Server {

    public static void main(String[] args) {
        // Tworzy serwer nasłuchujący na porcie 12345
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is listening on port 12345");

            // Akceptuje nowe połączenia i obsługuje je w osobnych wątkach
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Klasa obsługująca połączenia od klientów
    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            // Odczytuje dane od klienta
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String username = in.readLine(); // Odczytuje nazwę użytkownika
                System.out.println("User: " + username);

                int electrodeNumber = 1; // Numer elektrody (zaczyna od 1)
                String line;
                while ((line = in.readLine()) != null) {
                    if ("bye".equals(line)) {
                        break; // Zakończa połączenie gdy klient wysyła "bye"
                    }

                    // Generuje wykres z danych CSV i zapisuje jako Base64
                    String base64Image = generatePlotBase64(line);
                    // Zapisuje dane do bazy danych
                    saveToDatabase(username, electrodeNumber, base64Image);
                    electrodeNumber++; // Zwiększa numer elektrody
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Generuje wykres z danych CSV i zwraca jako ciąg Base64
        private String generatePlotBase64(String csvLine) {
            String[] values = csvLine.split(",");
            XYSeries series = new XYSeries("EEG Signal");

            // Dodaje punkty danych do serii
            for (int i = 0; i < values.length; i++) {
                series.add(i * 2, Double.parseDouble(values[i]));
            }

            XYSeriesCollection dataset = new XYSeriesCollection(series);
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "EEG Signal",
                    "Time (ms)",
                    "Amplitude (µV)",
                    dataset
            );

            // Konwertuje wykres na obraz BufferedImage
            BufferedImage image = chart.createBufferedImage(800, 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                // Zapisuje obraz jako PNG do strumienia ByteArrayOutputStream
                ChartUtils.writeBufferedImageAsPNG(baos, image);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Koduje obraz do formatu Base64 i zwraca
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }

        // Zapisuje dane do bazy danych SQLite
        private void saveToDatabase(String username, int electrodeNumber, String base64Image) {
            String url = "jdbc:sqlite:sample.db";

            String sql = "INSERT INTO eeg_signals(username, electrode_number, plot_base64) VALUES(?,?,?)";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setInt(2, electrodeNumber);
                pstmt.setString(3, base64Image);
                // Wykonuje zapytanie wstawiające dane do bazy
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
