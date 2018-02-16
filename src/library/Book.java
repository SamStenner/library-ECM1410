package library;

import java.util.Arrays;
import java.util.Vector;

public class Book {

    private int bookID;
    private String bookTitle;
    private Vector<String> bookAuthors;
    private int publishYear;
    private int quantity;

    public Book() {

    }

    public Book(String bookTitle){

    }

    public Book(int bookID, String bookTitle, String[] bookAuthors, int publishYear, int quantity) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.bookAuthors = new Vector<>(Arrays.asList(bookAuthors));
        this.publishYear = publishYear;
        this.quantity = quantity;
    }

    public String[] formatData(){
        String authors = Arrays.toString(bookAuthors.toArray(new String[bookAuthors.size()]));
        String[] data = {Integer.toString(bookID),
                         bookTitle,
                         authors.substring(1, authors.length()-1),
                         Integer.toString(publishYear),
                         Integer.toString(quantity)};
        return data;
    }

}
