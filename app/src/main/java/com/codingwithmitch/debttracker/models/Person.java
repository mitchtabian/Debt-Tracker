package com.codingwithmitch.debttracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

    private String name;
    private String photo_uri;

    public Person(String name, String photo_uri) {
        this.name = name;
        this.photo_uri = photo_uri;
    }

    public Person() {
    }

    protected Person(Parcel in) {
        name = in.readString();
        photo_uri = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", photo_uri='" + photo_uri + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(photo_uri);
    }
}
