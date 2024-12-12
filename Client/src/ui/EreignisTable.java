package ui;

import shop.common.Entities.Ereignis;
import shop.common.Interface.ShopInterface;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class EreignisTable extends ShopFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public EreignisTable(ShopInterface shop) {
        super(shop);
        setup();
    }

    private void setup() {
    setTitle("Ereignis Table");
    setSize(640, 480);

    tableModel = new DefaultTableModel(new Object[]{"Typ", "Username", "ArtikelNr", "Artikelanzahl", "Datum"}, 0);
    table = new JTable(tableModel);

    JScrollPane tableScrollPane = new JScrollPane(table);

    List<Ereignis> ereignislist = shop.gibAlleEreignisse();
    updateEreignisList(ereignislist);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(tableScrollPane, BorderLayout.CENTER);

    add(panel);
    setVisible(true);
}
    public void updateEreignisList(List<Ereignis> ereignis) {
        // Liste Sortieren
        Collections.sort(ereignis, Comparator.comparing(Ereignis::getEreignisDatum));
        // clear
        tableModel.setRowCount(0);
        // table mit updated data
        for (Ereignis e : ereignis) {
            Object[] rowData = {e.getTyp(), e.getUsername(), e.getArtikelNr(), e.getArtikelanzahl(), e.getDatum()};
            tableModel.addRow(rowData);
        }
    }
}

