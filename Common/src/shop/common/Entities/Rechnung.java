package shop.common.Entities;

import java.util.Date;
import java.util.HashMap;


public class Rechnung {
    private final Kunde kunde;
    private final Date datum;
    private HashMap<Integer, Integer> artikelMap;
    private double gesamtPreis;

    // Rechnung Constructor
    public Rechnung(Kunde kunde, HashMap<Integer, Integer> artikelMap, double gesamtPreis, Date datum) {
        this.kunde = kunde;
        this.artikelMap = new HashMap<>(artikelMap);
        this.gesamtPreis = gesamtPreis;
        this.datum = datum;
    }

    public String getKundenname() {
        return kunde.getUsername();
    }

    public Date getDatum() {
        return datum;
    }

    public HashMap<Integer, Integer> getArtikelMap() {
        return artikelMap;
    }

    public double getGesamtPreis() {
        return gesamtPreis;
    }
}
