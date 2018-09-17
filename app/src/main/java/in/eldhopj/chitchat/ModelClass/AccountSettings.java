package in.eldhopj.chitchat.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountSettings implements Parcelable {
    private static final String TAG = "AccountSettings";
    private String name;
    private String status;
    private String phoneNum;
    private String profilePic;
    private String thumbnail;
    private String lastSeen;
    private Boolean online;
    private String userId;

    public AccountSettings() {

    }

    public AccountSettings(String name, String status, String phoneNum, String profilePic, String thumbnail, String lastSeen, Boolean online) {
        this.name = name;
        this.status = status;
        this.phoneNum = phoneNum;
        this.profilePic = profilePic;
        this.thumbnail = thumbnail;
        this.lastSeen = lastSeen;
        this.online = online;
    }

    public AccountSettings(String name, String status, String phoneNum, String profilePic, String thumbnail, String lastSeen, Boolean online, String userId) {
        this.name = name;
        this.status = status;
        this.phoneNum = phoneNum;
        this.profilePic = profilePic;
        this.thumbnail = thumbnail;
        this.lastSeen = lastSeen;
        this.online = online;
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getOnline() {
        return online;
    }

    public String getUserId() {
        return userId;
    }

    protected AccountSettings(Parcel in) {
        name = in.readString();
        status = in.readString();
        phoneNum = in.readString();
        profilePic = in.readString();
        thumbnail = in.readString();
        lastSeen = in.readString();
        byte tmpOnline = in.readByte();
        online = tmpOnline == 0 ? null : tmpOnline == 1;
        userId = in.readString();
    }

    public static final Creator<AccountSettings> CREATOR = new Creator<AccountSettings>() {
        @Override
        public AccountSettings createFromParcel(Parcel in) {
            return new AccountSettings(in);
        }

        @Override
        public AccountSettings[] newArray(int size) {
            return new AccountSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(phoneNum);
        dest.writeString(profilePic);
        dest.writeString(thumbnail);
        dest.writeString(lastSeen);
        dest.writeByte((byte) (online == null ? 0 : online ? 1 : 2));
        dest.writeString(userId);
    }
}


