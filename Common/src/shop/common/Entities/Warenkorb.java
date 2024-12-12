package shop.common.Entities;

import java.util.HashMap;
public class Warenkorb {

    private HashMap<Integer, Integer> warenkorbMap = new HashMap<>();
    public HashMap<Integer, Integer> getWarenkorbMap() { return warenkorbMap; }

    // Artikel aus Warenkorb entfernen
    public boolean artikelEntfernen(int artikelnummer) {
        // Überprüfen, ob die Nummer im List vorhanden ist
        if (warenkorbMap.containsKey(artikelnummer)) {
            warenkorbMap.remove(artikelnummer);
            return true;
        }
        return false;
    }
    // löscht Warenkorb
    public void clearWarenkorb() {
        warenkorbMap.clear();
    }

    // Artikel in Warenkorb hinzufuegen
    public void inWk(int nummer, int menge) {
        if (warenkorbMap.containsKey(nummer)) {
            int aktuelleMenge = warenkorbMap.get(nummer);
            warenkorbMap.put(nummer, aktuelleMenge + menge); // Die Menge durch Hinzufügen der angegebenen Menge aktualisieren
        } else {
            warenkorbMap.put(nummer, menge);
        }
    }
    // Get Artikelmenge in Warenkorb
    public int getMenge(int nummer) {
        return warenkorbMap.getOrDefault(nummer, 0);
    }


    public boolean isEmpty() {
        return warenkorbMap.isEmpty();
    }
}