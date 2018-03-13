package library;

import java.time.LocalDate;
/**
 * This class is to represent a loan in a library system
 * @author Sam Stenner, Max Prüstel
 */
public class Loan {
    private int loanID;
    private int bookID;
    private int memberID;
    private LocalDate borrowDate;
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
    
    /**
     * Getter for the unique Loan ID
     * @return loan ID of the object
     */
    public int getLoanID(){
        return loanID;
    }
    
    /**
     * Getter for the unique book ID concerned with this loan.
     * @see class Book
     * @return unique book ID
     */
    public int getBookID(){
        return bookID;
    }

    /**
     * Getter for the unique member ID concerned with this loan.
     * @see class Member
     * @return unique member ID
     */
    public int getMemberID(){
        return memberID;
    }
    /**
     * Getter for the borrow date of the loan in a LocalDate object
     * @return borrow date
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Getter for the return date of a loan as a LocalDate object
     * @return return date of the loan
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    /**
     * This function returns the fine due for a borrow date using calculateFine from MiscOperations.
     * @see MiscOperations
     * @return fine in a floating point format 
     */
    public double getFine(){
        return MiscOperations.calculateFine(borrowDate);
    }
    
    /**
     * formedString produces a console formatted output of the loan data:
     * Loan ID, Book ID, MemberID, BorrowDate, Return date, latefine
     * @return a concatenated string containing book information
     */
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
    
    /**
     * returns a string with the loan data, which is formated for file output
     * @override Object.toString()
     * @return a String of the loan data, commaseperated
     */
    @Override
    public String toString(){
        return String.join(",", formatData());
    }

}
