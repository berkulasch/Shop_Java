package shop.common.Interface;

import shop.common.Entities.*;
import shop.common.Exceptions.*;

import java.io.IOException;
import java.util.List;

public interface ShopInterface {

    // Login/Register
    public abstract Person login(String name, String passwort) throws LoginFailedException;

    public abstract Person getUserEingeloggt();

    public abstract Mitarbeiter mRegister(Mitarbeiter mitarbeiter) throws BenutzerExistiertException, IOException;

    public abstract Kunde kRegister(Kunde kunde) throws BenutzerExistiertException, IOException;

    public abstract void logout();

    //Artikel
    public abstract List<Artikel> gibAlleArtikel();

    public abstract List<Ereignis> gibAlleEreignisse();

    public abstract void loescheArtikel(int nr) throws ArtikelExistiertNichtException, IOException ;

    public abstract void artikelEinfuegen(Artikel einArtikel) throws ArtikelExistiertException, PackunggroesseException, IOException;

    public abstract Artikel bestandErhoehen(int nr, int menge) throws ArtikelExistiertNichtException, IOException, PackunggroesseException;

    public abstract Artikel getArtikel(int nr)  throws ArtikelExistiertNichtException;

    public abstract List<Artikel> sucheNachBezeichnung(String bezeichnung);

    // Warenkorb

    public abstract void inWk(int nummer, int menge) throws ArtikelExistiertNichtException, BestandZuWenigException, PackunggroesseException;

    public abstract List<Artikel> zeigtWk();

    public abstract void ausWk(int nummer) throws ArtikelIstNichtImWkException;

    public abstract void warenkorbLoeschen() throws WarenkorbIstLeerException;

    public abstract Rechnung warenkorbKaufen() throws WarenkorbIstLeerException, ArtikelExistiertNichtException, BestandZuWenigException, IOException;

    public abstract void disconnect() throws IOException;

}
