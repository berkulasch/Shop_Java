package shop.common.Exceptions;

public class ArtikelExistiertNichtException extends Exception {

    public ArtikelExistiertNichtException() {
        super("Der Artikel mit der Nummer existiert nicht im Shop");
    }

}