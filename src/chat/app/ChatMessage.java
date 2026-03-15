package chat.app;
import java.io.Serializable;
import java.time.LocalTime;


public abstract class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L; 
    
    private String senderUsername;
    private LocalTime timestamp;
    
    public ChatMessage(String senderUsername) {
        this.senderUsername = senderUsername;
        this.timestamp = LocalTime.now();
    }
    

    public abstract String getDisplayContent(); 
    
    
    public String getSenderUsername() {
        return senderUsername;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }
}