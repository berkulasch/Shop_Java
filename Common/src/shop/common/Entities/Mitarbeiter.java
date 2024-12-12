package shop.common.Entities;

public class Mitarbeiter extends Person {

    private int mitarbeiterNr;

    // Mitarbeiter Constructor
    public Mitarbeiter(String username,String passwort){
        super(username,passwort);
    }

    public void setMitarbeiterNr(int mitarbeiterNr) {
        this.mitarbeiterNr = mitarbeiterNr;
    }

    public int getMitarbeiterNr() {
        return mitarbeiterNr;
    }

}
