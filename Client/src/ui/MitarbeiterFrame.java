package ui;


import shop.common.Entities.Artikel;
import shop.common.Entities.MassengutArtikel;
import shop.common.Exceptions.AlleFelderAusfuellenException;
import shop.common.Exceptions.ArtikelExistiertException;
import shop.common.Exceptions.PackunggroesseException;
import shop.common.Interface.ShopInterface;
import shop.server.Domain.Shop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
public class MitarbeiterFrame extends ShopFrame {

    private JTextField suchTextfeld = null;
    private JTextField bestandTextFeld = null;
    private JTextField preisTextFeld = null;
    private JTextField bezeichnungTextFeld = null;
    private JTextField massengutTextFeld = null;
    private JButton suchButton = null;
    private JButton hinzufuegenButton = null;
    private JButton bestandErhoehenButton = null;
    private JButton ereignisButton = null;
    private JButton artikelLoeschenButton = null;
    private JButton addMitarbeiterButton = null;
    private JButton ausloggenButton = null;
    private JList<Artikel> artikelList = null;

    public MitarbeiterFrame(ShopInterface shop) {
        super(shop);
        initialize();
    }
    private void initialize() {
        // Window settings
        setTitle("Mitarbeiter Menu");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout des JFrames: BorderLayout
        setLayout(new BorderLayout());
        JPanel suchPanel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        suchPanel.setLayout(gridBagLayout);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;    // "Westliche" (linksbündige) Ausrichtung
        c.fill = GridBagConstraints.HORIZONTAL; // Horizontal füllen
        c.gridy = 0;    // Zeile 0

        JLabel suchLabel = new JLabel("Suchbegriff:");
        c.gridx = 0;    // Spalte 0
        c.weightx = 0.2;    // 20% der gesamten Breite
        gridBagLayout.setConstraints(suchLabel, c);
        suchPanel.add(suchLabel);

        suchTextfeld = new JTextField();
        c.gridx = 1;    // Spalte 1
        c.weightx = 0.6;    // 60% der gesamten Breite
        gridBagLayout.setConstraints(suchTextfeld, c);
        suchPanel.add(suchTextfeld);

        suchButton = new JButton("Such!");

        c.gridx = 2;    // Spalte 2
        c.weightx = 0.2;    // 20% der gesamten Breite
        gridBagLayout.setConstraints(suchButton, c);
        suchPanel.add(suchButton);

        suchButton.addActionListener(new SuchActionListener());

        artikelList = new JList<Artikel>(new Vector<>());

        List<Artikel> artikellist = shop.gibAlleArtikel();
        aktualisiereArtikelListe(artikellist);
        JScrollPane scrollPane = new JScrollPane(artikelList);

        // WEST: BoxLayout (vertikal)
        JPanel einfuegePanel = new JPanel();
        einfuegePanel.setLayout(new BoxLayout(einfuegePanel, BoxLayout.PAGE_AXIS));

        // Abstandhalter ("Filler") zwischen Rand und erstem Element
        Dimension borderMinSize = new Dimension(5, 10);
        Dimension borderPrefSize = new Dimension(5, 10);
        Dimension borderMaxSize = new Dimension(5, 10);
        einfuegePanel.add(new Box.Filler(borderMinSize, borderPrefSize, borderMaxSize));

        bestandTextFeld = new JTextField();
        bezeichnungTextFeld = new JTextField();
        preisTextFeld = new JTextField();
        massengutTextFeld = new JTextField();

        einfuegePanel.add(new JLabel("Bezeichnung:"));
        einfuegePanel.add(bezeichnungTextFeld);
        einfuegePanel.add(new JLabel("Preis:"));
        einfuegePanel.add(preisTextFeld);
        einfuegePanel.add(new JLabel("Bestand:"));
        einfuegePanel.add(bestandTextFeld);
        einfuegePanel.add(new JLabel("Packunggrösse:"));
        einfuegePanel.add(massengutTextFeld);

        // Abstandhalter ("Filler") zwischen letztem Eingabefeld und Add-Button
        Dimension fillerMinSize = new Dimension(5, 20);
        Dimension fillerPrefSize = new Dimension(5, Short.MAX_VALUE);
        Dimension fillerMaxSize = new Dimension(5, Short.MAX_VALUE);
        Box.Filler filler = new Box.Filler(fillerMinSize, fillerPrefSize, fillerMaxSize);
        einfuegePanel.add(filler);

        hinzufuegenButton = new JButton("Hinzufuegen");
        hinzufuegenButton.addActionListener(e -> {
            try {
                artikelEinfuegen();
            } catch (IOException | AlleFelderAusfuellenException ex) {
                throw new RuntimeException(ex);
            }
        });

        artikelLoeschenButton = new JButton("Artikel Loeschen");
        artikelLoeschenButton.addActionListener(e -> {
            new ArtikelLoeschenFrame(shop,this);
        });

        bestandErhoehenButton = new JButton("Bestand Erhöhen");
        bestandErhoehenButton.addActionListener(e -> {
            new BestandFrame(shop,this);
        });

        addMitarbeiterButton = new JButton("Add Mitarbeiter");
        addMitarbeiterButton.addActionListener(e -> {
            this.dispose();
            new RegisterMitarbeiterFrame(shop);
        });

        ereignisButton = new JButton("Ereignisse Anzeigen");
        ereignisButton.addActionListener(e -> {
            new EreignisTable(shop);
        });

        ausloggenButton = new JButton("Ausloggen");
        ausloggenButton.addActionListener(e ->{
            System.exit(0);
        });

        einfuegePanel.add(hinzufuegenButton);
        einfuegePanel.add(bestandErhoehenButton);
        einfuegePanel.add(artikelLoeschenButton);
        einfuegePanel.add(addMitarbeiterButton);
        einfuegePanel.add(ereignisButton);
        einfuegePanel.add(ausloggenButton);

        // Abstandhalter ("Filler") zwischen letztem Element und Rand
        einfuegePanel.add(new Box.Filler(borderMinSize, borderPrefSize, borderMaxSize));

        getContentPane().add(suchPanel, BorderLayout.NORTH);
        add(einfuegePanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Artikel in der Artikelliste einfuegen
    private void artikelEinfuegen() throws IOException, AlleFelderAusfuellenException {
    String bezeichnung = bezeichnungTextFeld.getText();
    String preisS = preisTextFeld.getText();
    String bestandS = bestandTextFeld.getText();
    String massenS = massengutTextFeld.getText();

    if (bezeichnung.isEmpty() || bestandS.isEmpty() || preisS.isEmpty()) {
        bezeichnungTextFeld.setText("");
        preisTextFeld.setText("");
        bestandTextFeld.setText("");
        massengutTextFeld.setText("");
        JOptionPane.showMessageDialog(null, "Alle Felder ausfüllen");
    } else {
        try {
            float preis = Float.parseFloat(preisS);
            int bestand = Integer.parseInt(bestandS);

            if (massenS.isEmpty()) {
                try {
                    Artikel artikel = new Artikel(bezeichnung, preis, bestand);
                    shop.artikelEinfuegen(artikel);
                    List<Artikel> artikellist = shop.gibAlleArtikel();
                    aktualisiereArtikelListe(artikellist);
                    bezeichnungTextFeld.setText("");
                    preisTextFeld.setText("");
                    bestandTextFeld.setText("");
                } catch (PackunggroesseException | ArtikelExistiertException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            } else {
                int packung = Integer.parseInt(massenS);
                try {
                        MassengutArtikel massengutArtikel = new MassengutArtikel(bezeichnung, preis, bestand, packung);
                        shop.artikelEinfuegen(massengutArtikel);
                        List<Artikel> artikellist = shop.gibAlleArtikel();
                        aktualisiereArtikelListe(artikellist);
                        bezeichnungTextFeld.setText("");
                        preisTextFeld.setText("");
                        bestandTextFeld.setText("");
                        massengutTextFeld.setText("");
                }catch (PackunggroesseException | ArtikelExistiertException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ungültige Eingabe! Bitte geben Sie numerische Werte ein.");
            }
        }
    }



    // Artikelliste aktualisieren
    protected void aktualisiereArtikelListe(List<Artikel> liste) {
        // Sortierung nach Nummer per Lambda Expression:
         Collections.sort(liste, (artikel1, artikel2) -> artikel1.getArtikelNummer() - artikel2.getArtikelNummer());
         artikelList.setListData(new Vector<Artikel>(liste));
    }


    // Artikel suchen nach Bezeichnung
    public class SuchActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(suchButton)) {
                String suchBegriff = suchTextfeld.getText();
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


