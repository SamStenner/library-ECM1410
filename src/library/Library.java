package library;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {

    private List<Book> bookshelf = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private List<Loan> loanList = new ArrayList<>();

    public Library() {

    }

    public Library(String bookData, String memberData, String bookLoanData) {
        try {
            loadData(bookData, memberData, bookLoanData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData(String bookData, String memberData, String bookLoanData) {
        bookshelf.clear();
        memberList.clear();
        loanList.clear();
        try {
            FileReader fr = new FileReader(bookData);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] bookProps = line.split(",");
                int ID = Integer.parseInt(bookProps[0]);
                String title = bookProps[1];
                String[] authors = bookProps[2].split(":");
                int year = Integer.parseInt(bookProps[3]);
                int quant = Integer.parseInt(bookProps[4]);
                Book book = new Book(ID, title, authors, year, quant);
                bookshelf.add(book);
            }

            fr = new FileReader(bookLoanData);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                String[] loanProps = line.split(",");
                int loanID = Integer.parseInt(loanProps[0]);
                int bookID = Integer.parseInt(loanProps[1]);
                int memberID = Integer.parseInt(loanProps[2]);
                LocalDate loanDate = LocalDate.parse(loanProps[3]);
                Loan loan = new Loan(loanID, bookID, memberID, loanDate);
                loanList.add(loan);
            }
        } catch (Exception e) {
            System.out.println("Could not find data files!");
        }

        // READ MEMBERS TOO!!

    }

    //region Book Functions

    public void searchBook(String title) {
        
    }

    public void borrowBook(String bookTitle, String memberName, String borrowDate){

    }

    public void borrowBook(String bookTitle, String memberName){

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

    public List<Book> getBookshelf(){
        return bookshelf;
    }

    public List<Loan> getLoanList(){
        return loanList;
    }

    //endregion

    //region Member Functions

    public void searchMember(String foreName, String lastName) {

    }

    public void addNewMember(String lastName, String foreName, LocalDate subDate) {

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

    public void searchBook() {

    }

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
