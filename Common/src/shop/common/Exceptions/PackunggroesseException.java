package shop.common.Exceptions;

public class PackunggroesseException extends Exception{
    public PackunggroesseException(){
        super("Die Anzahl soll Vielfaches der Packunggroesse sein!");
    }
}
