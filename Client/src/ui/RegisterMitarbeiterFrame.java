package ui;

import shop.common.Entities.Mitarbeiter;
import shop.common.Exceptions.AlleFelderAusfuellenException;
import shop.common.Exceptions.BenutzerExistiertException;
import shop.common.Interface.ShopInterface;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegisterMitarbeiterFrame extends ShopFrame {

    private JTextField usernameTextField;
    private JTextField passwordField;
    private JButton backButton;
    private JButton registerButton;
    public RegisterMitarbeiterFrame(ShopInterface shop) {
        super(shop);
        setup();
        setupComponents();
    }
    private void setup() {
        setTitle("Register");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        setVisible(true);
    }
    private void setupComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 3));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameTextField = new JTextField();
        passwordField = new JTextField();

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameTextField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

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

    private void initializeMit(){
        this.dispose();
        LoginFrame loginFrame = new LoginFrame(shop);
        loginFrame.setVisible(true);
    }
    private void registration() throws AlleFelderAusfuellenException{
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            usernameTextField.setText("");
            passwordField.setText("");
            throw new AlleFelderAusfuellenException();
        } else {
            try {
                Mitarbeiter mitarbeiter = new Mitarbeiter(username,password);
                shop.mRegister(mitarbeiter);
                initializeMit();
            } catch (BenutzerExistiertException | IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }

    }

}
