import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class ClientHandlerTest {
    private Server server;
    private ClientHandler clientHandler;
    private PrintWriter out;

    @Before
    public void setUp() throws IOException {
        server = mock(Server.class);
        Socket clientSocket = mock(Socket.class);
        BufferedReader in = mock(BufferedReader.class);
        out = mock(PrintWriter.class);


        lenient().when(clientSocket.getInputStream()).thenReturn(new ByteArrayInputStream("Test message".getBytes()));
        lenient().when(clientSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        lenient().when(clientSocket.isClosed()).thenReturn(false);
        lenient().when(in.readLine()).thenReturn("TestUser", "Test message", "/exit");
        lenient().when(clientSocket.isClosed()).thenReturn(false);

        clientHandler = new ClientHandler(server, clientSocket);
        clientHandler.setReader(in);
        clientHandler.setWriter(out);
    }

    @Test
    public void testRun() {
        clientHandler.start();

        verify(server, times(1)).broadcastMessage(anyString());
    }

    @Test
    public void testSendMessage() {
        clientHandler.sendMessage("Test message");

        verify(out, times(1)).println("Test message");
    }
}