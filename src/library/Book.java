package library;

import java.util.List;
/**
 * This class represents a book in the library system.
 * @author Sam Stenner, Max Prüstel
 * @version 1
 */
public class Book {
    // A unique identifier for a book, in the integer form 1XXXXX
    private int bookID;
    // The book title
    private String bookTitle;
    // The list of authors of the book
    private String[] bookAuthors;
    // The year the book was published
    private int publishYear;
    // The total number of books the library has, uninfluenced by the number of loans
    private int quantityTotal;
    
    /**
     * The only constructor for a book, asking for the ID to be assigned, 
     * the title, the authors, the year of publication and the stocked quantity.
     * @param bookID unique number for the book (in the form 1XXXXX)
     * @param bookTitle title of the book
     * @param bookAuthors name(s) of the author(s)
     * @param publishYear year of publication
     * @param quantity stock quantity
     */
    public Book(int bookID, String bookTitle, String[] bookAuthors, int publishYear, int quantity) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.bookAuthors = bookAuthors;
        this.publishYear = publishYear;
        this.quantityTotal = quantity;
    }

    /**
     * gets a string of the book's data in a nicer format
     * @param loanList the list of loans
     * @return the book data
     */
    public String[] formatData(List<Loan> loanList) {
        String[] data = {Integer.toString(bookID),
            bookTitle,
            getBookAuthors(true),
            Integer.toString(publishYear),
            Integer.toString(getAvailable(loanList)),
            Integer.toString(quantityTotal)};
        return data;
    }

    /**
     * getter for the book ID
     * @return the unique book ID number
     */
    public int getBookID() {
        return this.bookID;
    }

    /**
     * getter for the book title
     * @return title of the book
     */
    public String getBookTitle() {
        return this.bookTitle;
    }
    
    /**
     * Returns the author name(s) as a concatenated string
     * @param niceFormat determines whether the author names are comma seperated
     * (for console output) or colon seperated (file output).
     * @return author name(s)
     */
    public String getBookAuthors(boolean niceFormat) {
        return String.join(niceFormat ? ", " : ":", this.bookAuthors);
    }

    /**
     * Getter for Year of publication as an integer
     * @return Year of publication
     */
    public int getPublishYear() {
        return this.publishYear;
    }
    
    /**
     * Setter for the book quantity. The argument shall not be the total new 
     * quantity but the change in quantity.
     * @param delta the number of books to be added (negative numbers supported)
     */
    public void setQuantity(int delta) {
        quantityTotal += delta;
    }
    
    /**
     * Getter for the total stock quantity of a book.
     * @return total stock quantity
     */
    public int getQuantityTotal() {
        return quantityTotal;
    }
    
    /**
     * Getter for the available stock quantity of a book, using a list of Loan 
     * objects, to deduct books currently on loan.
     * @param loanList a List \< Loan \> of loans to be taken into account for 
     * the deduction. 
     * @return Number of books currently not on loan
     */
    public int getAvailable(List<Loan> loanList) {
        int quantity = getQuantityTotal();
        for (Loan loan : loanList) {
            if (loan.getBookID() == getBookID()) {
                quantity--;
            }
        }
        return quantity;
    }
    
    /**
     * formedString produces a console formatted output of the book data:
     * Title, Author(s), Year of publication, Quantity available, total quantity
     * @param loanList a List \< Loan \> of loans to be taken into account for 
     * the calculation of the available quantity. 
     * @return a concatenated string containing book information
     */
    public String formedString(List<Loan> loanList) {
        String returnString = String.format("Book ID: %d "
                + "\nTitle: %s "
                + "\nAuthor(s): %s "
                + "\nYear of publication: %d "
                + "\nNumber available: %d"
                + "\nTotal quantity: %d",
                getBookID(),
                getBookTitle(),
                getBookAuthors(true),
                getPublishYear(),
                getAvailable(loanList),
                getQuantityTotal());
        return returnString;
    }

    /**
     * returns a string with the book data, which is formatted for file output
     * @override toString()
     * @return book information
     */
    @Override
    public String toString() {
        String returnString = getBookID()+","+getBookTitle()+","+getBookAuthors(false)+","+getPublishYear()+","+getQuantityTotal();
        return returnString;
    }

}
