package com.example.blogapp.Model;

public class User {
    private int id;
    private String userName, photo;

    public User() {
    }

    public User(int id, String userName, String photo) {
        this.id = id;
        this.userName = userName;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
