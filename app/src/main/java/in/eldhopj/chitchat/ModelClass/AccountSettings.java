package in.eldhopj.chitchat.ModelClass;

public class AccountSettings {

    private String name;
    private String status;
    private String phoneNum;
    private String profilePic;
    private String thumbnail;
    private String lastSeen;

    public AccountSettings(String name, String status, String phoneNum, String profilePic, String thumbnail, String lastSeen) {
        this.name = name;
        this.status = status;
        this.phoneNum = phoneNum;
        this.profilePic = profilePic;
        this.thumbnail = thumbnail;
        this.lastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getLastSeen() {
        return lastSeen;
    }
}


