package library;

import java.util.Arrays;
import java.util.Vector;

public class Book {

    private int bookID;
    private String bookTitle;
    private String[] bookAuthors;
    private int publishYear;
    private int quantity;

    public Book() {

    }

    public Book(String bookTitle){

    }

    public Book(int bookID, String bookTitle, String[] bookAuthors, int publishYear, int quantity) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.bookAuthors = bookAuthors;
        this.publishYear = publishYear;
        this.quantity = quantity;
    }

    public String[] formatData(){
        String[] data = {Integer.toString(bookID),
                         bookTitle,
                         getBookAuthorsStr(),
                         Integer.toString(publishYear),
                         Integer.toString(quantity)};
        return data;
    }

    public int getBookID(){
        return this.bookID;
    }

    public String getBookTitle(){
        return this.bookTitle;
    }

    public String[] getBookAuthors(){
        return this.bookAuthors;
    }

    public String getBookAuthorsStr(){
        return String.join(":", this.bookAuthors);
    }

    public int getPublishYear(){
        return this.publishYear;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public void setQuantity(int delta) {
        this.quantity += delta;
    }

    @Override
    public String toString(){
        String returnString = String.format("Book ID: %d \nTitle: %s \nAuthors: "
                + "%s \nYear of publication: %d \nNumber of copies: %d",getBookID(),
                getBookTitle(), getBookAuthorsStr(),getPublishYear(), getQuantity());
        return returnString;
    }

}
