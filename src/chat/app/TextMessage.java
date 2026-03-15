
package chat.app;

public class TextMessage extends ChatMessage {
    private static final long serialVersionUID = 1L;
    private String text;

    public TextMessage(String senderUsername, String text) {
        super(senderUsername);
        this.text = text;
    }


    @Override
    public String getDisplayContent() {
        return this.text;
    }
}