package ui;

import net.ShopFassade;

import shop.common.Interface.ShopInterface;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;

public class GUI extends JFrame {

    public static final int DEFAULT_PORT = 6788;

    private ShopInterface shop;
  //  private Shop shop;
    private JButton loginButton = null;
    private JButton registerMitarbeiterButton = null;
    private JButton registerKundeButton = null;


    public GUI(String title, String host, int port) throws HeadlessException, IOException {
        super(title);
        shop = new ShopFassade(host,port);
        initialize();
    }
    public GUI(String title, ShopInterface shop)  {
        super(title);
        this.shop = shop;
        initialize();
    }

    public static void main(String[] args) {
        int portArg = 0;
        String hostArg = null;
        InetAddress ia = null;

        // ---
        // Hier werden die main-Parameter geprüft:
        // ---

        // Host- und Port-Argument einlesen, wenn angegeben
        if (args.length > 2) {
            System.out.println("Aufruf: java <Klassenname> [<hostname> [<port>]]");
            System.exit(0);
        }
        switch (args.length) {
            case 0:
                try {
                    ia = InetAddress.getLocalHost();
                } catch (Exception e) {
                    System.out.println("XXX InetAdress-Fehler: " + e);
                    System.exit(0);
                }
                hostArg = ia.getHostName(); // host ist lokale Maschine
                portArg = DEFAULT_PORT;
                break;
            case 1:
                portArg = DEFAULT_PORT;
                hostArg = args[0];
                break;
            case 2:
                hostArg = args[0];
                try {
                    portArg = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Aufruf: java BibClientGUI [<hostname> [<port>]]");
                    System.exit(0);
                }
        }

        // Swing-UI auf dem GUI-Thread initialisieren
        // (host und port müssen für Verwendung in inner class final sein)
        final String host = hostArg;
        final int port = portArg;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    GUI gui = new GUI("Shop", host, port);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize(){
        setTitle("Main Menu");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        this.setVisible(true);

        JPanel loginPanel = new JPanel(new GridLayout(3, 3));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame(shop);
       });
        loginPanel.add(loginButton);

        JPanel registerMitarbeiterPanel = new JPanel();
        registerMitarbeiterPanel.setLayout(new BoxLayout(registerMitarbeiterPanel, BoxLayout.PAGE_AXIS));

        registerMitarbeiterButton = new JButton("Register als Mitarbeiter");
        registerMitarbeiterButton.addActionListener(e ->{
            this.dispose();
            new RegisterMitarbeiterFrame(shop);});
        registerMitarbeiterPanel.add(registerMitarbeiterButton);

        JPanel registerKundePanel = new JPanel();
        registerKundePanel.setLayout(new BoxLayout(registerKundePanel,BoxLayout.PAGE_AXIS));

        registerKundeButton = new JButton("Register als Kunde");
        registerKundeButton.addActionListener(e ->{
            this.dispose();
           new RegisterKundeFrame(shop);});
        registerKundePanel.add(registerKundeButton);

        add(loginButton);
        add(registerMitarbeiterButton);
        add(registerKundeButton);
    }

}







