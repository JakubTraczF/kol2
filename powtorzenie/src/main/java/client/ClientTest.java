package client;

import databasecreator.Creator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.api.BeforeAll;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.SQLException;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientTest {

    // Wykonuje się przed uruchomieniem testów, tworząc bazę danych
    @BeforeAll
    public static void setup() {
        // Tworzenie bazy danych przy użyciu klasy Creator
        Creator.main(new String[]{});
    }

    // Test parametryzowany wczytujący dane z pliku CSV
    @ParameterizedTest
    @CsvFileSource(resources = "/test.csv", numLinesToSkip = 1)
    public void testSendData(String username, String filePath, String expectedImagePath) throws Exception {
        // Zakładamy, że metoda sendData została zmodyfikowana do pracy z gniazdami lub symulacji interakcji sieciowej
        Client.sendData(username, filePath);

        // Walidacja wyniku testu
        String base64Plot = getPlotFromDatabase(username, filePath); // Pobranie wygenerowanego obrazka z bazy danych
        BufferedImage actualImage = decodeBase64ToImage(base64Plot); // Dekodowanie base64 do obiektu BufferedImage
        BufferedImage expectedImage = ImageIO.read(new File(expectedImagePath)); // Odczytanie oczekiwanego obrazka z pliku

        assertTrue(compareImages(expectedImage, actualImage)); // Porównanie oczekiwanego i rzeczywistego obrazka
    }

    // Metoda pobierająca base64 plot z bazy danych na podstawie nazwy użytkownika i ścieżki do pliku
    private String getPlotFromDatabase(String username, String filePath) throws SQLException {
        // Połączenie z bazą danych (przykład użycia SQLite)
        Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");

        // Zapytanie SQL do pobrania base64 plot
        String sql = "SELECT plot FROM plots WHERE username = ? AND file_path = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, filePath);
        ResultSet rs = pstmt.executeQuery();

        // Jeśli znaleziono wynik, odczytujemy base64 plot
        String base64Plot = null;
        if (rs.next()) {
            base64Plot = rs.getString("plot");
        }

        // Zamknięcie zasobów
        rs.close();
        pstmt.close();
        conn.close();

        return base64Plot; // Zwraca base64 plot
    }

    // Metoda dekodująca base64 do obiektu BufferedImage
    private BufferedImage decodeBase64ToImage(String base64Str) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Str);
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image; // Zwraca obrazek jako BufferedImage
    }

    // Metoda porównująca dwa obrazki BufferedImage
    private boolean compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false; // Jeśli wymiary obrazków są różne, zwraca false
        }

        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false; // Jeśli piksele obrazków są różne, zwraca false
                }
            }
        }

        return true; // Wszystkie piksele są identyczne, zwraca true
    }
}
