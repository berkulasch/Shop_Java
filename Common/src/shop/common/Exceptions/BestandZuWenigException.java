package shop.common.Exceptions;

public class BestandZuWenigException extends Exception{

    public BestandZuWenigException(){
        super("Artikelbestand ist für die angegebene Anzahl zu wenig");
    }
}
