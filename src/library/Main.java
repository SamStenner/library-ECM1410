package library;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
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
    private JScrollPane scrollPaneBooks;
    private JTabbedPane tabAreas;
    private JPanel tabBooks;
    private JPanel tabMembers;
    private JButton btnClear;
    private JLabel lblForeNameHeader;
    private JLabel lblLastNameHeader;
    private JLabel lblForeName;
    private JLabel lblLastName;
    private JPanel panelAccountDetails;
    private JTable tableLoans;
    private JLabel lblAccountDetails;
    private JLabel lblCreatedDateHeader;
    private JLabel lblCreatedDate;
    private JScrollPane scrollPaneLoans;
    private JLabel lblLoans;
    private JButton btnLogout;
    private JButton btnLoanDetails;
    private JLabel lblAccountIDHeader;
    private JLabel lblAccountID;

    private String bookData = "data/books.txt";
    private String memberData = "data/members.txt";
    private String loanData = "data/bookloans.txt";

    private static JFrame loginFrame;
    private static JFrame mainFrame;

    private Member currentMember;
    private Member currentLoans;

    private Book selectedBook;
    private Library lib;

    public Main() {
        lib = new Library(bookData, memberData, loanData);
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                showForm();
            }
        });
    }

    public static void showLogin(Main main){
        loginFrame = new JFrame("Login");
        loginFrame.setContentPane(new Login(main).panelLogin);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setVisible(true);
        loginFrame.toFront();
    }

    public static void showForm() {
        Main main = new Main();
        mainFrame = new JFrame("Library Project");
        mainFrame.setContentPane(main.panelMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 450);
        mainFrame.setVisible(true);
        mainFrame.setEnabled(false);
        showLogin(main);
    }

    public void configBookTable(){
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBookTable(txtSearch.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBookTable(txtSearch.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) { updateBookTable(txtSearch.getText());
            }
        });
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        String[] columnNames = {"Book ID", "Title", "Authors", "Publish Date", "Quantity"};
        model.setColumnCount(0);
        for (String col : columnNames){
            model.addColumn(col);
        }
        updateBookTable( null);
    }

    public void configLoanTable() {
        List<Loan> loanList = lib.getMemberLoanList(currentMember.getID());
        DefaultTableModel model = (DefaultTableModel) tableLoans.getModel();
        String[] columnNames = {"Loan ID", "Title", "Loan Date", "Return Date"};
        model.setRowCount(0);
        model.setColumnCount(0);
        for (String col : columnNames) {
            model.addColumn(col);
        }
        for (int i = 0; i < loanList.size(); i++){
            String[] loanData = createLoanRow(loanList.get(i));
            model.addRow(loanData);
        }
    }

    public void updateBookTable(String query){
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        model.setRowCount(0);
        List<Book> books = lib.searchBook(query);
        if (books == null) {
            books = lib.getBookshelf();
        }
        for (Book book : books) {
            model.addRow(book.formatData());
        }
        int matches = books.size();
        lblMatches.setText(txtSearch.getText().equals("") ? "" : "Matching Results: " + matches);
    }

    public void showAccountDetails(){
        lblAccountID.setText(Integer.toString(currentMember.getID()));
        lblForeName.setText(currentMember.getForeName());
        lblLastName.setText(currentMember.getLastName());
        lblCreatedDate.setText(currentMember.getRegisterDate().toString());
    }

    public void configInterface(){

        try {
           // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            System.out.println("CHANGING UI THEME SUCCESSFUL");
        } catch (Exception e) {
            System.out.println("CHANGING UI THEME FAILED");
        }

        List<Book> bookshelf = lib.getBookshelf();

        configBookTable();
        configLoanTable();
        showAccountDetails();

        tableBooks.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tableBooks.getSelectedRow();
                if (selectedRow != -1) {
                    for (Book book : bookshelf) {
                        String[] bookData = book.formatData();
                        if (bookData[0].equals(tableBooks.getModel().getValueAt(selectedRow, 0))) {
                            selectedBook = book;
                        }
                    }
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText(null);
            }
        });

        btnLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(null, "No book selected!");
                } else {
                    JOptionPane.showMessageDialog(null, selectedBook.getBookTitle());
                    lib.borrowBook();
                }
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
                lib.loadData();
                configBookTable();
                configLoanTable();
                JOptionPane.showMessageDialog(null, "Reloaded from file!");
            }
        });
    }

    public String[] createLoanRow(Loan loan) {
        String loanID = Integer.toString(loan.getLoanID());
        Book book = lib.searchBook(loan.getBookID());
        String bookTitle = book.getBookTitle();
        String borrowDate = loan.getBorrowDate().toString();
        String returnDate = loan.getBorrowDate().plusDays(30).toString();
        String[] rowData = {loanID, bookTitle, borrowDate, returnDate};
        return rowData;
    }

    public static void main(String[] args){
        showForm();
    }

    public void submitCredentials(String foreName, String lastName) {
        Member loginMember = lib.searchMember(foreName, lastName);
        if (loginMember != null) {
            mainFrame.setEnabled(true);
            loginFrame.dispose();
            currentMember = loginMember;
            configInterface();
        } else {
            JOptionPane.showMessageDialog(null, "Couldn't find member!");
        }
    }

    public void registerMember(String foreName, String lastName) {
        Member loginMember = lib.searchMember(foreName, lastName);
        if (loginMember == null) {
            lib.addNewMember(foreName, lastName, LocalDate.now());
            lib.loadData();
            submitCredentials(foreName, lastName);
        } else {
            JOptionPane.showMessageDialog(null, "Member already exists!");
        }
    }

}
