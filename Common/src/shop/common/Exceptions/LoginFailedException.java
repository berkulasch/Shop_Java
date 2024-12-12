package shop.common.Exceptions;

public class LoginFailedException extends Exception{
    public LoginFailedException() {
        super("Username oder Passwort ist falsch ");
    }

}
