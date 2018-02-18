package library;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.HashMap;

public class Library {

    private Member currentMember;

    private List<Book> bookshelf = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private List<Loan> loanList = new ArrayList<>();
    private HashMap<Book, Integer> BookLoanQuant = new HashMap<>();

    private String bookDatFile;
    private String memberDatFile;
    private String loanDatFile;

    public Library() {

    }

    public Library(String bookData, String memberData, String bookLoanData) {
        try {
            this.bookDatFile = bookData;
            this.memberDatFile = memberData;
            this.loanDatFile = bookLoanData;
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        bookshelf.clear();
        memberList.clear();
        loanList.clear();
        
        try {
            ArrayList<String> lines = MiscOperations.readFile(bookDatFile);
            bookshelf = MiscOperations.initBooks(lines);
            lines.clear();
            lines = MiscOperations.readFile(loanDatFile);
            loanList = MiscOperations.initLoans(lines);
            lines.clear();
            lines = MiscOperations.readFile(memberDatFile);
            memberList = MiscOperations.initMembers(lines);
         
        } catch (Exception e) {
            System.out.println("Could not find data files or files corrupted.");
        }
        for (Loan loan: loanList){
            Book lentBook = null;
            for (Book book: bookshelf){//find book
                if(book.getBookID() == loan.getBookID()){
                    lentBook = book;
                    break;
                }
            }
            if(lentBook != null){
                if(null != BookLoanQuant.putIfAbsent(lentBook, 1)){
                    BookLoanQuant.put(lentBook, BookLoanQuant.get(lentBook)+1);
                }
            }
        }

    }

    //region Book Functions
    public void searchBook(){
        System.out.println("You are now in the book search tool, please enter a"
                + " book ID, a title or an author.");
        String query = " ";
        try{
            query = MiscOperations.getInput();
        }
        catch (InputException e){
            System.out.println("An error occured while waiting for input. "
                    + "You will be redirected to the main menu, "
                    + "then please try again.");
            return;
        }
        ArrayList<Book> results = searchBook(query);
        if (results.size() > 1){
            Book result = results.remove(0);
            System.out.println("Your search result is:");
            System.out.println(result.toString());
            System.out.println("Copies available: " + 
                    ((BookLoanQuant.get(result) == null)
                    ?result.getQuantity(): 
                    result.getQuantity()-BookLoanQuant.get(result)));
            
        }
        else{
            System.out.println("Your search results are:");
            for(Book book:results){
                System.out.println(book.toString());
                
            }
        }
        
        
        
       
    }
    public ArrayList<Book> searchBook(String query) {
        ArrayList<Book> matchingBooks = new ArrayList<>();
        query = query == null ? "" : query.toLowerCase();
        for (Book book : bookshelf) {
            String bookID = Integer.toString(book.getBookID()).toLowerCase();
            String title = book.getBookTitle().toLowerCase();
            String author = book.getBookAuthorsStr().toLowerCase();
            if (bookID.contains(query) || title.contains(query) || author.contains(query)) {
                matchingBooks.add(book);
            }
        }
        return matchingBooks;
    }

    public Book searchBook(int bookID) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                return book;
            }
        }
        return null;
    }

    public void borrowBook(String bookTitle, String memberName, String borrowDate){

    }

    public void borrowBook(String bookTitle, String memberName){
        borrowBook(bookTitle,memberName,LocalDate.now().toString());
    }

    public void borrowBook(int bookID, int memberID, String borrowDate){

    }

    public void borrowBook(int bookID, int memberID){

    }

    public void returnBook(int loanID) {

    }

    public void addNewBook(String bookTitle, String[] authorNames, int publishYear, int numberOfCopies) {

    }

    public void changeQuantity(String bookTitle, int quantity) {

    }

    public int getBookshelfSize(){
        return bookshelf.size();
    }

    public List<Book> getBookshelf(){
        return bookshelf;
    }

    public List<Loan> getLoanList(){
        return loanList;
    }

    public List<Loan> getMemberLoanList(int memberID){
        ArrayList<Loan> memberLoans = new ArrayList<>();
        for (Loan loan : loanList) {
            if (loan.getMemberID() == memberID) {
                memberLoans.add(loan);
            }
        }
        return memberLoans;
    }

    //endregion

    //region Member Functions

    public Member searchMember(String foreName, String lastName) {
        ArrayList<Member> searchResults = new ArrayList<>();
        for (Member member : memberList) {
            if (member.getForeName().equals(foreName) && member.getLastName().equals(lastName)){
                searchResults.add(member);
            }
        }
        return null;
    }

    public void addNewMember(String foreName, String lastName,  LocalDate subDate) {
        int newID = memberList.get(memberList.size() - 1).getID() + 1;
        String entry = newID + "," + foreName + "," + lastName + "," + subDate.toString();
        MiscOperations.writeData(memberDatFile, entry);
    }

    //endregion

    //region Misc


    public void showAllBooks() {

    }
    

    public void showAllMembers() {

    }

    public void showAllBookLoans() {

    }

    public void saveChanges(String bookData, String memberData, String bookLoanData) {

    }

    //endregion

    //region Fucking stupid overloads

    public void searchMember() {

    }

    public void borrowBook() {

    }

    public void returnBook() {

    }

    public void addNewBook() {

    }

    public void addNewMember() {

    }

    public void changeQuantity() {

    }

    //endregion


}
