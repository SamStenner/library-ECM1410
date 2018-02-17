package library;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {

    private Member currentMember;

    private List<Book> bookshelf = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private List<Loan> loanList = new ArrayList<>();

    private String bookData;
    private String memberData;
    private String loanData;

    public Library() {

    }

    public Library(String bookData, String memberData, String bookLoanData) {
        try {
            this.bookData = bookData;
            this.memberData = memberData;
            this.loanData = bookLoanData;
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
            BufferedReader br = new BufferedReader(new FileReader(bookData));
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
            br = new BufferedReader(new FileReader(loanData));
            while ((line = br.readLine()) != null) {
                String[] loanProps = line.split(",");
                int loanID = Integer.parseInt(loanProps[0]);
                int bookID = Integer.parseInt(loanProps[1]);
                int memberID = Integer.parseInt(loanProps[2]);
                LocalDate loanDate = LocalDate.parse(loanProps[3]);
                Loan loan = new Loan(loanID, bookID, memberID, loanDate);
                loanList.add(loan);
            }
            br = new BufferedReader(new FileReader(memberData));
            while ((line = br.readLine()) != null) {
                String[] memberProps = line.split(",");
                int memberID = Integer.parseInt(memberProps[0]);
                String memberFirstName = memberProps[1];
                String memberLastName = memberProps[2];
                LocalDate registerDate = LocalDate.parse(memberProps[3]);
                Member member = new Member(memberID, memberFirstName, memberLastName, registerDate);
                memberList.add(member);
            }
        } catch (Exception e) {
            System.out.println("Could not find data files!");
        }

    }

    //region Book Functions

    public List<Book> searchBook(String query) {
        List<Book> matchingBooks = new ArrayList<>();
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
        List<Loan> memberLoans = new ArrayList<>();
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
        for (Member member : memberList) {
            if (member.getForeName().equals(foreName) && member.getLastName().equals(lastName)){
                return member;
            }
        }
        return null;
    }

    public void addNewMember(String foreName, String lastName,  LocalDate subDate) {
        int newID = memberList.get(memberList.size() - 1).getID() + 1;
        String entry = newID + "," + foreName + "," + lastName + "," + subDate.toString();
        writeData(memberData, entry);
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

    public void writeData(String fileData, String text){
        try {
            Writer output = new BufferedWriter(new FileWriter(fileData, true));
            output.append("\n" + text);
            output.close();
        } catch (Exception e) {
            System.out.println("Could not find data files!");
        }
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
