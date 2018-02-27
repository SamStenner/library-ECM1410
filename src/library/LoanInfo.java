package library;

import javax.swing.*;
import java.time.LocalDate;

public class LoanInfo {

    //region Used UI Elements
    public JPanel panelLoanInfo;
    private JLabel lblLoanID;
    private JLabel lblBookID;
    private JLabel lblBookTitle;
    private JLabel lblFineHeader;
    private JLabel lblAuthor;
    private JLabel lblPublishYear;
    private JLabel lblLoanDate;
    private JLabel lblReturnDate;
    private JLabel lblOverdue;
    private JLabel lblFine;
    private JLabel lblMemberID;
    private JLabel lblMemberName;
    //endregion

    //region Unused UI Elements
    private JLabel lblLoanIDHeader;
    private JLabel lblBookIDHeader;
    private JLabel lblTitleHeader;
    private JLabel lblAuthorHeader;
    private JLabel lblBookLoanDate;
    private JLabel lblOverdueHeader;
    private JLabel lblReturnHeader;
    private JLabel lblPublishHeader;
    private JLabel lblHeader;
    private JLabel lblMemberIDHeader;
    private JLabel lblMemberNameHeader;
    private JButton closeDetailsButton;
    //endregion

    public LoanInfo(Loan loan, Book book, Member member) {

        lblLoanID.setText(Integer.toString(loan.getLoanID()));
        lblBookID.setText(Integer.toString(book.getBookID()));
        lblBookTitle.setText(book.getBookTitle());
        lblAuthor.setText(book.getBookAuthors(true));
        lblPublishYear.setText(Integer.toString(book.getPublishYear()));
        lblMemberID.setText(Integer.toString(member.getID()));
        lblMemberName.setText(member.getFullName());
        calculate(loan);


    }

    public void calculate(Loan loan){
        LocalDate borrowDate = loan.getBorrowDate();
        LocalDate returnDate = borrowDate.plusDays(30);
        double fine = MiscOperations.calculateFine(borrowDate);
        lblLoanDate.setText(borrowDate.toString());
        lblReturnDate.setText(returnDate.toString());
        boolean fined = fine > 0;
        lblOverdue.setText(fined ? "Yes" : "No");
        if (fined) {
            lblFine.setText(MiscOperations.fineToString(fine));
        } else {
            lblFineHeader.setEnabled(false);
            lblFineHeader.setVisible(false);
            lblFine.setEnabled(false);
            lblFine.setVisible(false);
        }
    }

}
