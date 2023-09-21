import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler extends Thread {
    private final Server server;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private PrintWriter fileLogWriter;

    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public void setReader(BufferedReader reader) {
        this.in = reader;
    }

    public void setWriter(PrintWriter writer) {
        this.out = writer;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String userName = in.readLine();
            System.out.println(userName + " присоединился к чату.");
            server.broadcastMessage(userName + " присоединился к чату.");

            String message;
            fileLogWriter = new PrintWriter(new FileWriter("file.log", true), true); // Создание PrintWriter для записи в file.log

            while ((message = in.readLine()) != null) {
                if ("/exit".equals(message)) {
                    System.out.println(userName + " вышел из чата.");
                    fileLogWriter.println("[" + new Date() + "] " + userName + " вышел из чата."); // Запись в файл
                    break;
                }

                String formattedMessage = "[" + new Date() + "] " + userName + ": " + message;
                System.out.println(formattedMessage);
                server.broadcastMessage(formattedMessage);
                fileLogWriter.println(formattedMessage); // Запись в файл
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                fileLogWriter.close(); // Закрыть PrintWriter для записи в файл
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}