package library;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {

    //region Used UI Elements
    public JPanel panelLogin;
    private JTextField textLastName;
    private JTextField textFirstName;
    private JButton btnLogin;
    private JButton btnRegister;
    //endregion

    //region Unused UI Elements
    private JLabel lblFirstName;
    private JLabel lblLastName;
    private JLabel lblLogin;
    //endregion

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
                main.registerMember(textFirstName.getText(), textLastName.getText());
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
