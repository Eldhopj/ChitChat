package in.eldhopj.chitchat.ModelClass;

public class PhoneContacts {
    private String name;
    private String phone;

    //For fetching device contacts
    public PhoneContacts(String name, String phone) {
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
}
