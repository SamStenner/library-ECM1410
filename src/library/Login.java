package library;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    public JPanel panelLogin;
    private JTextField textLastName;
    private JTextField textFirstName;
    private JLabel lblFirstName;
    private JLabel lblLastName;
    private JButton btnLogin;
    private JLabel lblLogin;
    private JButton btnRegister;

    public Login(Main main){

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.submitCredentials(textFirstName.getText(), textLastName.getText());
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.registerMember(textFirstName.getText(), textFirstName.getText());
            }
        });
        textLastName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.submitCredentials(textFirstName.getText(), textLastName.getText());
            }
        });
    }

}
