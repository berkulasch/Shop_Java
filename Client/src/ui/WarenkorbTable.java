package ui;

import shop.common.Entities.Artikel;
import shop.common.Interface.ShopInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WarenkorbTable extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private ShopInterface shop;

    public WarenkorbTable(ShopInterface shop) {
        this.shop = shop;
        setup();
    }

    // Setup Screen
    private void setup() {
        setTitle("Warenkorb");
        setSize(320, 240);

        tableModel = new DefaultTableModel(new Object[]{"Bezeichnung", "Preis", "Artikelnummer"}, 0);
        table = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(table);

        List<Artikel> artikellist = shop.zeigtWk();
        gibArtikellisteAus(artikellist);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tableScrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    // gib Artikelliste aus
    private void gibArtikellisteAus(List<Artikel> liste) {
     tableModel.setRowCount(0);

     if (liste.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Warenkorb ist leer!", "Information", JOptionPane.INFORMATION_MESSAGE);
     } else {
        for (Artikel artikel : liste) {
            Object[] rowData = {artikel.getBezeichnung(), artikel.getPreis(), artikel.getArtikelNummer()};
            tableModel.addRow(rowData);
        }
      }
    }
}
