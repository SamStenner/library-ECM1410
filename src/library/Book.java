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
        return bookAuthors;
    }

    public String getBookAuthorsStr(){
        String bookAuthorsStr = Arrays.toString(bookAuthors);
        return bookAuthorsStr.substring(1, bookAuthorsStr.length() - 1);
    }

    public int getPublishYear(){
        return publishYear;
    }

    public int getQuantity(){
        return quantity;
    }
    @Override
    public String toString(){
        String authors = "";
        for(String name: getBookAuthors()){
            authors += name + " ";
        }
        String returnString = String.format("Book ID: %d \nTitle: %s \nAuthors: "
                + "%s \nYear of publication: %d \nNumber of copies: %d",getBookID(),
                getBookTitle(), authors,getPublishYear(), getQuantity());
        return returnString;
    }

}
