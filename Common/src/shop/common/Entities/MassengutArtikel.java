package shop.common.Entities;

public class MassengutArtikel extends Artikel {

    private final int packungGroesse ;

    // Massengutartikel Constructor
    public MassengutArtikel(String bezeichnung, float preis, int bestand, int packungGroesse) {
        super(bezeichnung, preis, bestand);
        this.packungGroesse = packungGroesse;
    }

    public int getPackungGroesse() {
        return packungGroesse;
    }

    @Override
    public String toString() {

        return "(" + getPreis() + "€" + ", " + getBezeichnung() + ", " + getBestand() + ", Packunggroesse: " + packungGroesse + ", " + getArtikelNummer() + ")";
    }

}
