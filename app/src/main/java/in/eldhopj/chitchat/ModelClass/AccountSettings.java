package in.eldhopj.chitchat.ModelClass;

public class AccountSettings {

    String name;
    String Status;

    public AccountSettings(String name, String status) {
        this.name = name;
        Status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
