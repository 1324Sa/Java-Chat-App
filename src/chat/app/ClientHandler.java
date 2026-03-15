package chat.app;
import java.io.*;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream out;
    private String clientUsername;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            closeAllResources();
        }
    }

    @Override
    public void run() {
        
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) { 
            
            
            clientUsername = (String) in.readObject(); 
            if (clientUsername == null) return;
            
            
            TextMessage joinMessage = new TextMessage("SERVER", clientUsername + " has joined the chat.");
            server.broadcastMessage(joinMessage, this);

            
            Object receivedObject;
            while ((receivedObject = in.readObject()) != null) {
                if (receivedObject instanceof ChatMessage) {
                    ChatMessage message = (ChatMessage) receivedObject;
                    
                    
                    System.out.println("Received from " + message.getSenderUsername() + 
                                       " (" + message.getClass().getSimpleName() + "): " + 
                                       message.getDisplayContent());
                    
                    server.broadcastMessage(message, this); 
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client " + clientUsername + " disconnected due to error: " + e.getMessage());
        } finally {
            closeAllResources();
        }
    }
    

    public void sendMessage(ChatMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            
        }
    }
    
    private void closeAllResources() {
        server.removeClient(this); 
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
