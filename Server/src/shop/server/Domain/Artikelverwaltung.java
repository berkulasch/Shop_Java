package shop.server.Domain;


import shop.common.Entities.*;
import shop.common.Exceptions.*;
import shop.server.Persistence.FilePersistenceManager;
import shop.server.Persistence.PersistenceManager;

import java.io.IOException;
import java.util.*;

public class Artikelverwaltung {

    private final Ereignisverwaltung ev;
    private List<Artikel> artikelListe;
    private PersistenceManager pm;

    public List<Artikel> getArtikelListe() {
        return (artikelListe);
    }

    public Artikelverwaltung() {
        this.artikelListe = new Vector<>();
        this.pm = new FilePersistenceManager();
        this.ev = new Ereignisverwaltung();
    }


    // < ------ Mitarbeiter ------ >

    /**
     * Artikel in der Liste hinzufuegen
     * @param einArtikel
     * @throws ArtikelExistiertException
     * @throws PackunggroesseException
     */
    public void artikelHinzufuegen(Artikel einArtikel) throws ArtikelExistiertException, PackunggroesseException {
        for (Artikel artikel : artikelListe) {
            if (einArtikel.getBezeichnung().equals(artikel.getBezeichnung())) {
                throw new ArtikelExistiertException(einArtikel);
            }
        }
        if (einArtikel instanceof MassengutArtikel) {
            if ((einArtikel.getBestand() % ((MassengutArtikel) einArtikel).getPackungGroesse() != 0)) {
                throw new PackunggroesseException();
            }
            if(((MassengutArtikel) einArtikel).getPackungGroesse() == 1){
              throw new PackunggroesseException();
            }
        }
        if (einArtikel.getArtikelNummer() < 0) {
            int newArtikelNummer = gibArtikelId();
            einArtikel.setArtikelNummer(newArtikelNummer);
        }
        artikelListe.add(einArtikel);
    }

    /**
     * Artikel Id erstellen
     * @return artikel
     */
    private int gibArtikelId() {
        int nr = -1;

        for (Artikel artikel : artikelListe) {
            nr = Math.max(nr, artikel.getArtikelNummer());
        }
        if (nr < 0) {
            return 2000;
        }
        return ++nr;
    }

    /**
     * Artikelbestand erhoehen
     * @param artikelnummer
     * @param menge
     * @throws ArtikelExistiertNichtException
     * @throws PackunggroesseException
     */
    public void bestandErhoehen(int artikelnummer, int menge) throws ArtikelExistiertNichtException, PackunggroesseException {
        for (Artikel artikel : artikelListe) {
            if (artikel.getArtikelNummer() == artikelnummer) {
                if (artikel instanceof MassengutArtikel) {
                    if (artikel.getBestand() != 0 && menge % ((MassengutArtikel) artikel).getPackungGroesse() != 0) {
                        throw new PackunggroesseException();
                    }
                }
                artikel.setBestand(artikel.getBestand() + menge);
                return;
            }
        }
        throw new ArtikelExistiertNichtException();
    }

    /**
     * Artikel loeschen aus dem Shop
     * @param artikelnummer
     * @throws ArtikelExistiertNichtException
     */
    public void artikelLoeschen(int artikelnummer) throws ArtikelExistiertNichtException {
        Artikel loeschen = null;
        for (Artikel artikel : artikelListe) {
            if (artikel.getArtikelNummer() == artikelnummer) {
                loeschen = artikel;
                break;
            }
        }
        if (loeschen != null) {
            artikelListe.remove(loeschen);
        } else {
            throw new ArtikelExistiertNichtException();
        }
    }

    /**
     * Artikel nach der nummer suchen
     * @param nummer
     * @return
     */
    public Artikel sucheArtikelNachNummer(int nummer) {
        for (Artikel artikel : artikelListe) {
            if (artikel.getArtikelNummer() == nummer) {
                return artikel;
            }
        }
        return null;
    }

    /**
     * Artikel nach der bezeichnung suchen
     * @param bezeichnung
     * @return
     */
    public List<Artikel> sucheArtikelNachBezeichnung(String bezeichnung) {
        List<Artikel> suchErg = new ArrayList<>();
        for (Artikel artikel : artikelListe) {
            if (artikel.getBezeichnung().equals(bezeichnung))
                suchErg.add(artikel);
        }
        return suchErg;
    }

    // < ------ Kunde/Warenkorb ------ >


    /**
     * Artikel in Warenkorb hinzufuegen
     * @param warenkorb
     * @param nummer
     * @param anzahl
     */
    public void artikelInWarenkorb(Warenkorb warenkorb, int nummer, int anzahl) {
        warenkorb.inWk(nummer, anzahl);
    }

    /**
     * Warenkorb anzeigen
     * @param warenkorb
     * @return warenkorbArtikelListe
     */
    public List<Artikel> zeigt(Warenkorb warenkorb) {
        HashMap<Integer, Integer> warenkorbArtikel = warenkorb.getWarenkorbMap();
        List<Artikel> warenkorbArtikelListe = new ArrayList<>();

        for (int artikelNummer : warenkorbArtikel.keySet()) {
            // Iteration über jede Artikelnummer im Warenkorb
            Artikel artikel = sucheArtikelNachNummer(artikelNummer);

            if (artikel != null) {
                int menge = warenkorbArtikel.get(artikelNummer);
                float preis = artikel.getPreis() * menge;
                int bestand = artikel.getBestand();

                Artikel wkArt = new Artikel(artikel.getBezeichnung(), preis, bestand);
                wkArt.setArtikelNummer(artikelNummer);
                warenkorbArtikelListe.add(wkArt);
            }
        }
        return warenkorbArtikelListe;
    }

