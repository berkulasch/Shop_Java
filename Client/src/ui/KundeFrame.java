package ui;

import shop.common.Entities.Artikel;
import shop.common.Entities.Rechnung;
import shop.common.Exceptions.*;
import shop.common.Interface.ShopInterface;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class KundeFrame extends ShopFrame {

    private JButton suchenButton = null;
    private JButton warenkorbButton = null;
    private JButton inWarenkorbButton = null;
    private JButton ausWarenkorbButton = null;
    private JButton clearWarenkorbButton = null;
    private JButton kaufenButton = null;
    private JButton ausloggenButton = null;
    private JTextField suchenTextfeld = null;
    private JTextField mengeTextFeld = null;
    private JTextField nummerTextFeld = null;
    private JList artikelList = null;
    public KundeFrame(ShopInterface shop) {
        super(shop);
        initialize();
    }
    private void initialize() {
        // Window settings
        setTitle("Kunde Menu");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        JPanel sPanel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        sPanel.setLayout(gridBagLayout);
        GridBagConstraints b = new GridBagConstraints();
        b.anchor = GridBagConstraints.WEST;	// "Westliche" (linksbündige) Ausrichtung
        b.fill = GridBagConstraints.HORIZONTAL; // Horizontal füllen
        b.gridy = 0;

        JLabel sLabel = new JLabel("Suchbegriff:");
        b.gridx = 0;	// Spalte 0
        b.weightx = 0.2;	// 20% der gesamten Breite
        gridBagLayout.setConstraints(sLabel, b);
        sPanel.add(sLabel);

        suchenTextfeld = new JTextField();
        b.gridx = 1;	// Spalte 1
        b.weightx = 0.6;	// 60% der gesamten Breite
        gridBagLayout.setConstraints(suchenTextfeld, b);
        sPanel.add(suchenTextfeld);

        suchenButton = new JButton("Such!");
        b.gridx = 2;	// Spalte 2
        b.weightx = 0.2;	// 20% der gesamten Breite
        gridBagLayout.setConstraints(suchenButton, b);
        sPanel.add(suchenButton);

        suchenButton.addActionListener(new SuchActionListener());

        artikelList = new JList(new Vector<>());

        List<Artikel> artikellist = shop.gibAlleArtikel();
        aktualisiereArtikelListe(artikellist);
        JScrollPane scrollPane = new JScrollPane(artikelList);

        // WEST: BoxLayout (vertikal)
        JPanel einfuegenPanel = new JPanel();
        einfuegenPanel.setLayout(new BoxLayout(einfuegenPanel, BoxLayout.PAGE_AXIS));

        // Abstandhalter ("Filler") zwischen Rand und erstem Element
        Dimension borderMinSize = new Dimension(5, 10);
        Dimension borderPrefSize = new Dimension(5, 10);
        Dimension borderMaxSize = new Dimension(5, 10);
        einfuegenPanel.add(new Box.Filler(borderMinSize, borderPrefSize, borderMaxSize));

        nummerTextFeld = new JTextField();
        mengeTextFeld = new JTextField();
        einfuegenPanel.add(new JLabel("Nummer:"));
        einfuegenPanel.add(nummerTextFeld);
        einfuegenPanel.add(new JLabel("Menge:"));
        einfuegenPanel.add(mengeTextFeld);

        // Abstandhalter ("Filler") zwischen letztem Eingabefeld und Add-Button
        Dimension fillerMinSize = new Dimension(5, 20);
        Dimension fillerPrefSize = new Dimension(5, Short.MAX_VALUE);
        Dimension fillerMaxSize = new Dimension(5, Short.MAX_VALUE);
        Box.Filler filler = new Box.Filler(fillerMinSize, fillerPrefSize, fillerMaxSize);
        einfuegenPanel.add(filler);

        warenkorbButton = new JButton("Warenkorb anzeigen");
        warenkorbButton.addActionListener(e -> {
            new WarenkorbTable(shop);
        });

        inWarenkorbButton = new JButton("Artikel in Warenkorb");
        inWarenkorbButton.addActionListener(e -> inWK());

        ausWarenkorbButton = new JButton("Artikel aus Warenkorb");
        ausWarenkorbButton.addActionListener(e -> ausWK());

        clearWarenkorbButton = new JButton("Warenkorb löschen");
        clearWarenkorbButton.addActionListener(e -> wkLoeschen());

        kaufenButton = new JButton("Warenkorb Kaufen");
        kaufenButton.addActionListener(e -> kaufenWk());

        ausloggenButton = new JButton("Ausloggen");
        ausloggenButton.addActionListener(e ->{
            System.exit(0);
        });

        einfuegenPanel.add(warenkorbButton);
        einfuegenPanel.add(inWarenkorbButton);
        einfuegenPanel.add(ausWarenkorbButton);
        einfuegenPanel.add(clearWarenkorbButton);
        einfuegenPanel.add(kaufenButton);
        einfuegenPanel.add(ausloggenButton);

        // Abstandhalter ("Filler") zwischen letztem Element und Rand
        einfuegenPanel.add(new Box.Filler(borderMinSize, borderPrefSize, borderMaxSize));
        getContentPane().add(sPanel, BorderLayout.NORTH);
        add(einfuegenPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

    }
    private void inWK(){
        String nummerS = nummerTextFeld.getText();
        String mengeS = mengeTextFeld.getText();
        if(!nummerS.isEmpty() && !mengeS.isEmpty()) {
            try {
                int nummer = Integer.parseInt(nummerS);
                nummerTextFeld.setText("");
                int menge = Integer.parseInt(mengeS);
                mengeTextFeld.setText("");
                shop.inWk(nummer, menge);
            } catch (PackunggroesseException | BestandZuWenigException | ArtikelExistiertNichtException | NumberFormatException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null,"Bitte nummer und menge eingeben!");
        }
    }
    private void ausWK(){
        String nummerS = nummerTextFeld.getText();
        if(!nummerS.isEmpty()) {
            try {
                int nummer = Integer.parseInt(nummerTextFeld.getText());
                nummerTextFeld.setText("");
                shop.ausWk(nummer);
            } catch (ArtikelIstNichtImWkException|NumberFormatException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        } else{
            JOptionPane.showMessageDialog(null, "Bitte die Artikelnummer eingeben!");
        }
    }

    private void kaufenWk() {
        try {
            Rechnung rechnung = shop.warenkorbKaufen();
            List<Artikel> artikellist = shop.gibAlleArtikel();
            aktualisiereArtikelListe(artikellist);
            showRechnungDetails(rechnung);
        } catch (ArtikelExistiertNichtException | IOException | BestandZuWenigException | WarenkorbIstLeerException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void showRechnungDetails(Rechnung rechnung) {
        String message = "Preis: " + rechnung.getGesamtPreis() + "\n";
        message += "Kunde: " + rechnung.getKundenname() + "\n";
        message += "Gekauften Artikel: " + rechnung.getArtikelMap() + "\n";
        message += "Datum" + rechnung.getDatum() + "\n";
        JOptionPane.showMessageDialog(this, message, "Rechnung Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void wkLoeschen(){
        try {
            shop.warenkorbLoeschen();
            List<Artikel> artikellist = shop.gibAlleArtikel();
            aktualisiereArtikelListe(artikellist);
            JOptionPane.showMessageDialog(null, "Warenkorb ist leer");
        } catch (WarenkorbIstLeerException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    private void aktualisiereArtikelListe(List<Artikel> liste) {
        // Sortierung nach Nummer per Lambda Expression:
        Collections.sort(liste, (artikel1, artikel2) -> artikel1.getArtikelNummer()-artikel2.getArtikelNummer());
        artikelList.setListData(new Vector<Artikel>(liste));
    }

    public class SuchActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(suchenButton)) {
                String suchBegriff = suchenTextfeld.getText();
                List<Artikel> suchErgebnis;
                if (suchBegriff.isEmpty()) {
                    suchErgebnis = shop.gibAlleArtikel();
                } else {
                    suchErgebnis = shop.sucheNachBezeichnung(suchBegriff);
                }
                aktualisiereArtikelListe(suchErgebnis);
            }
        }
    }
}
