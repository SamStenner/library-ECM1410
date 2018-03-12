package library;

import java.util.List;

public class Book {

    private int bookID;
    private String bookTitle;
    private String[] bookAuthors;
    private int publishYear;
    private int quantityTotal;

    public Book() {

    }

    public Book(int bookID, String bookTitle, String[] bookAuthors, int publishYear, int quantity) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.bookAuthors = bookAuthors;
        this.publishYear = publishYear;
        this.quantityTotal = quantity;
    }

    public String[] formatData(List<Loan> loanList) {
        String[] data = {Integer.toString(bookID),
            bookTitle,
            getBookAuthors(true),
            Integer.toString(publishYear),
            Integer.toString(getAvailable(loanList)),
            Integer.toString(quantityTotal)};
        return data;
    }

    public int getBookID() {
        return this.bookID;
    }

    public String getBookTitle() {
        return this.bookTitle;
    }

    public String getBookAuthors(boolean niceFormat) {
        return String.join(niceFormat ? ", " : ":", this.bookAuthors);
    }

    public int getPublishYear() {
        return this.publishYear;
    }

    public void setQuantity(int delta) {
        quantityTotal += delta;
    }

    public int getQuantityTotal() {
        return quantityTotal;
    }

    public int getAvailable(List<Loan> loanList) {
        int quantity = getQuantityTotal();
        for (Loan loan : loanList) {
            if (loan.getBookID() == getBookID()) {
                quantity--;
            }
        }
        return quantity;
    }

    public String toString(List<Loan> loanList) {
        String returnString = String.format("Book ID: %d "
                + "\nTitle: %s "
                + "\nAuthors: %s "
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

    @Override
    public String toString() {
        String returnString = getBookID()+","+getBookTitle()+","+getBookAuthors(false)+","+getPublishYear()+","+getQuantityTotal();
        return returnString;
    }

}
