package library;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddBook {

    //region Used UI Elements
    private JTextField txtTitle;
    private JTextField txtAuthors;
    private JTextField txtPublished;
    private JTextField txtQuantity;
    private JButton btnSubmit;
    public JPanel panelAddBook;
    //endregion

    //region Unused UI Elements
    private JLabel lblHeader;
    private JLabel lblTitle;
    private JLabel lblAuthors;
    private JLabel lblPublished;
    private JLabel lblQuantity;
    //endregion

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
                    lib.addNewBook(txtTitle.getText(), authors , published, quantity);
                    main.configBookTable();
                    thisFrame.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Book details not entered correctly:\n" + ex.getClass().getSimpleName(), "Add Book", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

}
