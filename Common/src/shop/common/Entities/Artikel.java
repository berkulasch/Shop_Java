package shop.common.Entities;

public class Artikel {

    private float preis;
    private String bezeichnung;
    private int bestand;
    private int artikelNummer = -1 ;


    // Artikel Constructor
    public Artikel(String bezeichnung, float preis, int bestand) {
        this.preis = preis;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.artikelNummer = artikelNummer;

    }

    public float getPreis() {
        return preis;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getBestand() {
        return bestand;
    }

    public void setPreis(float preis) {
        this.preis = preis;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void setBestand(int bestand) {
        this.bestand = bestand;
    }

    public int getArtikelNummer() {
        return artikelNummer;
    }
    public void setArtikelNummer(int artikelNummer) {
        this.artikelNummer = artikelNummer;
    }
    @Override
    public String toString() {

        return  "(" + preis + "€" + ", " + bezeichnung + ", " + bestand + ", " + artikelNummer + ")" ;
    }

}
