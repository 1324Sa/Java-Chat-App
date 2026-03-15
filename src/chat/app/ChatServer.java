package chat.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private final int PORT = 12345;

    public ChatServer() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server started on port " + PORT);
        } catch (IOException e) {
            System.err.println("Could not start server on port " + PORT);
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            
            while (!serverSocket.isClosed()) { 
                Socket clientSocket = serverSocket.accept(); 
                System.out.println("A new client connected: " + clientSocket);

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);        
                new Thread(handler).start(); 
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void broadcastMessage(ChatMessage message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) { 
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientToRemove) {
        clients.remove(clientToRemove);
        System.out.println("Client disconnected. Current users: " + clients.size());
    }
    
    private void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.startServer();
    }
}
