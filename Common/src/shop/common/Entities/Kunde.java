package shop.common.Entities;

public class Kunde extends Person {
    private String adresse;
    private int kundenNr;
    private Warenkorb warenkorb;

    // Kunde Constructor
    public Kunde(String username, String passwort, String adresse){
        super(username,passwort);
        this.adresse = adresse;
        this.warenkorb = new Warenkorb();
    }

    public int getKundenNr() {
        return kundenNr;
    }

    public void setKundenNr(int kundenNr) {
        this.kundenNr = kundenNr;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Warenkorb getWarenkorb() {
        return warenkorb;
    }

    public void setWarenkorb(Warenkorb warenkorb) {
        this.warenkorb = warenkorb;
    }

}
