package in.eldhopj.chitchat.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AccountSettings implements Parcelable{
    private static final String TAG = "AccountSettings";
    private String name;
    private String status;
    private String phoneNum;
    private String profilePic;
    private String thumbnail;
    private String lastSeen;
    private Boolean online;

    public AccountSettings(String name, String status, String phoneNum, String profilePic, String thumbnail, String lastSeen, Boolean online) {
        this.name = name;
        this.status = status;
        this.phoneNum = phoneNum;
        this.profilePic = profilePic;
        this.thumbnail = thumbnail;
        this.lastSeen = lastSeen;
        this.online = online;
        Log.d(TAG, "AccountSettings: "+online);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
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
    }
}


