package com.QueensKitchenSpringBoot.controllers.UserProfilePic.Entity;

import javax.persistence.*;

@Entity
@Table(name = "profile_pic")
public class ProfilePic {

    @Id
    private String profile_id;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    private Long userId;

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getUser_id() {
        return userId;
    }

    public void setUser_id(Long user_id) {
        this.userId = user_id;
    }

    public ProfilePic() {
    }

    public ProfilePic(String name, String type, byte[] data,Long userId) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.userId = userId;
    }

}
