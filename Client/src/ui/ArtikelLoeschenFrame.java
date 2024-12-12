package ui;

import shop.common.Entities.Artikel;
import shop.common.Exceptions.ArtikelExistiertNichtException;
import shop.common.Interface.ShopInterface;
import shop.server.Domain.Shop;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
public class ArtikelLoeschenFrame extends ShopFrame {

    private JTextField nummerTextFeld;
    private JButton loeschenButton;
    private MitarbeiterFrame mitarbeiterFrame;

    public ArtikelLoeschenFrame(ShopInterface shop, MitarbeiterFrame mitarbeiterFrame){
        super(shop);
        this.mitarbeiterFrame = mitarbeiterFrame;
        setup();
        setupComponents();
    }

    private void setup(){
        setTitle("Artikel Löschen");
        setSize(400, 180);
        setLayout(new GridLayout(2, 2));
        setResizable(false);
        setVisible(true);
    }

    private void setupComponents(){
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel nummerLabel = new JLabel("Artikelnummer: ");

        nummerTextFeld = new JTextField();

        inputPanel.add(nummerLabel);
        inputPanel.add(nummerTextFeld);

        JPanel buttonPanel = new JPanel();
        loeschenButton = new JButton("Artikel Loeschen");
        loeschenButton.addActionListener(e -> loeschen());

        buttonPanel.add(loeschenButton);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void loeschen() {
        String nummerS = nummerTextFeld.getText();
        if (!nummerS.isEmpty()) {
            try {
                int nummer = Integer.parseInt(nummerS);
                nummerTextFeld.setText("");
                shop.loescheArtikel(nummer);
                java.util.List<Artikel> artikellist = shop.gibAlleArtikel();
                mitarbeiterFrame.aktualisiereArtikelListe(artikellist);
            } catch (ArtikelExistiertNichtException | NumberFormatException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Bitte die Artikelnummer eingeben!");
        }
    }

}
