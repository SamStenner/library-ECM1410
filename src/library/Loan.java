package library;

import java.time.LocalDate;
/**
 * This class is to represent a loan in a library system
 * @author Sam Stenner, Max Prüstel
 */
public class Loan {
    /**
     * the unique ID for a Loan in the integer form of 3XXXXX
     */
    private int loanID;
    
    /**
     * the unique ID of the book to be lent 
     * @see class Book
     */
    private int bookID;
    
    /**
     * the unique ID of the member requesting the loan.
     * @see class Member
     */
    private int memberID;
    
    /**
     * A LocalDate object containing the date of the loan taken out.
     */
    private LocalDate borrowDate;
    
    /**
     * A LocalDate object containing the return date of the loan.
     */
    private LocalDate returnDate;
    
    /**
     * The constructor for a Loan object, asking for all relevant information 
     * for a loan to be taken out.
     * @param loanID unique ID the loan should have
     * @param bookID unique book ID to be lent
     * @param memberID unique member ID of the member to take out the loan.
     * @param borrowDate LocalDate of the borrow date
     */
    public Loan(int loanID, int bookID, int memberID, LocalDate borrowDate) {
        this.loanID = loanID;
        this.bookID = bookID;
        this.memberID = memberID;
        this.borrowDate = borrowDate;
        this.returnDate = borrowDate.plusDays(30);
    }
    
    /**
     * formatData gives a String array containing all relevant loan informations
     * @return loan information as a string
     */
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
