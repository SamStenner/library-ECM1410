package library;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    //region Used UI Elements
    private JPanel panelMain;
    private JTable tableBooks;
    private JTextField txtSearch;
    private JButton btnLoanBook;
    private JButton btnAddBook;
    private JButton btnDelBook;
    private JButton btnReturnBook;
    private JButton btnEditQuantity;
    private JButton btnSave;
    private JButton btnReload;
    private JLabel lblMatches;
    private JTabbedPane tabAreas;
    private JLabel lblForeName;
    private JLabel lblLastName;
    private JTable tableLoans;
    private JLabel lblCreatedDate;
    private JButton btnLogout;
    private JButton btnLoanDetails;
    private JLabel lblAccountID;
    private JTable tableAllMembers;
    private JTable tableAllLoans;
    private JButton btnAllLoanDetails;
    private JButton btnDelLoan;
    private JTextField txtSearchMembers;
    private JButton btnClearBookSearch;
    //endregion

    //region Unused UI Elements
    private JPanel panelButtons;
    private JLabel lblSearchBooks;
    private JScrollPane scrollPaneBooks;
    private JPanel tabBooks;
    private JPanel tabMembers;
    private JLabel lblForeNameHeader;
    private JLabel lblLastNameHeader;
    private JPanel panelAccountDetails;
    private JLabel lblAccountDetails;
    private JLabel lblCreatedDateHeader;
    private JScrollPane scrollPaneLoans;
    private JLabel lblLoans;
    private JLabel lblAccountIDHeader;
    private JPanel tabAdmin;
    private JLabel lblAllMembers;
    private JLabel lblAllLoans;
    private JPanel panelSearch;
    private JButton btnClearMemberSearch;
    private JLabel lblSearchMembers;
    private JButton btnRenewBook;
    private JButton btnRenewLoanAdmin;
    //endregion

    private JFrame loginFrame;
    public JFrame mainFrame;

    private Book selectedBook;
    private Loan selectedLoan;
    private Member currentUser;

    private Library lib;

    public Main(Library lib){
        this.lib = lib;
        mainFrame = new JFrame("Library Mangement System");
        mainFrame.setContentPane(panelMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 450);
        mainFrame.setVisible(true);
        mainFrame.setEnabled(false);
    }

    public void showLogin(){
        loginFrame = new JFrame("Login");
        loginFrame.setContentPane(new Login(this).panelLogin);
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
            JOptionPane.showMessageDialog(null, "No loan selected!", "Loan", JOptionPane.WARNING_MESSAGE);
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
        String[] columnNames = {"Book ID", "Title", "Authors", "Publish Date", "Num Available", "Total Quantity"};
        createTableColumns(model, columnNames);
        updateBookTable( null);
        List<Book> bookshelf = lib.getBookshelf();
        tableBooks.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tableBooks.getSelectedRow();
                if (selectedRow != -1) {
                    for (Book book : bookshelf) {
                        String[] bookData = book.formatData(lib.getLoanList());
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
        List<Loan> listLoans = lib.getMemberLoanList(currentUser.getID());
        DefaultTableModel model = (DefaultTableModel) tableLoans.getModel();
        String[] columnNames = {"Loan ID", "Book Title", "Member Name", "Loan Date", "Return Date", "Late Fine"};
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
        String[] columnNames = {"Loan ID", "Book Title", "Member Name", "Loan Date", "Return Date", "Late Fine"};
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
        ArrayList<Book> books = lib.matchBook(query);
        for (Book book : books) {
            String[] data = book.formatData(lib.getLoanList());
            model.addRow(data);
        }
        int matches = books.size();
        lblMatches.setText(txtSearch.getText().equals("") ? "" : "Matching Results: " + matches);
    }

    private void updateMembersTable(String query) {
        DefaultTableModel model = (DefaultTableModel) tableAllMembers.getModel();
        model.setRowCount(0);
        List<Member> members = lib.matchMember(query);
        for (Member member : members) {
            model.addRow(member.formatData());
        }
    }

    private void showAccountDetails(){
        lblAccountID.setText(Integer.toString(currentUser.getID()));
        lblForeName.setText(currentUser.getForeName());
        lblLastName.setText(currentUser.getLastName());
        lblCreatedDate.setText(currentUser.getRegisterDate().toString());
    }

    private void configForm(){

        configInterface();

        btnClearBookSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText(null);
            }
        });

        btnLoanBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBook != null) {
                    try {
                        Book loaningBook = selectedBook;
                        lib.borrowBook(loaningBook, currentUser.getID(), LocalDate.now());
                        configInterface();
                        JOptionPane.showMessageDialog(null, "Successfully loaned book:\n" + loaningBook.getBookTitle(), "Loan", JOptionPane.INFORMATION_MESSAGE);
                    } catch (RuntimeException ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Loan", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No book selected!", "Book", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnRenewBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLoan != null) {
                    if (selectedLoan.getFine() > 0) {
                        if (!paidFine(selectedLoan)) {
                            return;
                        }
                    }
                    lib.renewLoan(selectedLoan);
                    lib.loadData();
                    configInterface();
                    JOptionPane.showMessageDialog(null,
                            "Book successfully renewed!");
                } else {
                    JOptionPane.showMessageDialog(null, "No loan selected!", "Loan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnReturnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLoan != null) {
                    lib.returnBook(selectedLoan);
                    lib.loadData();
                    configInterface();
                } else {
                    JOptionPane.showMessageDialog(null, "No loan selected!", "Loan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnAddBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddBook();
            }
        });

        btnDelBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBook != null) {
                    lib.removeBook(selectedBook);
                    configInterface();
                    JOptionPane.showMessageDialog(null, "Successfully removed book!", "Book", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No book selected!", "Book", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnEditQuantity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBook != null) {
                    int result = JOptionPane.showOptionDialog(
                            null,
                            null,
                            "Quantity Editor",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new Object[]{"Set quantity", "Increase/Decrease quantity"},
                            null);
                    if (result == -1) {
                        return;
                    }
                    boolean setQuantity = result == JOptionPane.YES_OPTION;
                    String inputQuantity = JOptionPane.showInputDialog("Please enter quantity:");
                    if (inputQuantity != null) {
                        try {
                            Integer quantity = Integer.parseInt(inputQuantity);
                            if (setQuantity) {
                                lib.setQuantity(selectedBook, quantity);
                            } else {
                                lib.changeQuantity(selectedBook, quantity);
                            }
                            configBookTable();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Error: Number incorrectly formatted!", "Book", JOptionPane.ERROR_MESSAGE);
                        } catch (RuntimeException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Book", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No book selected!", "Book", JOptionPane.WARNING_MESSAGE);
                }
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

        btnRenewLoanAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLoan != null) {
                    lib.renewLoan(selectedLoan);
                    lib.loadData();
                    configInterface();
                    JOptionPane.showMessageDialog(null, "Successfully renewed loan!", "Book", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No loan selected!", "Loan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnDelLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLoan != null) {
                    lib.renewLoan(selectedLoan);
                    lib.returnBook(selectedLoan);
                    configInterface();
                } else {
                    JOptionPane.showMessageDialog(null, "No loan selected!", "Loan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lib.saveChanges();
                JOptionPane.showMessageDialog(null, "Saved successfully!", "Files", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lib.loadData();
                configInterface();
                JOptionPane.showMessageDialog(null, "Reloaded from file!", "Files", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                lib.showGUI();
            }
        });

        tabAreas.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                selectedLoan = null;
                tableLoans.clearSelection();
                tableAllLoans.clearSelection();
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
            String bookTitle = book.getBookTitle();
            String memberName = member.getFullName();
            String borrowDate = loan.getBorrowDate().toString();
            String returnDate = loan.getReturnDate().toString();
            String fine = MiscOperations.fineToString(loan.getBorrowDate());
            String[] rowData = {loanID, bookTitle, memberName, borrowDate, returnDate, fine};
            model.addRow(rowData);
        }
    }

    private void getSelectedLoan(JTable table, List<Loan> loans) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            for (Loan loan : loans) {
                if (Integer.toString(loan.getLoanID()).equals(table.getModel().getValueAt(selectedRow, 0))) {
                    selectedLoan = loan;
                }
            }
        }
    }

    public void submitCredentials(String foreName, String lastName) {
        currentUser = lib.searchMember(foreName, lastName);
        if (currentUser != null) {
            mainFrame.setEnabled(true);
            loginFrame.dispose();
            configForm();
        } else {
            JOptionPane.showMessageDialog(null, "Couldn't find member!", "Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registerMember(String foreName, String lastName) {
        Member loginMember = lib.searchMember(foreName, lastName);
        if (loginMember == null) {
            try {
                lib.addNewMember(foreName, lastName, LocalDate.now());
                lib.loadData();
                submitCredentials(foreName, lastName);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Register", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Member already exists!", "Register", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean cancelAddBook(){
        return !(JOptionPane.showConfirmDialog(null, "Book with title already exists! Continue anyway?") == JOptionPane.YES_OPTION);
    }

    public boolean paidFine(Loan loan){
        int result = JOptionPane.showConfirmDialog(null,
                "You have an outstanding fine of: " + MiscOperations.fineToString(loan.getFine()) + "\nPay now?");
        switch (result){
            case JOptionPane.YES_OPTION:
                try {
                    int cashOrCard = JOptionPane.showOptionDialog(
                            null,
                            "How would you like to pay the fine?",
                            "Payment method",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Cash", "Card", "Cancel"},
                            null);
                    if (cashOrCard != JOptionPane.CLOSED_OPTION && cashOrCard != JOptionPane.CANCEL_OPTION) {
                        lib.payFine(selectedLoan, cashOrCard);
                        return true;
                    } else {
                        return false;
                    }
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Loan", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case JOptionPane.NO_OPTION:
                JOptionPane.showMessageDialog(null, "Cannot continue until the fine is paid!", "Loan", JOptionPane.WARNING_MESSAGE);
                return false;
            case JOptionPane.CANCEL_OPTION:
                return false;
        }
        return false;
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(null, message);
    }
}
