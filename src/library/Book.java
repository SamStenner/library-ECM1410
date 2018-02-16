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
        String bookAuthorsStr = Arrays.toString(bookAuthors);
        String[] data = {Integer.toString(bookID),
                         bookTitle,
                         bookAuthorsStr.substring(1, bookAuthorsStr.length() - 1),
                         Integer.toString(publishYear),
                         Integer.toString(quantity)};
        return data;
    }

}
