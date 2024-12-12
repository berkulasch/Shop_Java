package shop.common.Exceptions;

public class ArtikelIstNichtImWkException extends Exception {

    public ArtikelIstNichtImWkException(){
        super("Artikel ist nicht im Warenkorb!");
    }
}
