package com.moobasoft.yezna.rest.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {

    private int id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private Date createdAt;

    public User() {}

    public User(int id, String username, String avatar, Date createdAt) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /** Horrific Parcelable boilerplate */

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(username);
        out.writeString(email);
        out.writeString(password);
        out.writeString(avatar);
        out.writeLong(createdAt.getTime());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        id        = in.readInt();
        username  = in.readString();
        email     = in.readString();
        password  = in.readString();
        avatar    = in.readString();
        createdAt = new Date(in.readLong());
    }
}
