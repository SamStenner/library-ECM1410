package library;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.time.LocalDate;
import java.util.List;

import static javax.swing.UIManager.getInstalledLookAndFeels;

public class Main {
    private JPanel panelMain;
    private JTable tableBooks;
    private JTextField txtSearch;
    private JButton btnLoan;
    private JButton btnAdd;
    private JButton btnDelBook;
    private JButton btnReturn;
    private JButton btnEdit;
    private JButton btnSave;
    private JButton btnReload;
    private JLabel lblMatches;
    private JPanel panelButtons;
    private JLabel lblSearchBooks;
    private JScrollPane scrollPaneBooks;
    private JTabbedPane tabAreas;
    private JPanel tabBooks;
    private JPanel tabMembers;
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
    private JPanel tabAdmin;
    private JTable tableAllMembers;
    private JTable tableAllLoans;
    private JLabel lblAllMembers;
    private JLabel lblAllLoans;
    private JButton btnAllLoanDetails;
    private JButton btnDelLoan;
    private JPanel panelSearch;
    private JTextField txtSearchMembers;
    private JButton btnClearBooks;
    private JButton btnClearMembers;
    private JLabel lblSearchMembers;

    private String bookData = "data/books.txt";
    private String memberData = "data/members.txt";
    private String loanData = "data/bookloans.txt";

    private static JFrame loginFrame;
    private static JFrame mainFrame;

    private Member currentMember;

    private List<Loan> listLoans;

    private Book selectedBook;
    private Loan selectedLoan;

    private Library lib;

    public Main() {
        lib = new Library(bookData, memberData, loanData);
        btnLogout.addActionListener((ActionEvent e) -> {
            mainFrame.dispose();
            showForm();
        });
    }

    public static void main(String[] args){
        showForm();
    }

    public static void showLogin(Main main){
        loginFrame = new JFrame("Login");
        loginFrame.setContentPane(new Login(main).panelLogin);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setVisible(true);
        loginFrame.toFront();
    }

    public void showAddBook(){
        JFrame addBookFrame = new JFrame("Add Book");
        AddBook addBook = new AddBook(lib, addBookFrame, this);
        addBookFrame.setContentPane(addBook.panelAddBook);
        addBookFrame.pack();
        addBookFrame.setSize(addBookFrame.getWidth() + 20, addBookFrame.getHeight() + 20);
        addBookFrame.setVisible(true);
        addBookFrame.toFront();
    }

    public void showLoanInfo(){
        if (selectedLoan != null) {
            JFrame loanFrame = new JFrame("Loan Info");
            Book loanedBook = lib.searchBook(selectedLoan.getBookID());
            Member loanMember = lib.searchMember(selectedLoan.getMemberID());
            loanFrame.setContentPane(new LoanInfo(selectedLoan, loanedBook, loanMember).panelLoanInfo);
            loanFrame.pack();
            loanFrame.setSize(loanFrame.getWidth() + 20, loanFrame.getHeight() + 20);
            loanFrame.setVisible(true);
            loanFrame.toFront();
        } else {
            JOptionPane.showMessageDialog(null, "No loan selected!");
        }
    }

    public static void showForm() {
        setStyle(1);
        Main main = new Main();
        mainFrame = new JFrame("Library Project");
        mainFrame.setContentPane(main.panelMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 450);
        mainFrame.setVisible(true);
        mainFrame.setEnabled(false);
        showLogin(main);
    }

