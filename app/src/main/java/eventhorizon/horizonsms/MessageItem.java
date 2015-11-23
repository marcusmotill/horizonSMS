package eventhorizon.horizonsms;

/**
 * Created by marcusmotill on 11/22/15.
 */
public class MessageItem {

    private String messageBody;
    private String date;
    private boolean sent;

    public MessageItem(String messageBody, String date, boolean sent) {
        this.messageBody = messageBody;
        this.date = date;
        this.sent = sent;
    }


    public String getDate() {
        return date;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}

