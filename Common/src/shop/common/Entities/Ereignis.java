package shop.common.Entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ereignis {

   private final String typ;
   private final String username;
   private final int artikelNr;
   private final int artikelanzahl;
   private final String datum;


    // Ereignis Constructor fuer erstellen
    public Ereignis(String username, String typ , int artikelNr, int artikelanzahl) {
        this.username = username;
        this.typ = typ;
        this.artikelNr = artikelNr;
        this.artikelanzahl = artikelanzahl;
        this.datum = generateDatum();

    }

    // Constructor fuer lesen und speichern
    public Ereignis(String username, String typ, int artikelNr, int artikelanzahl,String datum) {
        this.username = username;
        this.typ = typ;
        this.artikelNr = artikelNr;
        this.artikelanzahl = artikelanzahl;
        this.datum = datum;

    }

    public String getTyp() {
        return typ;
    }

    public int getArtikelNr() {
        return artikelNr;
    }

    public int getArtikelanzahl() {
        return artikelanzahl;
    }

    private String generateDatum() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm");
        return dateFormat.format(new Date());
    }

    public String getDatum() {
        return datum;
    }

    public Date getEreignisDatum() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm");
        try {
            return dateFormat.parse(datum);
        } catch (ParseException e) {
            return  null;
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {

        return  "(" + getTyp() + "," + getUsername() + "," + getArtikelanzahl() + "," + getEreignisDatum() + ")" ;
    }
}
