package ui;
import shop.common.Entities.Kunde;
import shop.common.Entities.Mitarbeiter;
import shop.common.Exceptions.AlleFelderAusfuellenException;
import shop.common.Exceptions.LoginFailedException;
import shop.common.Interface.ShopInterface;
import javax.swing.*;
import java.awt.*;


public class LoginFrame extends ShopFrame {
    private JTextField usernameTextField;
    private JTextField passwordField;
    private JButton loginButton;
    private JButton backButton;

    public LoginFrame(ShopInterface shop) {
        super(shop);
        setup();
        setupComponents();
    }
    private void setup() {
        setTitle("Login");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        setVisible(true);
    }
    private void setupComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 4));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameTextField = new JTextField();
        passwordField = new JTextField();
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameTextField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.addActionListener(e ->{
            try {
                login();
            } catch (AlleFelderAusfuellenException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        backButton = new JButton("Zurück");
        backButton.addActionListener(e -> {
             goBackToMain();

        });

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void initializeLogin(ShopInterface shop){
        this.dispose();
        if(shop.getUserEingeloggt() instanceof Mitarbeiter) {
            initializeMitarbeiter();
        }else if(shop.getUserEingeloggt() instanceof Kunde) {
            initializeKunde();
        }
    }
    private void initializeMitarbeiter() {
        MitarbeiterFrame mitarbeiterFrame = new MitarbeiterFrame(shop);
        mitarbeiterFrame.setVisible(true);
    }
    private void initializeKunde() {
        KundeFrame kundeFrame = new KundeFrame(shop);
        kundeFrame.setVisible(true);
    }
    private void login() throws AlleFelderAusfuellenException{
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            usernameTextField.setText("");
            passwordField.setText("");
            throw new AlleFelderAusfuellenException();
        } else {
            try {
                shop.login(username,password);
                initializeLogin(shop);
            } catch (LoginFailedException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }
}
