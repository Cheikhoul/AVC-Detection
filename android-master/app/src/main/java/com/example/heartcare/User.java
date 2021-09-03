package com.example.heartcare;
//Classe définissant l'Objet User pour permettre l'identification.
public class User {

    private String email;
    private String password;

    public User(String username,String password){
        this.email=username;
        this.password=password;
    }

    public String getMail() {
        return email;
    }

    public void setMail(String mail) {
        this.email = mail;
    }

    public String getMdp() {
        return password;
    }

    public void setMdp(String password) {
        this.password = password;
    }



}
