package com.socialnetwork.socialnetworkapp.domain;

import java.util.Objects;

public class User extends Entity<Long>{
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    public User(String firstName, String lastName, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public String toString(){
        return "User id" + this.getId() + "{" +
                "\nfirstName = " + this.firstName +
                "\nlastName = " + this.lastName +
                "\nemail = " + this.email +
                "\n}";
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(!(obj instanceof User)) return false;
        User other = (User) obj;
        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getId());
    }
}
