package shop.common.Exceptions;


import shop.common.Entities.Person;

public class BenutzerExistiertException extends Exception{

    // Attribute
    private Person user;

    // Konstruktor
    public BenutzerExistiertException(Person a) {
        super("Der User " + " mit dem Nutzernamen '" + a.getUsername() + "' existiert bereits im System.");
        this.user = a;
    }

}
