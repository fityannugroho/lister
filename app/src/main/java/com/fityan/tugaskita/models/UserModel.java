package com.fityan.tugaskita.models;

public class UserModel {
  public static final String ID_FIELD = "id";
  public static final String EMAIL_FIELD = "email";
  public static final String NAME_FIELD = "name";

  private String id;
  private String email;
  private String name;


  public UserModel() {}


  public UserModel(String id, String email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
  }


  public String getId() {
    return id;
  }


  public void setId(String id) {
    this.id = id;
  }


  public String getEmail() {
    return email;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }
}
