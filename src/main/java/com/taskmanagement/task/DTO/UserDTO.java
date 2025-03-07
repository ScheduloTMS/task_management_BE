package com.taskmanagement.task.DTO;

public class UserDTO {

    private String userId;
    private String name;
    private String email;
    private byte[] photo;


    public UserDTO() {}

    public UserDTO(String userId, String name, String email, byte[] photo) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.photo = photo;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
