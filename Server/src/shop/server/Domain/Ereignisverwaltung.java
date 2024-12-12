package shop.server.Domain;

import shop.common.Entities.Artikel;
import shop.common.Entities.Ereignis;
import shop.common.Entities.Person;
import shop.common.Exceptions.ArtikelExistiertException;
import shop.server.Persistence.FilePersistenceManager;
import shop.server.Persistence.PersistenceManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Ereignisverwaltung {
    private static final List<Ereignis> ereignisListe = new Vector<>();
    private PersistenceManager pm = new FilePersistenceManager();

    public List<Ereignis> getEreignisListe() {
        return (ereignisListe);
    }

    /**
     * lese Ereignis datei ein
     * @param datei
     * @throws IOException
     */
    public void liesEreignis(String datei) throws IOException {
        pm.openForReading(datei);
        Ereignis einEreignis;
        do {
            // Ereignis-Objekt einlesen
            einEreignis = pm.ladeEreignis();
            if (einEreignis != null) {
                try {
                    ereignisEinfuegen(einEreignis);
                } catch (ArtikelExistiertException e) {

                    e.printStackTrace();
                }
            }
        } while (einEreignis != null);

        pm.close();
    }

    /**
     * Ereignis in der Liste einfuegen
     * @param ereignis
     * @throws ArtikelExistiertException
     */
    public void ereignisEinfuegen(Ereignis ereignis) throws ArtikelExistiertException {
        ereignisListe.add(ereignis);
    }

    /**
     * Ereignis schreiben
     * @param datei
     * @throws IOException
     */

    // Schreibe Ereignis datei
    public void schreibeEreignis(String datei) throws IOException {
        pm.openForWriting(datei);

        for (Ereignis e : ereignisListe) {
            pm.speicherEreignis(e);
        }

        pm.close();
    }

    /**
     * Erstelle Ereigniss und in der Ereignissliste hinzufuegen
     * @param p
     * @param a
     * @param anzahl
     * @param typ
     */
    public void erstelleEreignis(Person p, Artikel a, int anzahl, String typ ) {
        String benutzerName = p.getUsername();
        int artikelNr = a.getArtikelNummer();
        Ereignis ereignis = new Ereignis(benutzerName,typ,artikelNr,anzahl);
        ereignisListe.add(ereignis);
    }

    public List<Ereignis> ereigniseFuer(Artikel artikel, int tage) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
        Date jetzt = new Date();
        String datumOhneStunden = dateFormat.format(jetzt);
        try {
            jetzt = dateFormat.parse(datumOhneStunden);
        } catch (ParseException ignored) {

        }
        Date bis = new Date(jetzt.getTime() - TimeUnit.DAYS.toMillis(tage));
        List<Ereignis> ergebnis = new ArrayList<>();
        for (Ereignis ereignis : ereignisListe) {
            if(ereignis.getArtikelNr() == artikel.getArtikelNummer() && ereignis.getEreignisDatum().getTime() >= bis.getTime()) {
                ergebnis.add(ereignis);
            }
        }
        return ergebnis;
    }

}
