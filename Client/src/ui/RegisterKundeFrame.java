package ui;

import shop.common.Entities.Kunde;
import shop.common.Exceptions.AlleFelderAusfuellenException;
import shop.common.Exceptions.BenutzerExistiertException;
import shop.common.Interface.ShopInterface;
import shop.server.Domain.Shop;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegisterKundeFrame extends ShopFrame {

    private JTextField usernameTextField;
    private JTextField passwordField;
    private JTextField adresseTextField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterKundeFrame(ShopInterface shop) {
        super(shop);
        setup();
        setupComponents();
        this.shop = shop;
    }
    private void setup() {
        setTitle("Register");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        setVisible(true);
    }

    private void setupComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 4));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel adresseLabel = new JLabel("Adresse:");

        usernameTextField = new JTextField();
        passwordField = new JTextField();
        adresseTextField = new JTextField();

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameTextField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(adresseLabel);
        inputPanel.add(adresseTextField);

        JPanel buttonPanel = new JPanel();
        registerButton = new JButton("Register");
        registerButton.addActionListener(e ->{
            try {
                registration();
            } catch (AlleFelderAusfuellenException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        backButton = new JButton("Zurück");
        backButton.addActionListener(e ->{
            goBackToMain();
        });


        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void initializeKunde(){
        this.dispose();
        LoginFrame loginFrame = new LoginFrame(shop);
        loginFrame.setVisible(true);
    }
    private void registration() throws AlleFelderAusfuellenException{
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String adresse = adresseTextField.getText();

        if (username.isEmpty() || password.isEmpty()|| adresse.isEmpty()) {
            usernameTextField.setText("");
            passwordField.setText("");
            adresseTextField.setText("");
            throw new AlleFelderAusfuellenException();
        } else {
            try {
                Kunde kunde = new Kunde(username,password,adresse);
                shop.kRegister(kunde);
                initializeKunde();
            } catch (BenutzerExistiertException | IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }
}
