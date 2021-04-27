package com.example.bloodpressuremonitor;

//TODO erre az osztályra is megcsinálni a bekötést (regisztráció, bejelentkezés)
public class Patient {
    private String id;
    private int identifier;
    private String name;
    private String gender;
    private String email;

    public Patient(int identifier, String name, String gender, String email) {
        this.identifier = identifier;
        this.name = name;
        this.gender = gender;
        this.email = email;
    }

    public Patient(){}

    public String _getId(){
        return id;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "identifier=" + identifier +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
