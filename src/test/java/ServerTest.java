import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServerTest {
    private Server server;

    @Mock
    private ServerSocket serverSocket;

    @BeforeEach
    public void setUp() {
        server = new Server(8091);
    }

    @Test
    public void testStart() throws IOException {
        Socket clientSocket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(clientSocket);

        server.start();

        verify(serverSocket, times(1)).accept();
    }

    @Test
    public void testBroadcastMessage() {
        ClientHandler client1 = mock(ClientHandler.class);
        ClientHandler client2 = mock(ClientHandler.class);
        server.getClients().add(client1);
        server.getClients().add(client2);

        server.broadcastMessage("Test message");

        verify(client1, times(1)).sendMessage("Test message");
        verify(client2, times(1)).sendMessage("Test message");
    }
}