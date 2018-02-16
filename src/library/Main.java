package library;

import javafx.scene.control.RadioButton;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Main {
    private JPanel panelMain;
    private JTable tableBooks;
    private JTextField txtSearch;
    private JButton btnLoan;
    private JButton btnAdd;
    private JButton btnDel;
    private JButton btnReturn;
    private JButton btnEdit;
    private JButton btnSave;
    private JButton btnReload;
    private JLabel lblMatches;
    private JPanel panelButtons;
    private JLabel lblSearch;
    private JScrollPane scrollPaneTable;

    private String bookData = "data/books.txt";
    private String memberData = "data/members.txt";
    private String loanData = "data/bookloans.txt";

    public Main() {

        Library lib = new Library(bookData, memberData, loanData);
        configInterface(lib);

    }

    public void configTable(List<Book> bookshelf){
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTable(bookshelf, txtSearch.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTable(bookshelf, txtSearch.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTable(bookshelf, txtSearch.getText());
            }
        });
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        String[] columnNames = {"ID", "Title", "Authors", "Publish Date", "Quantity"};
        for (String col : columnNames){
            model.addColumn(col);
        }
        tableBooks = new JTable(model);



        updateTable(bookshelf, null);
    }

    public void updateTable(List<Book> books, String query){
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        model.setRowCount(0);
        for (int i = 0; i < books.size(); i++){
            String[] book = books.get(i).formatData();
            for (int j = 0; j < model.getColumnCount(); j++) {
                if (query == null || book[j].toLowerCase().contains(query.toLowerCase())) {
                    model.addRow(book);
                    break;
                }
            }
        }
        lblMatches.setText(model.getRowCount() < books.size() ? "Matching Results: " + model.getRowCount() : "");
        tableBooks = new JTable(model);

    }

    public void configInterface(Library lib){

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            System.out.println("CHANGING UI THEME SUCCESSFUL");
        } catch (Exception e) {
            System.out.println("CHANGING UI THEME FAILED");
        }

        configTable(lib.getBookshelf());

        btnLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "LOANING");
                lib.borrowBook();
            }
        });

        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "RETURNING");
                lib.returnBook();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "ADDING");
                lib.addNewBook();
            }
        });

        btnDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "REMOVING");
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "EDITING");
                lib.changeQuantity();
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "SAVING");
                lib.saveChanges(bookData, memberData, loanData);
            }
        });

        btnReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "RELOADING");
            }
        });
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Library Project");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
