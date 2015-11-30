package com.moobasoft.yezna.rest.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Question implements Parcelable {

    public static final int MIN_LENGTH = 10;

    private int id;
    private boolean isPublic;
    private String question;
    private String image;
    private String url;
    private int noes;
    private int yeses;
    private Date createdAt;
    private int timeLimit;
    private User user;

    public Question() {}

    public Question(int id, boolean isPublic, String question, String image, String url, int noes,
                    int yeses, Date createdAt, int timeLimit, User user) {
        this.id        = id;
        this.isPublic  = isPublic;
        this.question  = question;
        this.image     = image;
        this.url       = url;
        this.noes      = noes;
        this.yeses     = yeses;
        this.createdAt = createdAt;
        this.timeLimit = timeLimit;
        this.user      = user;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Question && ((Question)o).getId() == getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNoes() {
        return noes;
    }

    public void setNoes(int noes) {
        this.noes = noes;
    }

    public int getYeses() {
        return yeses;
    }

    public void setYeses(int yeses) {
        this.yeses = yeses;
    }

    public String getCreatedAt() {
        return DateFormat.getDateInstance(
                DateFormat.LONG, Locale.getDefault()).format(createdAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /** Horrific Parcelable boilerplate */

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeByte((byte) (isPublic ? 1 : 0));
        out.writeString(question);
        out.writeString(image);
        out.writeString(url);
        out.writeInt(noes);
        out.writeInt(yeses);
        out.writeLong(createdAt.getTime());
        out.writeParcelable(user, flags);
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    private Question(Parcel in) {
        id        = in.readInt();
        isPublic  = in.readByte() == 1;
        question  = in.readString();
        image     = in.readString();
        url       = in.readString();
        noes      = in.readInt();
        yeses     = in.readInt();
        createdAt = new Date(in.readLong());
        user      = in.readParcelable(User.class.getClassLoader());
    }
}