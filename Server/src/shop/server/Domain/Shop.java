package shop.server.Domain;

import shop.common.Exceptions.*;
import shop.common.Entities.*;
import shop.common.Interface.ShopInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Shop implements ShopInterface {

    private Person userEingeloggt;
    private Personverwaltung pv;
    private Artikelverwaltung av;
    private Ereignisverwaltung ev;

    public Shop(String datei) throws IOException, ArtikelExistiertException, BenutzerExistiertException {
        this.pv = new Personverwaltung();
        this.ev = new Ereignisverwaltung();
        this.av = new Artikelverwaltung();

        pv.liesKunde("Kunde.txt");
        pv.liesMitarbeiter("Mitarbeiter.txt");
        av.liesArtikel("Artikel.txt");
        ev.liesEreignis("Ereignis.txt");
    }

    // < --------------------- Login/Register Methode ---------------------- >
    public Person getUserEingeloggt() {
        return userEingeloggt;
    }

    /**
     * Gibt Person als eingeloggten Nutzer wieder
     * @param username
     * @param passwort
     * @return
     * @throws LoginFailedException
     */
    public Person login(String username, String passwort) throws LoginFailedException {
        this.userEingeloggt = pv.einloggen(username, passwort);
        return this.userEingeloggt;
    }

    /**
     * Mitarbeiter ueber Personverwaltung registrieren
     * @param einMit
     * @return
     * @throws BenutzerExistiertException
     */
    public Mitarbeiter mRegister(Mitarbeiter einMit) throws BenutzerExistiertException {
        pv.mitRegister(einMit);
        try {
            pv.schreibeMitarbeiter("Mitarbeiter.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return einMit;
    }

    /**
     * Kunde ueber Personverwaltung registrieren
     * @param einKunde
     * @return
     * @throws BenutzerExistiertException
     */
    public Kunde kRegister(Kunde einKunde) throws BenutzerExistiertException {
        pv.kunRegister(einKunde);
        try {
            pv.schreibeKunde("Kunde.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return einKunde;
    }

    /**
     * Person ausgeloggt
     */
    public void logout() {
        userEingeloggt = null;
    }


    // < ------------------- Artikel/Mitarbeiter ------------------ >

    /**
     * Gib alle Artikel von der Liste aus
     * @return
     */
    public List<Artikel> gibAlleArtikel() {
        return av.getArtikelListe();
    }

    /**
     * Gib alle Ereignisse von der Liste aus
     * @return
     */
    public List<Ereignis> gibAlleEreignisse() {
        return ev.getEreignisListe();
    }

    /**
     * Artikel aus dem Shop loeschen
     * @param nr
     * @throws ArtikelExistiertNichtException
     * @throws IOException
     */
    public void loescheArtikel(int nr) throws ArtikelExistiertNichtException, IOException {
        Artikel artikel = av.sucheArtikelNachNummer(nr);
        av.artikelLoeschen(nr);

        String typ = "Auslagerung";
        ev.erstelleEreignis(userEingeloggt, artikel, 0, typ);
        ev.schreibeEreignis("Ereignis.txt");
        av.schreibeArtikel("Artikel.txt");
    }

    /**
     *  Artikel in der Artikelliste einfuegen
     * @param einArtikel
     * @throws ArtikelExistiertException
     * @throws PackunggroesseException
     * @throws IOException
     */
    public void artikelEinfuegen(Artikel einArtikel) throws ArtikelExistiertException, PackunggroesseException, IOException {
        av.artikelHinzufuegen(einArtikel);
        av.schreibeArtikel("Artikel.txt");

        String typ = "Einlagerung";
        ev.erstelleEreignis(userEingeloggt, einArtikel, einArtikel.getBestand(), typ);
        ev.schreibeEreignis("Ereignis.txt");
    }

    /**
     * Artikelbestand erhoehen
     * @param nr
     * @param menge
     * @return artikel
     * @throws ArtikelExistiertNichtException
     * @throws IOException
     * @throws PackunggroesseException
     */
    public Artikel bestandErhoehen(int nr, int menge) throws ArtikelExistiertNichtException, IOException, PackunggroesseException {
        av.bestandErhoehen(nr, menge);

        Artikel artikel = av.sucheArtikelNachNummer(nr);
        String typ = "Erhoehen";
        ev.erstelleEreignis(userEingeloggt, artikel, menge, typ);
        ev.schreibeEreignis("Ereignis.txt");
        av.schreibeArtikel("Artikel.txt"); // update artikellist
        return artikel;
    }

    public Artikel getArtikel(int nr)  throws ArtikelExistiertNichtException {
        Artikel artikel = av.sucheArtikelNachNummer(nr);
        if (artikel == null) {
            throw  new ArtikelExistiertNichtException();
        }
        return artikel;
    }


    // bestand history
    public List<Ereignis> bestandVerlauf(Artikel artikel)  {
       return av.bestandverlauf(artikel);
    }

    /**
     * Artikel nach Bezeichnung suchen
     * @param bezeichnung
     * @return
     */
    public List<Artikel> sucheNachBezeichnung(String bezeichnung) {
        return av.sucheArtikelNachBezeichnung(bezeichnung);
    }

    // < ------------------- Artikel/Warenkorb ------------------ >

    /**
     * Artikel in Warenkorb hinzufuegen
     * @param nummer
     * @param menge
     * @throws ArtikelExistiertNichtException
     * @throws BestandZuWenigException
     * @throws PackunggroesseException
     */
    public void inWk(int nummer, int menge) throws ArtikelExistiertNichtException, BestandZuWenigException, PackunggroesseException {
        Artikel artikel = av.sucheArtikelNachNummer(nummer);

        if (artikel instanceof MassengutArtikel ) {
          //  if ((artikel.getBestand() % (menge) != 0) || menge == 1 )
             if((menge % ((MassengutArtikel) artikel).getPackungGroesse()) != 0 || menge == 1) {
                throw new PackunggroesseException();
            }
        }
        if (artikel == null) {
            throw new ArtikelExistiertNichtException();
        } else if (artikel.getBestand() < menge + ((Kunde) userEingeloggt).getWarenkorb().getMenge(nummer)) {
            throw new BestandZuWenigException();
        } else
            av.artikelInWarenkorb(((Kunde) userEingeloggt).getWarenkorb(), artikel.getArtikelNummer(), menge);
    }

    /**
     * Warenkorb anzeigen
     * @return
     */
    public List<Artikel> zeigtWk() {
        return av.zeigt(((Kunde) userEingeloggt).getWarenkorb());
    }

    /**
     * Artikel aus Warenkorb entfernen
     * @param nummer
     * @throws ArtikelIstNichtImWkException
     */
    public void ausWk(int nummer) throws ArtikelIstNichtImWkException {
        Artikel artikel = av.sucheArtikelNachNummer(nummer);
        if (artikel == null) {
            throw new ArtikelIstNichtImWkException();
        }
        HashMap<Integer, Integer> warenkorbArtikelListe = ((Kunde) userEingeloggt).getWarenkorb().getWarenkorbMap();
        if (warenkorbArtikelListe.isEmpty()) {
            throw new ArtikelIstNichtImWkException();
        }
        av.artikelAusWk(((Kunde) userEingeloggt).getWarenkorb(), nummer);
    }

    /**
     * Artikel in Warenkorb loeschen
     * @throws WarenkorbIstLeerException
     */
    public void warenkorbLoeschen() throws WarenkorbIstLeerException {
        HashMap<Integer, Integer> warenkorbArtikelListe = ((Kunde) userEingeloggt).getWarenkorb().getWarenkorbMap();
        if (warenkorbArtikelListe.isEmpty()) {
            throw new WarenkorbIstLeerException();
        }
        av.warenkorbLoeschen(((Kunde) userEingeloggt).getWarenkorb());
    }

    /**
     * Warenkorb Kaufen
     * @return rechnung
     * @throws WarenkorbIstLeerException
     * @throws ArtikelExistiertNichtException
     * @throws BestandZuWenigException
     * @throws IOException
     */
    public Rechnung warenkorbKaufen() throws WarenkorbIstLeerException, ArtikelExistiertNichtException, BestandZuWenigException, IOException {
        Kunde kunde = (Kunde) userEingeloggt;
        Rechnung rechnung = av.warenkorbKaufen(kunde.getWarenkorb(), kunde);
        av.schreibeArtikel("Artikel.txt");

        return rechnung;
    }

    @Override
    public void disconnect() throws IOException {

    }

}
