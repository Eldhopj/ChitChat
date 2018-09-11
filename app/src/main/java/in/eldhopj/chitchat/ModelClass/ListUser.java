package in.eldhopj.chitchat.ModelClass;

public class ListUser {
    private String name;
    private String phone;
    private String thumbImageUrl;


    public ListUser(String name, String phone, String thumbImageUrl) {
        this.name = name;
        this.phone = phone;
        this.thumbImageUrl = thumbImageUrl;
    }

    //For fetching device contacts
    public ListUser(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public void setThumbImageUrl(String thumbImageUrl) {
        this.thumbImageUrl = thumbImageUrl;
    }
}
