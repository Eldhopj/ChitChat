package in.eldhopj.chitchat.ModelClass;

public class Conversations {

    String message;
    long timeStamp;
    Boolean seen;
    String type;
    String from;

    public Conversations() {
    }

    public Conversations(String message, long timeStamp, Boolean seen, String type,String from) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.seen = seen;
        this.type = type;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Boolean getSeen() {
        return seen;
    }
}
