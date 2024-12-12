package Persistence;

import Entities.Kunde;
import Entities.Person;

import java.io.IOException;

public class FileBasedKundenPersistenceManager extends FilePersistenceManager implements PersistenceManager<Kunde> {
    private final String personFileName;

    public FileBasedKundenPersistenceManager(String personFileName) {

        this.personFileName = personFileName;
    }

    @Override
    public Kunde load() throws IOException {
        openForReading(personFileName);
        Kunde kunde = ladeKunde();
        close();
        return kunde;
    }

    @Override
    public boolean save(Kunde kunde) {
        try {
            openForWriting(personFileName);
            boolean speicherKunde = speicherKunde(kunde);
            close();
            return speicherKunde;
        } catch (IOException ignored) {
            System.err.println(ignored.getMessage());
            //ignore
        }
        return false;
    }
}
