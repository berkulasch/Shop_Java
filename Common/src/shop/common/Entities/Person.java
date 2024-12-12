package shop.common.Entities;

public class Person {
    protected String username;
    protected String password;

    // Constructor Person
    public Person(String username,String passwort) {
        this.username = username;
        this.password = passwort;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswort() {
        return password;
    }

    public void setPasswort(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }


}

