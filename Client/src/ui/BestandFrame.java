package ui;
import shop.common.Entities.Artikel;
import shop.common.Exceptions.ArtikelExistiertNichtException;
import shop.common.Exceptions.PackunggroesseException;
import shop.common.Interface.ShopInterface;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BestandFrame extends ShopFrame {
    private JTextField nummerTextFeld;
    private JButton bestandButton;
    private JTextField bestandTextFeld;
    private MitarbeiterFrame mitarbeiterFrame;

    public BestandFrame(ShopInterface shop, MitarbeiterFrame mitarbeiterFrame){
        super(shop);
        this.mitarbeiterFrame = mitarbeiterFrame;
        setup();
        setupComponents();
    }

    private void setup(){
        setTitle("Bestand Erhöhen");
        setSize(400, 180);
        setLayout(new GridLayout(2, 2));
        setResizable(false);
        setVisible(true);
    }
    private void setupComponents(){
        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        JLabel nummerLabel = new JLabel("Artikelnummer: ");
        JLabel bestandLabel = new JLabel("Bestand: ");

        nummerTextFeld = new JTextField();
        bestandTextFeld = new JTextField();

        inputPanel.add(nummerLabel);
        inputPanel.add(nummerTextFeld);
        inputPanel.add(bestandLabel);
        inputPanel.add(bestandTextFeld);

        JPanel buttonPanel = new JPanel();
        bestandButton = new JButton("Bestand Erhöhen");
        bestandButton.addActionListener(e -> erhoehen());

        buttonPanel.add(bestandButton);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void erhoehen() {
        String nummerS = nummerTextFeld.getText();
        String bestandS = bestandTextFeld.getText();
        if(!nummerS.isEmpty() && !bestandS.isEmpty()) {
            try {
                int nummer = Integer.parseInt(nummerTextFeld.getText());
                nummerTextFeld.setText("");
                int bestand = Integer.parseInt(bestandTextFeld.getText());
                bestandTextFeld.setText("");
                shop.bestandErhoehen(nummer, bestand);
                java.util.List<Artikel> artikellist = shop.gibAlleArtikel();
                mitarbeiterFrame.aktualisiereArtikelListe(artikellist);
            } catch (ArtikelExistiertNichtException | NumberFormatException | IOException | PackunggroesseException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Bitte Artikelnummer und Bestand eingeben!");
        }
    }

}
