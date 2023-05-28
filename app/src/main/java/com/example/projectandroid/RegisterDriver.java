package com.example.projectandroid;

public class RegisterDriver {
    public String email;
    public String name;
    public String phone_number;
    public String car_number;
    public String uid;
    public String role;
    public String person_card;
    public String public_card;
    public String car_picture;

    public RegisterDriver() {}

    public RegisterDriver(String n, String e, String pn, String cn, String uid, String role) {
        this.name = n;
        this.email = e;
        this.phone_number = pn;
        this.car_number = cn;
        this.uid = uid;
        this.role = role;
//        this.person_card = personCard;
//        this.public_card = publicCard;
//        this.car_picture = carPicture;
    }
}