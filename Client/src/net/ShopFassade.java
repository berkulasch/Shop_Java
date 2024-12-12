package net;

import shop.common.Entities.*;
import shop.common.Exceptions.*;
import shop.common.Interface.ShopInterface;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ShopFassade implements ShopInterface {
    // Datenstrukturen für die Kommunikation
    private Socket socket = null;
    private BufferedReader sin; // server-input stream
    private PrintStream sout; // server-output stream
    private Person person;

    /**
     * Konstruktor, der die Verbindung zum Server aufbaut (Socket) und dieser
     * Grundlage Eingabe- und Ausgabestreams für die Kommunikation mit dem
     * Server erzeugt.
     *
     * @param host Rechner, auf dem der Server läuft
     * @param port Port, auf dem der Server auf Verbindungsanfragen warten
     */
    public ShopFassade(String host, int port) throws IOException {
        try {
            // Socket-Objekt fuer die Kommunikation mit Host/Port erstellen
            socket = new Socket(host, port);

            // Stream-Objekt fuer Text-I/O ueber Socket erzeugen
            InputStream is = socket.getInputStream();
            sin = new BufferedReader(new InputStreamReader(is));
            sout = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Fehler beim Socket-Stream öffnen: " + e);
            // Wenn im "try"-Block Fehler auftreten, dann Socket schließen:
            if (socket != null)
                socket.close();
            System.err.println("Socket geschlossen");
            System.exit(0);
        }

        // Verbindung erfolgreich hergestellt: IP-Adresse und Port ausgeben
        System.err.println("Verbunden: " + socket.getInetAddress() + ":"
                + socket.getPort());

        // Begrüßungsmeldung vom Server lesen
        String message = sin.readLine();
        System.out.println(message);
    }

    @Override
    public Person login(String name, String passwort) throws LoginFailedException {
        sout.println("l");
        sout.println(name);
        sout.println(passwort);
        try {
            String antwort = sin.readLine();
            if (!antwort.equalsIgnoreCase("Erfolg")) {
                throw new LoginFailedException();
            }
            String un = sin.readLine();
            String pw = sin.readLine();
            String kTyp = sin.readLine();

            if (kTyp.equalsIgnoreCase("k")) {
                person = new Kunde(un, pw, sin.readLine());
            } else if (kTyp.equalsIgnoreCase("m")) {
                person = new Mitarbeiter(un, pw);
            } else  {
                throw new LoginFailedException();
            }
            return person;
        } catch (IOException e) {
            throw new LoginFailedException();
        }
    }

    @Override
    public Person getUserEingeloggt() {
        return person;
    }

    @Override
    public Mitarbeiter mRegister(Mitarbeiter mitarbeiter) throws BenutzerExistiertException{
        // Kennzeichen für gewählte Aktion senden
        sout.println("m");
        // Parameter für Aktion senden
        sout.println(mitarbeiter.getUsername());
        sout.println(mitarbeiter.getPasswort());

        try {
            String answer = sin.readLine();
            if (answer.equals("Fehler")) {
                throw new BenutzerExistiertException(mitarbeiter);
            }
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public Kunde kRegister(Kunde kunde) throws BenutzerExistiertException{
        // Kennzeichen für gewählte Aktion senden
        sout.println("r");
        // Parameter für Aktion senden
        sout.println(kunde.getUsername());
        sout.println(kunde.getPasswort());
        sout.println(kunde.getAdresse());

        try {
            String answer = sin.readLine();
            if (answer.equals("Fehler")) {
                throw new BenutzerExistiertException(kunde);
            }
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public void logout() {
        sout.println("x");
        try {
            sin.readLine();
            person = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Artikel> gibAlleArtikel() {
        // Kennzeichen für gewählte Aktion senden
        sout.println("a");
        return leseArtikelListe();
    }

    private List<Artikel> leseArtikelListe() {
        List<Artikel> liste = new ArrayList<Artikel>();

        // Antwort vom Server lesen und im info-Feld darstellen:
        String antwort;
        try {
            // Anzahl gefundener Artikel einlesen
            antwort = sin.readLine();
            int anzahl = Integer.parseInt(antwort);
            for (int i = 0; i < anzahl; i++) {
                // Artikel vom Server lesen ...
                Artikel artikel = liesArtikelVonServer();
                // ... und in Liste eintragen
                liste.add(artikel);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return liste;
    }

    private Artikel liesArtikelVonServer() throws IOException {
        String nr = sin.readLine();
        int nummer = Integer.parseInt(nr);

        String bezeichnung = sin.readLine();
        if (bezeichnung == null) {
            return null;
        }

        String bestandStr = sin.readLine();
        int bestand = Integer.parseInt(bestandStr);

        String preisStr = sin.readLine();
        float preis = Float.parseFloat(preisStr);

        String packung = sin.readLine();
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

    @Override
    public List<Ereignis> gibAlleEreignisse() {
        List<Ereignis> liste = new ArrayList<Ereignis>();

        // Kennzeichen für gewählte Aktion senden
        sout.println("k");

        // Antwort vom Server lesen und im info-Feld darstellen:
        String antwort;
        try {
            // Anzahl gefundener Ereignisse einlesen
            antwort = sin.readLine();
            int anzahl = Integer.parseInt(antwort);
            for (int i = 0; i < anzahl; i++) {
                // ereignis vom Server lesen ...
                Ereignis ereignis = liesEreignisVonServer();
                // ... und in Liste eintragen
                liste.add(ereignis);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return liste;
    }
    private Ereignis liesEreignisVonServer() throws IOException {
        String antwort;

        //daten einlesen
        String benutzerName = sin.readLine();
        String type = sin.readLine();
        antwort = sin.readLine();
        int artikelNr = Integer.parseInt(antwort);
        antwort = sin.readLine();
        int artikelAnzahl = Integer.parseInt(antwort);
        String datum = sin.readLine();
        //Neues Ereignis erzeugen
        Ereignis ereignis = new Ereignis(benutzerName,type, artikelNr,artikelAnzahl, datum);
        return ereignis;
    }

    @Override
    public void loescheArtikel(int nr)throws ArtikelExistiertNichtException, IOException{
        // Kennzeichen für gewählte Aktion senden
        sout.println("b");
        // Parameter für Aktion senden
        sout.println(nr);

        try {
            String answer = sin.readLine();
            if (answer.equals("Fehler")) {
                throw new ArtikelExistiertNichtException();
            }
        } catch (IOException e) {
            e.getMessage();
        }

    }

    @Override
    public void artikelEinfuegen(Artikel einArtikel) throws ArtikelExistiertException, PackunggroesseException{
        // Kennzeichen für gewählte Aktion senden
        sout.println("c");
        // Parameter für Aktion senden
        if(einArtikel instanceof MassengutArtikel) {
            sout.println("j");
        } else {
            sout.println("n");
        }
        sout.println(einArtikel.getBezeichnung());
        sout.println(einArtikel.getPreis());
        sout.println(einArtikel.getBestand());
        if(einArtikel instanceof MassengutArtikel) {
            sout.println(((MassengutArtikel) einArtikel).getPackungGroesse());
        }
        // Antwort vom Server lesen:
        try {
            String antwort;
            antwort = sin.readLine();
            if (antwort.equals("Fehler")) {
                throw new ArtikelExistiertException(einArtikel);
            } else if (antwort.equals("Fehler2")) {
                throw new PackunggroesseException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Artikel bestandErhoehen(int nr, int menge) throws ArtikelExistiertNichtException,PackunggroesseException {
        sout.println("d");
        sout.println(nr);
        sout.println(menge);

        // Antwort von server
        try {
            String antwort = sin.readLine();
            if (antwort.equals("Fehler")) {
                throw new ArtikelExistiertNichtException();
            } else if (antwort.equals("Fehler2")) {
                throw new PackunggroesseException();
            }
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }


    @Override
    public Artikel getArtikel(int nr) throws ArtikelExistiertNichtException {
        return null;
    }

    @Override
    public List<Artikel> sucheNachBezeichnung(String bezeichnung) {
        List<Artikel> liste = new ArrayList<Artikel>();
        // Kennzeichen für gewählte Aktion senden
        sout.println("e");
        // Parameter für Aktion senden
        sout.println(bezeichnung);

        // Antwort vom Server lesen und im info-Feld darstellen:
        String antwort = "?";
        try {
            // Anzahl gefundener Artikel einlesen
            antwort = sin.readLine();
            int anzahl = Integer.parseInt(antwort);
            for (int i = 0; i < anzahl; i++) {
                // Artikel vom Server lesen ...
                Artikel artikel = liesArtikelVonServer();
                // ... und in Liste eintragen
                liste.add(artikel);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return liste;
    }

    @Override
    public void inWk(int nummer, int menge) throws ArtikelExistiertNichtException, BestandZuWenigException, PackunggroesseException {
        sout.println("f");
        sout.println(nummer);
        sout.println(menge);
        // Antwort vom Server lesen:
        String antwort = "Fehler";
        try {
            antwort = sin.readLine();
            if (antwort.equals("Fehler")) {
                throw new ArtikelExistiertNichtException();
            } else if (antwort.equals("Fehler2")) {
                throw new BestandZuWenigException();
            }  else if (antwort.equals("Fehler3")) {
                throw new PackunggroesseException();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<Artikel> zeigtWk() {
        List<Artikel> liste = new ArrayList<Artikel>();

        // Kennzeichen für gewählte Aktion senden
        sout.println("h");

        // Antwort vom Server lesen und im info-Feld darstellen:
        String antwort = "?";
        try {
            // Anzahl gefundener Artikel einlesen
            antwort = sin.readLine();
            int anzahl = Integer.parseInt(antwort);
            for (int i = 0; i < anzahl; i++) {
                // Artikel vom Server lesen ...
                Artikel artikel = liesArtikelVonServer();
                // ... und in Liste eintragen
                liste.add(artikel);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return liste;
    }

    @Override
    public void ausWk(int nummer) throws ArtikelIstNichtImWkException {
        sout.println("g");
        sout.println(nummer);

        // Antwort vom Server lesen:
        String antwort = "Fehler";
        try {
            antwort = sin.readLine();
           if (antwort.equals("Fehler")) {
                throw new ArtikelIstNichtImWkException();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void warenkorbLoeschen() throws WarenkorbIstLeerException {
        // Kennzeichen für gewählte Aktion senden
        sout.println("j");
        String antwort;
        try {
            antwort = sin.readLine();
            if (antwort.equals("Fehler")) {
                throw new WarenkorbIstLeerException();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Rechnung warenkorbKaufen() throws WarenkorbIstLeerException, ArtikelExistiertNichtException, BestandZuWenigException, IOException {
        sout.println("p");

        try {
            String antwort = sin.readLine();
            if (antwort.equals("Fehler")){
                throw new ArtikelExistiertNichtException();
            } else if(antwort.equals("Fehler2")){
                throw new BestandZuWenigException();
            } else if(antwort.equals("Fehler3")){
                throw new WarenkorbIstLeerException();
            }

            Date date = new Date(Long.parseLong(sin.readLine()));
            int anzahlDerArtikel = Integer.parseInt(sin.readLine());
            HashMap<Integer, Integer> gekaufteArtikelMap = new HashMap<>();

            for (int i = 0; i < anzahlDerArtikel; i++) {
                int artikelNummer = Integer.parseInt(sin.readLine());
                int menge = Integer.parseInt(sin.readLine());
                gekaufteArtikelMap.put(artikelNummer, menge);
            }

            double gesamtPreis = Double.parseDouble(sin.readLine());
            return new Rechnung((Kunde) person, gekaufteArtikelMap, gesamtPreis,date );

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void disconnect() throws IOException {
        // Kennzeichen für gewählte Aktion senden
        sout.println("q");
        // (Parameter sind hier nicht zu senden)

        // Antwort vom Server lesen:
        String antwort = "Fehler";
        try {
            antwort = sin.readLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println(antwort);
    }
}
