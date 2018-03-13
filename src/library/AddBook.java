package library;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI Class for the add book dialog
 */
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

    /**
     * The constructor for the AddBook dialog
     * @param lib the library to add the book to
     * @param thisFrame the window on which the dialog will be based
     * @param main the main frame
     */
    public AddBook(Library lib, JFrame thisFrame, Main main) {

        btnSubmit.addActionListener(new ActionListener() {
            /**
             * Action listener for internal class to deal with user input.
             */
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