    public static void setStyle(int style){
        try {
            UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[style].getClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configBookTable(){
        txtSearchMembers.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMembersTable(txtSearchMembers.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMembersTable(txtSearchMembers.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMembersTable(txtSearchMembers.getText());
            }
        });
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        String[] columnNames = {"Book ID", "Title", "Authors", "Publish Date", "Quantity"};
        createTableColumns(model, columnNames);
        updateBookTable( null);
        List<Book> bookshelf = lib.getBookshelf();
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
        txtSearch.setText(null);
    }

    public void configLoanTable() {
        listLoans = lib.getMemberLoanList(currentMember.getID());
        DefaultTableModel model = (DefaultTableModel) tableLoans.getModel();
        String[] columnNames = {"Loan ID", "Book ID", "Member ID", "Loan Date", "Return Date"};
        createTableColumns(model, columnNames);
        addLoanRows(model, listLoans);
        TableColumnModel tcm = tableLoans.getColumnModel();
        tcm.removeColumn(tcm.getColumn(2));
        tableLoans.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                getSelectedLoan(tableLoans, listLoans);
            }
        });
    }

    public void configAllLoansTable() {
        List<Loan> listAllLoans = lib.getLoanList();
        DefaultTableModel model = (DefaultTableModel) tableAllLoans.getModel();
        String[] columnNames = {"Loan ID", "Book ID", "Member ID", "Loan Date", "Return Date"};
        createTableColumns(model, columnNames);
        addLoanRows(model, listAllLoans);
        tableAllLoans.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                getSelectedLoan(tableAllLoans, lib.getLoanList());
            }
        });
    }

    public void configAllMembersTable(){
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
        DefaultTableModel model = (DefaultTableModel) tableAllMembers.getModel();
        String[] columnNames = {"Member ID", "First Name", "Last Name", "Join Date"};
        createTableColumns(model, columnNames);
        updateMembersTable(null);
        txtSearchMembers.setText(null);
    }

    private void updateBookTable(String query){
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        model.setRowCount(0);
        List<Book> books = lib.searchBook(query);
        for (Book book : books) {
            model.addRow(book.formatData());
        }
        int matches = books.size();
        lblMatches.setText(txtSearch.getText().equals("") ? "" : "Matching Results: " + matches);
    }

    private void updateMembersTable(String query) {
        DefaultTableModel model = (DefaultTableModel) tableAllMembers.getModel();
        model.setRowCount(0);
        List<Member> members = lib.searchMember(query);
        for (Member member : members) {
            model.addRow(member.formatData());
        }
    }

    private void showAccountDetails(){
        lblAccountID.setText(Integer.toString(currentMember.getID()));
        lblForeName.setText(currentMember.getForeName());
        lblLastName.setText(currentMember.getLastName());
        lblCreatedDate.setText(currentMember.getRegisterDate().toString());
    }

    private void configForm(){

        configInterface();

        btnClearBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText(null);
            }
        });

        btnLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBook != null) {
                    try {
                        Book loaningBook = selectedBook;
                        lib.borrowBook(loaningBook.getBookID(), currentMember.getID());
                        lib.loadData();
                        configInterface();
                        JOptionPane.showMessageDialog(null, "Successfully loaned book:\n" + loaningBook.getBookTitle());
                    } catch (RuntimeException re){
                        JOptionPane.showMessageDialog(null, re.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No book selected!");
                }
            }
        });

        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLoan != null) {
                    if (MiscOperations.calculateFine(selectedLoan.getBorrowDate()) > 0) {
                        JOptionPane.showMessageDialog(null, "You must pay a fine first!");
                        return;
                    }
                    Book returnBook = lib.searchBook(selectedLoan.getBookID());
                    lib.returnBook(selectedLoan.getLoanID());
                    lib.loadData();
                    configInterface();
                    JOptionPane.showMessageDialog(null, "Successfully returned book:\n" + returnBook.getBookTitle());
                } else {
                    JOptionPane.showMessageDialog(null, "No loan selected!");
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddBook();
            }
        });

        btnDelBook.addActionListener(new ActionListener() {
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

        btnLoanDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoanInfo();
            }
        });

        btnAllLoanDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoanInfo();
            }
        });

        btnDelLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLoan != null) {
                    lib.returnBook(selectedLoan.getLoanID());
                    lib.loadData();
                    configInterface();
                } else {
                    JOptionPane.showMessageDialog(null, "No loan selected!");
                }
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
                configInterface();
                JOptionPane.showMessageDialog(null, "Reloaded from file!");
            }
        });
    }

    private void configInterface(){
        selectedLoan = null;
        selectedBook = null;
        configBookTable();
        configLoanTable();
        configAllLoansTable();
        configAllMembersTable();
        showAccountDetails();
    }

    private void createTableColumns(DefaultTableModel model, String[] columnNames){
        model.setRowCount(0);
        model.setColumnCount(0);
        for (String col : columnNames) {
            model.addColumn(col);
        }
    }

    private void addLoanRows(DefaultTableModel model, List<Loan> loans){
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            Book book = lib.searchBook(loan.getBookID());
            Member member = lib.searchMember(loan.getMemberID());
            String loanID = Integer.toString(loan.getLoanID());
            String bookID = Integer.toString(book.getBookID());
            String memberID = Integer.toString(member.getID());
            String borrowDate = loan.getBorrowDate().toString();
            String returnDate = loan.getBorrowDate().plusDays(30).toString();
            String[] rowData = {loanID, bookID, memberID, borrowDate, returnDate};
            model.addRow(rowData);
        }
    }

    private void getSelectedLoan(JTable table, List<Loan> loans) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            for (Loan loan : loans) {
                String[] loanData = loan.formatData();
                if (loanData[0].equals(table.getModel().getValueAt(selectedRow, 0))) {
                    selectedLoan = loan;
                }
            }
        }
    }

    public void submitCredentials(String foreName, String lastName) {
        Member loginMember = lib.searchMember(foreName, lastName);
        if (loginMember != null) {
            mainFrame.setEnabled(true);
            loginFrame.dispose();
            currentMember = loginMember;
            configForm();
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
