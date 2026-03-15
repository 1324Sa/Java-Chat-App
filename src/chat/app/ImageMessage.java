package chat.app;

public class ImageMessage extends ChatMessage {
    private static final long serialVersionUID = 1L;
    private byte[] imageBytes;
    private String description; 

    public ImageMessage(String senderUsername, byte[] imageBytes, String description) {
        super(senderUsername); 
        this.imageBytes = imageBytes;
        this.description = description;
    }


    @Override
    public String getDisplayContent() {
        return "[IMAGE ATTACHED] - Description: " + description; 
    }
}
