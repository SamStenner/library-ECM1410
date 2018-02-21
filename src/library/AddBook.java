package library;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class AddBook {
    private JLabel lblHeader;
    private JLabel lblTitle;
    private JLabel lblAuthors;
    private JLabel lblPublished;
    private JLabel lblQuantity;
    private JTextField txtTitle;
    private JTextField txtAuthors;
    private JTextField txtPublished;
    private JTextField txtQuantity;
    private JButton btnSubmit;
    public JPanel panelAddBook;

    public AddBook(Library lib, JFrame thisFrame, Main main) {

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] authors = txtAuthors.getText().split(",");
                    for (int i = 0; i < authors.length; i++) {
                        authors[i] = authors[i].trim();
                    }
                    int published = Integer.parseInt(txtPublished.getText());
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    try {
                        lib.addNewBook(txtTitle.getText(), authors , published, quantity);
                        main.configBookTable();
                        thisFrame.dispose();
                    } catch (RuntimeException re) {
                        JOptionPane.showMessageDialog(null, re.getMessage());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Book details not entered correctly!");
                }
            }
        });
    }


}
