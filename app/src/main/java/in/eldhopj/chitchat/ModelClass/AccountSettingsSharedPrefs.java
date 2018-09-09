package in.eldhopj.chitchat.ModelClass;

public class AccountSettingsSharedPrefs {
    private String name;
    private String status;

    public AccountSettingsSharedPrefs(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
