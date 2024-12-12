package Persistence;

import Entities.Kunde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryKundenPersistence implements PersistenceManager<Kunde, String> {

    @Override
    public Kunde load(String s) throws IOException {
        return null;
    }

    @Override
    public boolean save(Kunde kunde) {
        return false;
    }
}
