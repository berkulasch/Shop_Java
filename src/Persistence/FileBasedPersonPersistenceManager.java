package Persistence;

import Entities.Kunde;

import java.io.IOException;

public class FileBasedPersonPersistenceManager extends FilePersistenceManager implements PersistenceManager<Kunde> {
    private final String personFileName;

    public FileBasedPersonPersistenceManager(String personFileName) {

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
    public Kunde load(Object o) throws IOException {
        return null;
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
