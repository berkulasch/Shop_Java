package shop.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shop.common.Entities.*;
import shop.common.Exceptions.*;
import shop.common.Interface.ShopInterface;

public class ClientRequestProcessor implements Runnable {

    private ShopInterface shop;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintStream out;
    private Person benutzer;

    public ClientRequestProcessor(Socket socket, ShopInterface shop) {
        this.shop = shop;
        clientSocket = socket;

        // I/O-Streams initialisieren und ClientRequestProcessor-Objekt als Thread starten:
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException e2) {
            }
            System.err.println("Ausnahme bei Bereitstellung des Streams: " + e);
            return;
        }

        System.out.println("Verbunden mit " + clientSocket.getInetAddress()
                + ":" + clientSocket.getPort());
    }

    public void run() {
        String input = "";


        // Begrüßungsnachricht an den Client senden
        out.println("Server an Client: Bin bereit für Deine Anfragen!");

        // Hauptschleife zur wiederholten Abwicklung der Kommunikation
        do {
            // Beginn der Benutzerinteraktion:
            // Aktion vom Client einlesen [dann ggf. weitere Daten einlesen ...]
            try {
                input = in.readLine();
            } catch (Exception e) {
                System.out.println("--->Fehler beim Lesen vom Client (Aktion): ");
                System.out.println(e.getMessage());
                break;
            }
            if(input!= null) {
                if (benutzer != null) {
                    try {
                        shop.login(benutzer.getUsername(), benutzer.getPasswort());
                    } catch (LoginFailedException ignore) {

                    }
                }
            }
            // Eingabe bearbeiten:
            if (input == null) {
                // input wird von readLine() auf null gesetzt, wenn Client Verbindung abbricht
                // Einfach behandeln wie ein "quit"
                input = "q";
            } else if (input.equals("l")) {
                // Aktion "einloggen _l" gewählt
                einloggen();
            } else if (input.equals("r")) {
                // Aktion "registerKunde _r" gewählt
                registerKunde();
            } else if (input.equals("m")) {
                // Aktion "registerMitarbeiter _m" gewählt
                registerMitarbeiter();
            } else if (input.equals("a")) {
                // Aktion "artikelausgeben _a" gewählt
                ausgeben();
            } else if (input.equals("b")) {
                // Aktion "artikelLoeschen b" gewählt
                try {
                    artikelLoeschen();
                } catch (ArtikelExistiertNichtException | IOException ignored) {
                }
            } else if (input.equals("c")) {
                // Aktion "artikelEinfuegen c" gewählt
                artikelEinfuegen();
            } else if (input.equals("d")) {
                // Aktion "bestandErhoehen d" gewählt
                try {
                    bestandErhoehen();
                } catch (PackunggroesseException | ArtikelExistiertNichtException | IOException ignored) {
                }
            } else if (input.equals("e")) {
                // Aktion "suchen e" gewählt
                suchen();
            } else if (input.equals("f")) {
                // Aktion "artikelinWk f" gewählt
                try {
                    artikelInWK();
                } catch (PackunggroesseException | ArtikelExistiertNichtException | BestandZuWenigException ignored) {
                }
            } else if (input.equals("g")) {
                // Aktion "artikelausWk g" gewählt
                try {
                    artikelAusWk();
                } catch (ArtikelIstNichtImWkException ignored) {

                }
            } else if (input.equals("h")) {
                // Aktion "zeigtWk h" gewählt
                zeigtWk();
            } else if (input.equals("j")) {
                // Aktion "wkLoeschen j" gewählt
                try {
                    wkLoeschen();
                } catch (WarenkorbIstLeerException ignored) {
                }
            } else if (input.equals("k")) {
                // Aktion "zeigtEreignis k" gewählt
                zeigtEreignis();
            } else if (input.equals("p")) {
                // Aktion "kaufen p" gewählt
                kaufen();
            } else if (input.equals("x")) {
                // Aktion "logout x" gewählt
                logout();
                this.benutzer = null;
            }


        } while (!(input.equals("q")));
        // Verbindung wurde vom Client abgebrochen:
        disconnect();
    }

    private void einloggen() {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        // hier ist nur der Benutzername
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (BenutzerName): ");
            System.out.println(e.getMessage());
        }
        String username = new String(input);

        // hier ist nur der Passwort
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Passwort): ");
            System.out.println(e.getMessage());
        }
        String passwort = new String(input);

        try {
            this.benutzer = shop.login(username, passwort);
            out.println("Erfolg");
            out.println(shop.getUserEingeloggt().getUsername());
            out.println(shop.getUserEingeloggt().getPasswort());
            if (shop.getUserEingeloggt() instanceof Kunde) {
                Kunde k = (Kunde) shop.getUserEingeloggt();
                out.println("k"); //Kunde
                out.println(k.getAdresse());
            } else {
                out.println("m"); //Mitarbeiter
            }
            System.out.println("Benutzer " + username + " eingeloggt");
        } catch (LoginFailedException e) {
            System.out.println("--->Fehler beim Einloggen: ");
            System.out.println(e.getMessage());
            out.println("Fehler");
        }
    }

    private void registerKunde() {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        // hier ist nur der Benutzername
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (BenutzerName): ");
            System.out.println(e.getMessage());
        }
        String username = new String(input);

        // hier ist nur der Passwort
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Passwort): ");
            System.out.println(e.getMessage());
        }
        String passwort = new String(input);

        // hier ist nur der Adresse
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Adresse): ");
            System.out.println(e.getMessage());
        }
        String adresse = new String(input);

        try {
            Kunde kunde = new Kunde(username, passwort, adresse);
            shop.kRegister(kunde);
            out.println("Erfolg");
        } catch (BenutzerExistiertException | IOException ignored) {
            out.println("Fehler");
        }
    }

    private void registerMitarbeiter() {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        // hier ist nur der Benutzername
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (BenutzerName): ");
            System.out.println(e.getMessage());
        }
        String username = new String(input);

        // hier ist nur der Passwort
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Passwort): ");
            System.out.println(e.getMessage());
        }
        String passwort = new String(input);
        try {
            Mitarbeiter mitarbeiter = new Mitarbeiter(username, passwort);
            shop.mRegister(mitarbeiter);
            out.println("Erfolg");
        } catch (BenutzerExistiertException | IOException ignored) {
            out.println("Fehler");
        }
    }

    private void logout() {
        shop.logout();
        out.println("Erfolg");
    }

    private void ausgeben() {
        // Die Arbeit soll wieder das Artikelverwaltungsobjekt machen:
        List<Artikel> artikel = null;
        artikel = shop.gibAlleArtikel();
        sendeArtikelListAnClient(artikel);
    }

    private void sendeArtikelListAnClient(List<Artikel> artikel) {
        // Anzahl der gefundenen Artikel senden
        out.println(artikel.size());
        for (Artikel a : artikel) {
            sendeArtikelAnClient(a);
        }
    }

    private void sendeArtikelAnClient(Artikel a) {
        // Nummer des Artikels senden
        out.println(a.getArtikelNummer());
        // Titel des Artikels senden
        out.println(a.getBezeichnung());
        // Bestand des Artikels senden
        out.println(a.getBestand());
        // Preis des Artikels senden
        out.println(a.getPreis());
        if (a instanceof MassengutArtikel) {
            out.println(((MassengutArtikel) a).getPackungGroesse());
        } else {
            out.println(-1);
        }
    }

    private void zeigtWk() {
        // Die Arbeit soll wieder das Warenkorbverwaltungsobjekt machen:
        List<Artikel> artikel = null;
        artikel = shop.zeigtWk();

        sendeArtikelListAnClient(artikel);
    }

    private void zeigtEreignis() {
        List<Ereignis> ereignis = null;
        ereignis = shop.gibAlleEreignisse();

        sendealleEreignisAnClient(ereignis);
    }

    private void sendealleEreignisAnClient(List<Ereignis> ereignis) {
        // Anzahl der gefundenen Ereignis senden
        out.println(ereignis.size());
        for (Ereignis e : ereignis) {
            sendEreignisAnClient(e);
        }
    }

    private void sendEreignisAnClient(Ereignis ereignis) {
        // Username senden
        out.println(ereignis.getUsername());
        // Typ des Ereignis senden
        out.println(ereignis.getTyp());
        // Nummer des Ereignis senden
        out.println(ereignis.getArtikelNr());
        //Bestand-Änderung senden
        out.println(ereignis.getArtikelanzahl());
        // Datum senden
        out.println(ereignis.getDatum());
    }

    private void wkLoeschen() throws WarenkorbIstLeerException {
        try {
            shop.warenkorbLoeschen();
            out.println("Erfolg");
        } catch (WarenkorbIstLeerException e) {
            out.println("Fehler");
        }
    }

    private void artikelLoeschen() throws ArtikelExistiertNichtException, IOException {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out
                    .println("--->Fehler beim Lesen vom Client (Nr): ");
            System.out.println(e.getMessage());
        }
        int artikelNummer = Integer.parseInt(input);

        try {
            shop.loescheArtikel(artikelNummer);
            out.println("Erfolg");
        } catch (ArtikelExistiertNichtException e) {
            out.println("Fehler");
        }
    }

    private void kaufen() {
        try {
            Rechnung rechnung = shop.warenkorbKaufen();
            sendeRechnung(rechnung);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ArtikelExistiertNichtException e) {
            out.println("Fehler");
        } catch (BestandZuWenigException e) {
            out.println("Fehler2");
        } catch (WarenkorbIstLeerException e) {
            out.println("Fehler3");
        }

    }

    public void sendeRechnung(Rechnung rechnung) {
        out.println(rechnung.getKundenname());
        out.println(rechnung.getDatum().getTime());
        HashMap<Integer, Integer> gekaufteArtikelMap = rechnung.getArtikelMap();
        int size = gekaufteArtikelMap.size();
        out.println(size);

        for (Map.Entry<Integer, Integer> entry : gekaufteArtikelMap.entrySet()) {
            int artikelNummer = entry.getKey();
            int menge = entry.getValue();
            out.println(artikelNummer);
            out.println(menge);
        }
        out.println(rechnung.getGesamtPreis());

    }

    private void artikelEinfuegen() {
        try {
            // Artikel einfuegen
            String artikelArt = in.readLine();
            String bezeichnung = in.readLine();
            float preis = Float.parseFloat(in.readLine());
            int bestand = Integer.parseInt(in.readLine());
            Artikel artikel = null;
            if (artikelArt.equals("j")) {
                String packungsgroesseString = in.readLine();
                int packungsgroesse = Integer.parseInt(packungsgroesseString);
                artikel = new MassengutArtikel(bezeichnung, preis, bestand, packungsgroesse);
            } else if (artikelArt.equals("n")) {
                artikel = new Artikel(bezeichnung, preis, bestand);
            }
            try {
                shop.artikelEinfuegen(artikel);
                out.println("Erfolg");
            } catch (ArtikelExistiertException e) {
                out.println("Fehler");
            } catch (PackunggroesseException e) {
                out.println("Fehler2");
            }
        } catch (Exception e) {
            out.println("Fehler");
        }

    }

    private void bestandErhoehen() throws PackunggroesseException, ArtikelExistiertNichtException, IOException {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Nr): ");
            System.out.println(e.getMessage());
        }
        int artikelNummer = Integer.parseInt(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Menge): ");
            System.out.println(e.getMessage());
        }
        int menge = Integer.parseInt(input);

        try {
            shop.bestandErhoehen(artikelNummer, menge);
            out.println("Erfolg");
        } catch (ArtikelExistiertNichtException e) {
            out.println("Fehler");
        } catch (PackunggroesseException e3) {
            out.println("Fehler2");
        }

    }

    private void suchen() {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        // hier ist nur der Bezeichnung des gesuchten Artikels erforderlich:
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Bezeichnung): ");
            System.out.println(e.getMessage());
        }
        // Achtung: Objekte sind Referenzdatentypen:
        // Artikelbezeichnung in neues String-Objekt kopieren,
        // damit bezeichnung nicht bei nächster Eingabe in input überschrieben wird
        String bezeichnung = new String(input);

        // die eigentliche Arbeit soll das Artikelverwaltungsobjekt machen:
        List<Artikel> artikel = null;
        if (bezeichnung.equals(""))
            artikel = shop.gibAlleArtikel();
        else
            artikel = shop.sucheNachBezeichnung(bezeichnung);

        sendeArtikelListAnClient(artikel);
    }

    private void artikelInWK() throws PackunggroesseException, ArtikelExistiertNichtException, BestandZuWenigException {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Nr): ");
            System.out.println(e.getMessage());
        }
        int artikelNummer = Integer.parseInt(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Nr): ");
            System.out.println(e.getMessage());
        }
        int menge = Integer.parseInt(input);

        try {
            shop.inWk(artikelNummer, menge);
            out.println("Erfolg");
        } catch (ArtikelExistiertNichtException e) {
            out.println("Fehler");
        } catch (BestandZuWenigException e) {
            out.println("Fehler2");
        } catch (PackunggroesseException e3) {
            out.println("Fehler3");
        }
    }

    private void artikelAusWk() throws ArtikelIstNichtImWkException {
        String input = null;
        // lese die notwendigen Parameter, einzeln pro Zeile
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("--->Fehler beim Lesen vom Client (Nr): ");
            System.out.println(e.getMessage());
        }
        int nummer = Integer.parseInt(input);
        try {
            shop.ausWk(nummer);
            out.println("Erfolg");
        } catch (ArtikelIstNichtImWkException e) {
            out.println("Fehler");
        }
    }

    private void disconnect() {
        try {
            out.println("Tschuess!");
            clientSocket.close();

            System.out.println("Verbindung zu " + clientSocket.getInetAddress()
                    + ":" + clientSocket.getPort() + " durch Client abgebrochen");
        } catch (Exception e) {
            System.out.println("--->Fehler beim Beenden der Verbindung: ");
            System.out.println(e.getMessage());
            out.println("Fehler");
        }
    }

}
