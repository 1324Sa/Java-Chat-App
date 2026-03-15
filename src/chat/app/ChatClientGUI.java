package chat.app;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import javax.swing.*;


public class ChatClientGUI extends JFrame {
    
    
    private JTextArea messageArea;
    private JTextField messageField;
    
    
    private Socket socket;
    private ObjectInputStream in; 
    private ObjectOutputStream out;
    private String username;
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 12345;
    
    private static final DateTimeFormatter TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public ChatClientGUI(String username) {
        this.username = username;
        
        
        setTitle("Chat Client - User: " + username);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);
        
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
        
        
        ActionListener sendListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };
        sendButton.addActionListener(sendListener);
        messageField.addActionListener(sendListener);
        
        setVisible(true);
        
        connectToServer();
        listenForMessages();
    }
    
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            
            out.writeObject(username);
            out.flush();

        } catch (IOException e) {
            appendMessage("SYSTEM: Error connecting to server: " + e.getMessage());
            closeAllResources();
        }
    }
    

    private void sendMessage() {
        String input = messageField.getText().trim();
        if (input.isEmpty()) return;
        
        ChatMessage messageToSend = null;
        
        
        if (input.equalsIgnoreCase("/exit")) {
            appendMessage("SYSTEM: Disconnecting...");
            closeAllResources();
            System.exit(0);
        } else if (input.toLowerCase().startsWith("/image ")) {
            String description = input.substring(7).trim();
            byte[] dummyImageBytes = new byte[0]; 
            messageToSend = new ImageMessage(username, dummyImageBytes, description);
        } else {
            messageToSend = new TextMessage(username, input);
        }

        try {
            if (messageToSend != null) {
                out.writeObject(messageToSend);
                out.flush();
                
                appendMessage(formatMessage(messageToSend)); 
                messageField.setText("");
            }
        } catch (IOException e) {
            appendMessage("SYSTEM: Error sending message: " + e.getMessage());
            closeAllResources();
        }
    }


    public void listenForMessages() {
        Thread listenerThread = new Thread(() -> {
            Object receivedObject;
            try {
                while ((receivedObject = in.readObject()) != null) {
                    if (receivedObject instanceof ChatMessage) {
                        ChatMessage message = (ChatMessage) receivedObject;
                        
                         
                        SwingUtilities.invokeLater(() -> {
                            
                            if (!message.getSenderUsername().equals(username)) {
                                appendMessage(formatMessage(message));
                            }
                        });
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                SwingUtilities.invokeLater(() -> 
                    appendMessage("SYSTEM: Connection lost to server."));
            } finally {
                closeAllResources();
            }
        });
        listenerThread.start();
    }


    private String formatMessage(ChatMessage message) {
        String time = message.getTimestamp().format(TIME_FORMATTER);
        String messageType = message.getClass().getSimpleName();
        
        return String.format("[%s] <%s> (%s): %s",
                time,
                message.getSenderUsername(),
                messageType,
                message.getDisplayContent());
    }


    private void appendMessage(String text) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(text + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    private void closeAllResources() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            
        }
    }

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog(
                    null, 
                    "Enter your username:", 
                    "Welcome to Chat", 
                    JOptionPane.PLAIN_MESSAGE);
            
            if (username != null && !username.trim().isEmpty()) {
                new ChatClientGUI(username.trim());
            } else {
                System.exit(0);
            }
        });
    }
}