    /**
     * Artikel aus Warenkorb entfernen
     * @param warenkorb
     * @param nummer
     * @return
     */
    public boolean artikelAusWk(Warenkorb warenkorb, int nummer) {
        return warenkorb.artikelEntfernen(nummer);
    }

    /**
     * Warenkorb loeschen
     * @param warenkorb
     */
    public void warenkorbLoeschen(Warenkorb warenkorb) {
        warenkorb.clearWarenkorb();
    }

    /**
     * Warenkorb kaufen
     * @param warenkorb
     * @param kunde
     * @return rechnung
     * @throws WarenkorbIstLeerException
     * @throws ArtikelExistiertNichtException
     * @throws BestandZuWenigException
     */
    public Rechnung warenkorbKaufen(Warenkorb warenkorb, Kunde kunde) throws WarenkorbIstLeerException, ArtikelExistiertNichtException, BestandZuWenigException {
        HashMap<Integer, Integer> warenkorbArtikelListe = warenkorb.getWarenkorbMap();

        if (warenkorb.isEmpty()) {
            throw new WarenkorbIstLeerException();
        }

        for (Integer artikelNummer : warenkorbArtikelListe.keySet()) {
            int bestandImWarenkorb = warenkorbArtikelListe.get(artikelNummer); // Stückzahl des Artikels im Warenkorb
            Artikel artikel = sucheArtikelNachNummer(artikelNummer); // Suchen des Artikels anhand der Artikelnummer

            if (artikel instanceof MassengutArtikel) {
                int aktBestand = artikel.getBestand();
               // int packung = ((MassengutArtikel) artikel).getPackungGroesse();
                if (bestandImWarenkorb <= aktBestand) {
                  //  int neu = aktBestand - (packung * bestandImWarenkorb);
                    int neu = aktBestand - bestandImWarenkorb;

                    if (neu < 0) {
                        throw new BestandZuWenigException();
                    }
                    aktualisiereBestandUndErstelleEreignis(neu, artikel, kunde);
                }

            } else if (artikel != null) {
                int aktuellerBestand = artikel.getBestand();

                if (bestandImWarenkorb <= aktuellerBestand) {
                    int neuerBestand = aktuellerBestand - bestandImWarenkorb;
                    // Aktualisieren des Lagerbestands des Artikels
                    aktualisiereBestandUndErstelleEreignis(neuerBestand, artikel, kunde);
                } else {
                    throw new BestandZuWenigException();
                }
            } else {
                throw new ArtikelExistiertNichtException();
            }
        }

        double gesamtPreis = berechneGesamtPreis(warenkorbArtikelListe); // berechnen der gesamten Preis
        Date datum = new Date();

        Rechnung rechnung = new Rechnung(kunde, warenkorbArtikelListe, gesamtPreis, datum);

        warenkorb.clearWarenkorb();
        return rechnung;


    }

    private void aktualisiereBestandUndErstelleEreignis(int neuerBestand, Artikel artikel, Kunde kunde) {
        artikel.setBestand(neuerBestand);
        try {
            bestandverlauf(artikel);
            String typ = "Verkauf";
            ev.erstelleEreignis(kunde, artikel, neuerBestand, typ);
            ev.schreibeEreignis("Ereignis.txt");
            bestandverlauf(artikel);

        } catch (Exception ignored) {

        }
    }

    public List<Ereignis> bestandverlauf(Artikel artikel) {
        List<Ereignis> ereignisList = ev.ereigniseFuer(artikel,30);
        ereignisList.sort(new Comparator<Ereignis>() {
            @Override
            public int compare(Ereignis o1, Ereignis o2) {
                return Long.compare(o2.getEreignisDatum().getTime() , o1.getEreignisDatum().getTime());
            }
        });
       return  ereignisList;
    }


    /**
     * gesamt Preis von Kauf berechnen
     * @param warenkorbArtikelListe
     * @return gesamtPreis
     */
    private double berechneGesamtPreis(HashMap<Integer, Integer> warenkorbArtikelListe) {
        double gesamtPreis = 0.0;

        for (Integer artikelNummer : warenkorbArtikelListe.keySet()) {
            int bestandImWarenkorb = warenkorbArtikelListe.get(artikelNummer);
            Artikel artikel = sucheArtikelNachNummer(artikelNummer);

            if (artikel != null) {
                double artikelPreis = artikel.getPreis();
                gesamtPreis += artikelPreis * bestandImWarenkorb;
            }
        }

        return gesamtPreis;
    }


    // < ------ Schreiben/Lesen Artikel ------ >

    /**
     * Lese die Artikeldatei
     * @param datei
     * @throws IOException
     */
    public void liesArtikel(String datei) throws IOException {
        pm.openForReading(datei);
        Artikel artikel;
        do {
            artikel = pm.ladeArtikel();
            if (artikel != null) {
                try {
                    artikelHinzufuegen(artikel);
                } catch (ArtikelExistiertException | PackunggroesseException ignored) {
                }
            }
        } while (artikel != null);
        pm.close();
    }

    /**
     * Schreibe die Artikeldatei
     * @param datei
     * @throws IOException
     */
    public void schreibeArtikel(String datei) throws IOException {
        pm.openForWriting(datei);
        for (Artikel a : artikelListe) {
            pm.speicherArtikel(a);
        }
        pm.close();
    }


}
