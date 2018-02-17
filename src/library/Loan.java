package library;

import java.time.LocalDate;

public class Loan {

    private int loanID;
    private int bookID;
    private int memberID;
    private LocalDate borrowDate;

    public Loan(int loanID, int bookID, int memberID, LocalDate borrowDate) {
        this.loanID = loanID;
        this.bookID = bookID;
        this.memberID = memberID;
        this.borrowDate = borrowDate;
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
}
