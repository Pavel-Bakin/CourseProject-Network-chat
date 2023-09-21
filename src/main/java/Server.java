import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public void start() {
        try {
            loadServerSettings(); // Загрузка настроек из файла

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Сервер запущен на порту " + port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    clients.add(clientHandler);
                    clientHandler.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadServerSettings() {
        try {
            File file = new File("settings.txt");
            if (!file.exists()) {
                // Если файл не существует, создаем его и записываем настройки по умолчанию
                PrintWriter writer = new PrintWriter(file);
                writer.println("Port=8091");
                writer.close();
            }

            Properties properties = new Properties();
            FileInputStream input = new FileInputStream(file);
            properties.load(input);
            input.close();

            // Получение порта из файла настроек
            String portString = properties.getProperty("Port");
            if (portString != null && !portString.isEmpty()) {
                port = Integer.parseInt(portString);
            } else {
                // Значение порта не найдено, установите значение по умолчанию
                port = 8091;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) {
        int port = 8091; // Порт по умолчанию
        Server server = new Server(port);
        server.start();
    }
}