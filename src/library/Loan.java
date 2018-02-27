package library;

import java.time.LocalDate;
import java.util.List;

public class Loan {

    private int loanID;
    private int bookID;
    private int memberID;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Loan(int loanID, int bookID, int memberID, LocalDate borrowDate) {
        this.loanID = loanID;
        this.bookID = bookID;
        this.memberID = memberID;
        this.borrowDate = borrowDate;
        this.returnDate = borrowDate.plusDays(30);
    }

    public String[] formatData(){
        String[] data = {Integer.toString(loanID),
                Integer.toString(bookID),
                Integer.toString(memberID),
                borrowDate.toString()};
        return data;
    }

    public int getLoanID(){
        return loanID;
    }

    public int getBookID(){
        return bookID;
    }

    public int getMemberID(){
        return memberID;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getFine(){
        return MiscOperations.calculateFine(borrowDate);
    }

    public String formedString(){
        String returnString = String.format("Loan ID: %d " +
                        "\nBook ID: %d " +
                        "\nMember ID: %d " +
                        "\nBorrow Date: %s" +
                        "\nReturn Date: %s" +
                        "\nLate fine: %s",
                        getLoanID(),
                        getBookID(),
                        getMemberID(),
                        getBorrowDate().toString(),
                        getReturnDate().toString(),
                        MiscOperations.fineToString(getFine()));
        return returnString;
    }

    @Override
    public String toString(){
        return String.join(",", formatData());
    }

}
