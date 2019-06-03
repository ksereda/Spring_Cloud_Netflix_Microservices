package com.example.hystrix.exqmple_3;

public class UserModel {

    private Long id;
    private String username;
    private String personalUserNumber;
    private String title;
    private Long postNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonalUserNumber() {
        return personalUserNumber;
    }

    public void setPersonalUserNumber(String personalUserNumber) {
        this.personalUserNumber = personalUserNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(Long postNumber) {
        this.postNumber = postNumber;
    }
}
