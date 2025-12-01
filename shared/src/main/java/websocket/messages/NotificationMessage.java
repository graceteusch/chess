package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
