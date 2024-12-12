package shop.server.Persistence;

import shop.common.Entities.*;

import java.io.*;

public class FilePersistenceManager implements PersistenceManager {

    private BufferedReader reader = null;
    private PrintWriter writer = null;

    public void openForReading(String datei) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(datei));
    }

    public void openForWriting(String datei) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(datei)));
    }

    public boolean close() {
        if (writer != null) {
            writer.close();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String liesZeile() throws IOException {
        if (reader != null)
            return reader.readLine();
        else
            return "";
    }

    private void schreibeZeile(String daten) {
        if (writer != null)
            writer.println(daten);
    }

    private String leseBisZumErstenWert() throws IOException {
        String zeile = liesZeile();
        while (zeile != null && zeile.trim().isEmpty()) {
            zeile = liesZeile();
        }
        return zeile;
    }

    /**
     * Methode zum Einlesen der Kundedaten aus einer externen Datenquelle
     * @return kunde
     * @throws IOException
     */
    public Kunde ladeKunde() throws IOException {
        String username = leseBisZumErstenWert();
        if (username == null) {
            return null;
        }

        String passwort = leseBisZumErstenWert();
        if (passwort == null) {
            return null;
        }

        String adresse = leseBisZumErstenWert();
        if (adresse == null) {
            return null;
        }

        String nr = leseBisZumErstenWert();
        int nummer = Integer.parseInt(nr);

        Kunde kunde = new Kunde(username, passwort, adresse);
        kunde.setKundenNr(nummer);
        return kunde;
    }

    /**
     * Methode zum Schreiben der Kundedaten in eine externe Datenquelle
     * @param kunde
     * @return true
     */
    public boolean speicherKunde(Kunde kunde) {
        schreibeZeile(kunde.getUsername());
        schreibeZeile(kunde.getPasswort());
        schreibeZeile(kunde.getAdresse());
        schreibeZeile(kunde.getKundenNr() + "");
        return true;
    }

    /**
     * Methode zum Einlesen der Mitarbeiterdaten aus einer externen Datenquelle
     * @return mitarbeiter
     * @throws IOException
     */
    public Mitarbeiter ladeMitarbeiter() throws IOException {
        String username = leseBisZumErstenWert();
        if (username == null) {
            return null;
        }

        String passwort = leseBisZumErstenWert();
        if (passwort == null) {
            return null;
        }

        String nr = leseBisZumErstenWert();
        int nummer = Integer.parseInt(nr);

        Mitarbeiter mitarbeiter = new Mitarbeiter(username, passwort);
        mitarbeiter.setMitarbeiterNr(nummer);
        return mitarbeiter;
    }

    /**
     * Methode zum Schreiben der Mitarbeiterdaten in eine externe Datenquelle
     * @param mitarbeiter
     * @return true
     */
    public boolean speicherMitarbeiter(Mitarbeiter mitarbeiter) {
        schreibeZeile(mitarbeiter.getUsername());
        schreibeZeile(mitarbeiter.getPasswort());
        schreibeZeile(mitarbeiter.getMitarbeiterNr() + "");
        return true;
    }

    /**
     * Methode zum Einlesen der Artikeldaten aus einer externen Datenquelle
     * @return artikel
     * @throws IOException
     */
    public Artikel ladeArtikel() throws IOException {
        String bezeichnung = leseBisZumErstenWert();
        if (bezeichnung == null) {
            return null;
        }

        String preisStr = leseBisZumErstenWert();
        float preis = Float.parseFloat(preisStr);

        String bestandStr = leseBisZumErstenWert();
        int bestand = Integer.parseInt(bestandStr);

        String nr = leseBisZumErstenWert();
        int nummer = Integer.parseInt(nr);

        String packung = leseBisZumErstenWert();
        int packungGroesse = Integer.parseInt(packung);

        if (packungGroesse == -1) {
            Artikel artikel = new Artikel(bezeichnung, preis, bestand);
            artikel.setArtikelNummer(nummer);
            return artikel;
        } else {
            MassengutArtikel massengutArtikel = new MassengutArtikel(bezeichnung, preis, bestand, packungGroesse);
            massengutArtikel.setArtikelNummer(nummer);
            return massengutArtikel;
        }

    }

    /**
     * Methode zum Schreiben der Artikeldaten in eine externe Datenquelle
     * @param artikel
     * @return true
     */
    public boolean speicherArtikel(Artikel artikel) {
        schreibeZeile(artikel.getBezeichnung());
        schreibeZeile(String.valueOf(artikel.getPreis()));
        schreibeZeile(String.valueOf(artikel.getBestand()));
        schreibeZeile(artikel.getArtikelNummer() + "");

        if (artikel instanceof MassengutArtikel) {
            schreibeZeile(((MassengutArtikel) artikel).getPackungGroesse() + "");
        } else {
            schreibeZeile("-1");
        }
        return true;
    }

    /**
     * Methode zum Einlesen der Ereignisdaten aus einer externen Datenquelle
     * @return ereignis
     * @throws IOException
     */
    public Ereignis ladeEreignis() throws IOException {
        String benutzer = leseBisZumErstenWert();
        String typ = leseBisZumErstenWert();

        if (typ == null) {
            return null;
        }
        String artikelnr = leseBisZumErstenWert();
        int nummer = Integer.parseInt(artikelnr);

        String an = leseBisZumErstenWert();
        int anzahl = Integer.parseInt(an);

        String datum = leseBisZumErstenWert();

        Ereignis ereignis = new Ereignis(benutzer, typ, nummer, anzahl, datum);

        return ereignis;

    }

    /**
     * Methode zum Schreiben der Ereignisdaten in eine externe Datenquelle
     * @param e
     * @return true
     */
    public boolean speicherEreignis(Ereignis e) {
        schreibeZeile(e.getUsername());
        schreibeZeile(e.getTyp() + "");
        schreibeZeile(String.valueOf(e.getArtikelNr()) + "");
        schreibeZeile(String.valueOf(e.getArtikelanzahl()) + "");
        schreibeZeile(e.getDatum() + "");
        return true;
    }


}
