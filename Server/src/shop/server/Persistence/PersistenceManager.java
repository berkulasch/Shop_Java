package shop.server.Persistence;

import shop.common.Entities.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface PersistenceManager {

    public void openForReading(String datei) throws FileNotFoundException;
    public void openForWriting(String datei) throws IOException;
    public boolean close();

    public Kunde ladeKunde() throws IOException;
    public boolean speicherKunde(Kunde kunde);
    public Mitarbeiter ladeMitarbeiter() throws IOException;
    public boolean speicherMitarbeiter(Mitarbeiter mitarbeiter);

    public Artikel ladeArtikel() throws IOException;
    public boolean speicherArtikel(Artikel artikel);

    public boolean speicherEreignis(Ereignis ereignis);

    public Ereignis ladeEreignis() throws IOException;
}
