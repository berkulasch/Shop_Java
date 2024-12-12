package ui.cui;

import shop.common.Entities.*;
import shop.common.Exceptions.*;
import shop.server.Domain.Shop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class CUI {
    private BufferedReader in;
    private Shop shop;


    public CUI(String datei) throws IOException, ArtikelExistiertException, BenutzerExistiertException {
        shop = new Shop(datei);

        // Stream-Objekt fuer Texteingabe ueber Konsolenfenster erzeugen
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private Person user() {
        return shop.getUserEingeloggt();
    }

    private void gibMenueAus() {
        if (user() == null) {
            mainMenue();
        } else {
            userMenu(user());
        }
    }

    private void userMenu(Person p) {
        if (p instanceof Mitarbeiter) {
            mitarbeiterMenue((Mitarbeiter) p);
        } else if (p instanceof Kunde) {
            kundeMenue((Kunde) p);
        } else {
            throw new RuntimeException("Unbekannte Art von Nutzer");
        }
    }

    private void mitarbeiterMenue(Mitarbeiter p) {
        System.out.print("\n Mitarbeiter Menue: ");
        System.out.print("         \n  ---------------------");
        System.out.print("         \n  Artikel anzeigen: 'a'");
        System.out.print("         \n  Artikel löschen: 'b'");
        System.out.print("         \n  Artikel einfügen: 'c'");
        System.out.print("         \n  Artikelbestand erhöhen: 'd'");
        System.out.print("         \n  Artikelsuchen nach Bezeichnung: 'e'");
        System.out.print("         \n  Mitarbeiter erstellen: 'f'");
        System.out.print("         \n  Bestandverlauf anzeigen : 'g'");
        System.out.print("         \n  ---------------------");
        System.out.print("         \n  Ausloggen: 'x'");
        System.out.println("         \n  Beenden: 'q'");
        System.out.flush();
    }

    private void kundeMenue(Kunde p) {
        System.out.print("\n Kunde Menue: ");
        System.out.print("         \n  ---------------------");
        System.out.print("         \n  Artikel anzeigen:  'a'");
        System.out.print("         \n  Artikel suchen:  'b'");
        System.out.print("         \n  Artikel in Warenkorb:  'c'");
        System.out.print("         \n  Warenkorb anzeigen: 'd'");
        System.out.print("         \n  Artikel aus Warenkorb: 'e'");
        System.out.print("         \n  Warenkorb loeschen: 'f'");
        System.out.print("         \n  Warenkorb kaufen:  'g'");
        System.out.print("         \n  ---------------------");
        System.out.print("         \n  Ausloggen:  'x'");
        System.out.print("         \n  Beenden:        'q'");
        System.out.flush();
    }

    private void mainMenue() {
        System.out.println("_________________SHOP_________________");
        System.out.print("\n Login: 'l'");
        System.out.print("\n Mitarbeiter Registrieren: 'm'");
        System.out.print("\n Kunde Registrieren: 'k'");
        System.out.print("\n ----------------------------------------");
        System.out.print("\n Beenden:'q'");
        System.out.print("\n ----------------------------------------\n");
        System.out.flush();
    }


    protected void startEingabe(String line) throws IOException {
        if (user() == null) {
            mainMenue(line);
        } else {
            startEingabe(line, user());
        }
    }


    private void startEingabe(String line, Person p) throws IOException {
        if (p instanceof Mitarbeiter) {
            mitarbeiterEingabe(line, (Mitarbeiter) p);
        } else if (p instanceof Kunde) {
            kundeEingabe(line, (Kunde) p);
        } else {
            throw new RuntimeException("Unbekannte Art von Nutzer");
        }
    }

    // Start Menu - Eingabe
    private void mainMenue(String line) throws IOException {
        String username;
        String passwort;
        String adresse;

        switch (line) {

            // Login
            case "l":
            case "L":
                System.out.print("Benutzername: ");
                username = liesEingabe();

                System.out.print("Passwort: ");
                passwort = liesEingabe();

                try {
                    Person p = shop.login(username, passwort);
                    System.out.print("Moin Moin ! " + p.getUsername());
                } catch (LoginFailedException e) {
                    System.err.println(e.getMessage());
                }
                // System.out.print("Moin Moin ! " + p.getUsername());
                break;

            // Mitarbeiter Registrierung
            case "m":
            case "M":
                System.out.print("Benutzername: ");
                username = liesEingabe();

                System.out.print("Passwort: ");
                passwort = liesEingabe();

                try {
                    Mitarbeiter einMit = new Mitarbeiter(username, passwort);
                    shop.mRegister(einMit);
                } catch (BenutzerExistiertException e) {
                    System.err.println(e.getMessage());
                }
                break;

            // Kunde Registrierung
            case "k":
            case "K":
                System.out.print("Benutzername: ");
                username = liesEingabe();

                System.out.print("Passwort: ");
                passwort = liesEingabe();

                System.out.print("Adresse: ");
                adresse = liesEingabe();
                try {
                    Kunde einKunde = new Kunde(username, passwort, adresse);
                    shop.kRegister(einKunde);
                } catch (BenutzerExistiertException e) {
                    System.err.println(e.getMessage());
                }
                break;

                default:
                System.out.println("Ungültige Eingabe");
        }

    }

    // Mitarbeiter Befehle
    private void mitarbeiterEingabe(String line, Mitarbeiter p) throws IOException {
        String nummer;
        int nr;
        int bestand;
        int preis;
        String bezeichnung;
        List<Artikel> liste;
        String username;
        String passwort;

        // Eingabe bearbeiten:
        switch (line) {
            case "a":
                //Alle Artikel
                liste = shop.gibAlleArtikel();
                gibArtikellisteAus(liste);
                break;
            case "b":
                //Artikel löschen
                System.out.print("Artikelnummer: ");
                nummer = liesEingabe();
                nr = Integer.parseInt(nummer);
                try {
                    shop.loescheArtikel(nr);
                } catch (ArtikelExistiertNichtException e) {
                    System.err.println(e.getMessage());
                }
                break;

            case "c":
                // Artikel einfuegen
                System.out.print("Bezeichnung: ");
                bezeichnung = liesEingabe();

                System.out.print("Massengutartikel? j/n: ");
                String artikelArt = liesEingabe();

                System.out.print("Preis: ");
                nummer = liesEingabe();
                preis = Integer.parseInt(nummer);

                System.out.print("Bestand: ");
                nummer = liesEingabe();
                bestand = Integer.parseInt(nummer);

                if (artikelArt.equals("j")) {
                    System.out.print("Packungsgroesse: ");
                    String packungsgroesseString = liesEingabe();
                    int packungsgroesse = Integer.parseInt(packungsgroesseString);

                    Artikel einArtikel = new MassengutArtikel(bezeichnung, preis, bestand, packungsgroesse);
                    try {
                        shop.artikelEinfuegen(einArtikel);
                    } catch (ArtikelExistiertException | PackunggroesseException e) {
                        System.err.println(e.getMessage());
                    }
                } else if (artikelArt.equals("n")) {
                    Artikel einArtikel = new Artikel(bezeichnung, preis, bestand);
                    try {
                        shop.artikelEinfuegen(einArtikel);
                    } catch (ArtikelExistiertException | PackunggroesseException e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    System.out.println("Bitte waehlen sie j/n.");
                }
                break;

            case "d":
                // Bestand erhoehen
                System.out.println("Artikelnummer: ");
                nummer = liesEingabe();
                nr = Integer.parseInt(nummer);

                System.out.print("Wie viele Artikel werden hinzugefuegt?: ");
                String count = liesEingabe();
                int menge = Integer.parseInt(count);

                try {
                    shop.bestandErhoehen(nr, menge);
                } catch (ArtikelExistiertNichtException e) {
                    System.err.println(e.getMessage());
                } catch (PackunggroesseException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "e":
                System.out.print("Artikelbezeichnung: ");
                bezeichnung = liesEingabe();
                liste = shop.sucheNachBezeichnung(bezeichnung);
                gibArtikellisteAus(liste);
                break;

            case "f":
                // Mitarbeiter register
                System.out.print("Benutzername: ");
                username = liesEingabe();

                System.out.print("Passwort: ");
                passwort = liesEingabe();

                try {
                    Mitarbeiter einMit = new Mitarbeiter(username, passwort);
                    shop.mRegister(einMit);
                } catch (BenutzerExistiertException e) {
                    System.err.println(e.getMessage());
                }
                break;

            case "g":
                // Bestand Verlauf
                System.out.println("Artikelnummer: ");
                nummer = liesEingabe();
                nr = Integer.parseInt(nummer);

                try {
                    Artikel artikel = shop.getArtikel(nr);
                    List<Ereignis> bestandVerlauf = shop.bestandVerlauf(artikel);
                    printBestandVerlauf(artikel, bestandVerlauf);

                } catch (ArtikelExistiertNichtException e) {
                    System.err.println(e.getMessage());
                }
                break;

            case "x":
                shop.logout();
                break;
        }
    }




    //Kunden Befehle
    private void kundeEingabe(String line, Kunde p) throws IOException {
        List<Artikel> liste;
        String bezeichnung;
        int nummer;
        int menge;
        String mn;
        String nr;

        switch (line) {
            case "a":
                // Artikel anzeigen
                liste = shop.gibAlleArtikel();
                gibArtikellisteAus(liste);
                break;
            case "b":
                // Artikel suchen
                System.out.print("Artikelbezeichnung: ");
                bezeichnung = liesEingabe();
                liste = shop.sucheNachBezeichnung(bezeichnung);
                gibArtikellisteAus(liste);
                break;
            case "c":
                //Artikel addieren in Wk
                System.out.print("Artikelnummer: ");
                nr = liesEingabe();
                nummer = Integer.parseInt(nr);

                System.out.print("Anzahl: ");
                mn = liesEingabe();
                menge = Integer.parseInt(mn);
                try {
                    shop.inWk(nummer, menge);
                } catch (ArtikelExistiertNichtException | BestandZuWenigException | PackunggroesseException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "d":
                //zeigt WK
                System.out.print("Warenkorb: ");
                liste = shop.zeigtWk();
                gibArtikellisteAus(liste);
                break;
            case "e":
                //Artikel aus Warenkorb nehmen
                System.out.println("Nummer des zu löschenden Artikels eingeben: ");
                nr = liesEingabe();
                nummer = Integer.parseInt(nr);
                try {
                    shop.ausWk(nummer);
                } catch (ArtikelIstNichtImWkException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "f":
                // Warenkorb loeschen
                try {
                    shop.warenkorbLoeschen();
                } catch (WarenkorbIstLeerException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "g":
                //Warenkorb kaufen
                try {
                   Rechnung rechnung = shop.warenkorbKaufen();
                   printRechnung(rechnung);
                } catch (WarenkorbIstLeerException | ArtikelExistiertNichtException | BestandZuWenigException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "x":
                shop.logout();
                break;
            default:
                System.out.println("Ungültige Eingabe");
        }
    }

    private void printBestandVerlauf(Artikel artikel, List<Ereignis> bestandVerlauf) {
        System.out.println("Artikel : "+ artikel.getArtikelNummer()+ " Aktueller Bestand : "+ artikel.getBestand());
        for (Ereignis e : bestandVerlauf) {
            System.out.println(e.getDatum() + ": " + " Bestand: " + (e.getArtikelanzahl()));
        }
    }

    public void printRechnung(Rechnung rechnung) {
        System.out.println("Rechnung");
        System.out.println("Kunde: " +rechnung.getKundenname());
        System.out.println("Datum: " +rechnung.getDatum());
        System.out.println("------------------------------");
        System.out.println("Artikel:");
        HashMap<Integer, Integer> gekaufteArtikelMap = rechnung.getArtikelMap();
        for (Integer artikelnummer : gekaufteArtikelMap.keySet()) {
            try {
                Artikel artikel = shop.getArtikel(artikelnummer);
                System.out.println(artikel.getArtikelNummer() +" : " +artikel.getBezeichnung()
                        +" Stückpreis " + artikel.getPreis() + " Anzahl : "+ gekaufteArtikelMap.get(artikelnummer));
            } catch (ArtikelExistiertNichtException e) {
                System.err.println("Artikel mit der " +artikelnummer +" konnte nicht gefunden werden ");
            }
        }
        System.out.println("------------------------------");
        System.out.println("Gesamtpreis: " + rechnung.getGesamtPreis() + " €");
    }

    private void gibArtikellisteAus(List liste) {
        if (liste.isEmpty()) {
            System.out.println("Liste ist leer.");
        } else {
            for (Object artikel : liste) {
                System.out.println(artikel);
            }
        }
    }

    private String liesEingabe() throws IOException {
        // einlesen von Konsole
        return in.readLine();

    }

    public void run() throws IOException {
        // Variable für Eingaben von der Konsole
        String input = "";

        // Hauptschleife der Benutzungsschnittstelle
        do {
            gibMenueAus();
            try {
                input = liesEingabe();
                startEingabe(input);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!input.equals("q"));
    }

    public static void main(String[] args) throws BenutzerExistiertException, ArtikelExistiertException {
        CUI cui;
        try {
            cui = new CUI("ShopCUI");
            cui.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}


