package eventhorizon.horizonsms;

/**
 * Created by marcusmotill on 11/22/15.
 */
public class ConversationItem {
    private String contactName;
    private String previousMessage;
    private String address;

    public ConversationItem(String contactName, String previousMessage, String address) {
        this.contactName = contactName;
        this.previousMessage = previousMessage;
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPreviousMessage() {
        return previousMessage;
    }

    public String getAddress() {
        return address;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPreviousMessage(String previousMessage) {
        this.previousMessage = previousMessage;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
