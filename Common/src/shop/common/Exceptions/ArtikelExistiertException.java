package shop.common.Exceptions;


import shop.common.Entities.Artikel;

public class ArtikelExistiertException extends Exception{

    private Artikel artikel;
    public ArtikelExistiertException(Artikel a) {
        super("Der Artikel existiert bereits");
        this.artikel = a;
    }

    public Artikel getArtikel() {
        return this.artikel;
    }

}
