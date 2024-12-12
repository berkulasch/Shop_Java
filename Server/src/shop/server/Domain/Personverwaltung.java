package shop.server.Domain;
import shop.common.Entities.Kunde;
import shop.common.Entities.Mitarbeiter;
import shop.common.Entities.Person;
import shop.common.Exceptions.BenutzerExistiertException;
import shop.common.Exceptions.LoginFailedException;
import shop.server.Persistence.FilePersistenceManager;
import shop.server.Persistence.PersistenceManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Personverwaltung {
    private List <Mitarbeiter> mitarbeiterListe;
    private List <Kunde> kundenListe;
    private PersistenceManager pm;

    public Personverwaltung() {
        this.mitarbeiterListe = new ArrayList<>();
        this.kundenListe = new ArrayList<>();
        this.pm = new FilePersistenceManager();
    }

    /**
     * Check die datei fuer einloggen ein
     * @param username
     * @param passwort
     * @return kunde
     * @return mitarbeiter
     * @throws LoginFailedException
     */
    public Person einloggen(String username, String passwort) throws LoginFailedException {
        for (Kunde kunde:kundenListe) {
            if(username.equals(kunde.getUsername()) && passwort.equals(kunde.getPasswort())) {
                return kunde;
            }
        }
        for (Mitarbeiter mitarbeiter:mitarbeiterListe) {
            if(username.equals(mitarbeiter.getUsername()) && passwort.equals(mitarbeiter.getPasswort())) {
                return mitarbeiter;
            }
        }
            throw new LoginFailedException();
    }

    /**
     * Gib Mitarbeiter neue Id
     * @return nr
     */
    private int gibMitId() {
        int nr = 100;

        while (true) {
            boolean nrExistiert = false;

            for (Mitarbeiter mitarbeiter : mitarbeiterListe) {
                if (mitarbeiter.getMitarbeiterNr() == nr) {
                    nrExistiert = true;
                    break;
                }
            }
            if (!nrExistiert) {
                return nr;
            }
            nr++;
        }
    }

    /**
     * Gib Kunde neue Id
     * @return nr
     */
    private int gibKundeId() {
        int nr = 1000;

        while (true) {
            boolean nrExistiert = false;

            for (Kunde kunde : kundenListe) {
                if (kunde.getKundenNr() == nr) {
                    nrExistiert = true;
                    break;
                }
            }
            if (!nrExistiert) {
                return nr;
            }
            nr++;
        }
    }

    /**
     * Mitarbeiter mit neuer Id registrieren
     * @param einMitarbeiter
     * @return einMitarbeiter
     * @throws BenutzerExistiertException
     */
    public Mitarbeiter mitRegister(Mitarbeiter einMitarbeiter) throws BenutzerExistiertException {
        mitarbeiterEinfuegen(einMitarbeiter);
        int newMitarbeiternummer = gibMitId();
        einMitarbeiter.setMitarbeiterNr(newMitarbeiternummer);

        return einMitarbeiter;
    }

    /**
     * Fuege Mitarbeiter in der Liste ein
     * @param einMitarbeiter
     * @throws BenutzerExistiertException
     */
    public void mitarbeiterEinfuegen(Mitarbeiter einMitarbeiter) throws BenutzerExistiertException {
        if(suche(einMitarbeiter.getUsername(),einMitarbeiter.getMitarbeiterNr())){
            throw new BenutzerExistiertException(einMitarbeiter);
        } else {
            mitarbeiterListe.add(einMitarbeiter);
        }
    }

    /**
     * Kunde mit neuer Id registrieren
     * @param einKunde
     * @return einKunde
     * @throws BenutzerExistiertException
     */
    public Kunde kunRegister(Kunde einKunde)throws BenutzerExistiertException {
        kundeEinfuegen(einKunde);
        int newKundeNummer = gibKundeId();
        einKunde.setKundenNr(newKundeNummer);

        return einKunde;
    }

    /**
     * Fuege Kunde in der Liste ein
     * @param einKunde
     * @throws BenutzerExistiertException
     */
    public void kundeEinfuegen(Kunde einKunde) throws BenutzerExistiertException {
      if(suche(einKunde.getUsername(),einKunde.getKundenNr())){
          throw new BenutzerExistiertException(einKunde);
      } else {
          kundenListe.add(einKunde);
      }
    }

    /**
     * Check fuer registrieren ein
     * @param username
     * @param nummer
     * @return false
     */
    public boolean suche(String username, int nummer) {
        for (Mitarbeiter mitarbeiter : mitarbeiterListe) {
            if (mitarbeiter.getUsername().equals(username) || mitarbeiter.getMitarbeiterNr() == nummer)
                return true;
        }
        for (Kunde kunde : kundenListe) {
            if (kunde.getUsername().equals(username) || kunde.getKundenNr() == nummer)
                return true;
        }
        return false;
    }

    // < ----- Schreiben/Lesen Kunde-Mitarbeiter ------ >

    /**
     * Schreibe datei von Kunde
     * @param datei
     * @throws IOException
     */
    public void schreibeKunde(String datei) throws IOException {
        pm.openForWriting(datei);

        for (Kunde kunde : kundenListe) {
            pm.speicherKunde(kunde);

        }
        pm.close();
    }

    /**
     * Schreibe datei von Mitarbeiter
     * @param datei
     * @throws IOException
     */
    public void schreibeMitarbeiter(String datei) throws IOException {
        pm.openForWriting(datei);

        for (Mitarbeiter mitarbeiter : mitarbeiterListe) {
            pm.speicherMitarbeiter(mitarbeiter);

            }
        pm.close();
    }

    /**
     * Kunde datei lesen
     * @param datei
     * @throws IOException
     */
    public void liesKunde(String datei) throws IOException {
        pm.openForReading(datei);
        Kunde kunde;
        do {
            kunde = pm.ladeKunde();
            if (kunde != null) {
                try {
                    kundeEinfuegen(kunde);
                } catch (BenutzerExistiertException ignored) {

                }
            }
        } while (kunde != null);

        pm.close();
    }

    /**
     * Mitarbeiter datei lesen
     * @param datei
     * @throws IOException
     */
    public void liesMitarbeiter(String datei) throws IOException {
        pm.openForReading(datei);
        Mitarbeiter mitarbeiter;
        do {
            mitarbeiter = pm.ladeMitarbeiter();
            if (mitarbeiter != null) {
                try {
                    mitarbeiterEinfuegen(mitarbeiter);
                } catch (BenutzerExistiertException ignored) {
                }
            }
        } while (mitarbeiter != null);

        pm.close();
    }

}
