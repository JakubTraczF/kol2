package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter username:");
        String username = scanner.nextLine();

        System.out.println("Enter path to CSV file:");
        String filepath = scanner.nextLine();

        try {
            sendData(username, filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    public static void sendData(String name, String filepath) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new FileReader(filepath))) {

            // Send the username
            out.println(name);

            // Read and send the file content line by line
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
                Thread.sleep(2000); // wait for 2 seconds
            }

            // Inform the server that sending is finished
            out.println("bye");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
