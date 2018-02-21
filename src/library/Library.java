package library;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Library {

    private Member currentMember;

    private List<Book> bookshelf = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private List<Loan> loanList = new ArrayList<>();
    private HashMap<Book, Integer> BookLoanQuant = new HashMap<>();

    private String bookDatFile;
    private String memberDatFile;
    private String loanDatFile;
    
    private Book selectedBook;
    private Member selectedMember;

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
            e.printStackTrace();
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
        if (results.size() > 1) { // TODO Max should that be >= 1?
            Book result = results.remove(0);
            System.out.println("Your search result is:");
            System.out.println(result.toString());
            System.out.println("Copies available: " +
                    ((BookLoanQuant.get(result) == null)
                            ? result.getQuantity() :
                            result.getQuantity() - BookLoanQuant.get(result)));
        }
        if (results.size() == 1) {
            Book result = results.remove(0);
            System.out.println("Your search result is:");
            System.out.println(result.toString());
            System.out.println("Copies available: " + getAvailableCopies(result));
        }
        else if (results.size() > 1){
            System.out.println("Your search results are:");
            for(Book book:results){
                System.out.println(book.toString());
            }
            System.out.println("Do you want to refine your search? [Y/N]");
            try{
                String input = MiscOperations.getInput();
                if(input.charAt(0) == 'Y' || input.charAt(0) == 'y'){
                    refineSearchBook(results);
                }
                else{
                    System.out.println("No further search initiated, "
                            + "you will be redirected to the main menu.");
                }
            }
            catch (InputException e){
                System.out.println("An error occured with your input.");
                return;
            }
        } else{
            System.out.println("There were no books found matching your query.");
            System.out.println("You will be redirected to the main menu.");
        }
       
    }
    
    public void refineSearchBook(ArrayList<Book> results){
        System.out.println();
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

    public void borrowBook(String bookTitle, String memberForeName, String memberLastName){
        borrowBook(bookTitle, memberForeName, memberLastName, LocalDate.now());
    }

    public void borrowBook(String bookTitle, String memberForeName, String memberLastName, LocalDate borrowDate){
        int bookID = searchBook(bookTitle).get(0).getBookID(); //TODO Handle if search fails
        int memberID = searchMember(memberForeName, memberLastName).getID();
        borrowBook(bookID, memberID, borrowDate);
    }

    public void borrowBook(int bookID, int memberID, LocalDate borrowDate) {
        int borrowed = MiscOperations.getBooksBorrowed(loanList, memberID);
        if (borrowed < 5) {
            int newID = loanList.get(loanList.size() - 1).getLoanID() + 1;
            Loan loan = new Loan(newID, bookID, memberID, borrowDate);
            loanList.add(loan);
            changeQuantity(bookID, -1);
            MiscOperations.writeData(loanDatFile, loan.toString(), true);
            loadData();
        } else {
            throw new RuntimeException("You are already loaning the maximum number of books!");
        }
    }

    public void borrowBook(int bookID, int memberID){
        borrowBook(bookID, memberID, LocalDate.now());
    }

    public void returnBook(int loanID) {
        int counter = 0;
        for (Loan loan : loanList) {
            if (loan.getLoanID() == loanID) {
                loanList.remove(counter);
                MiscOperations.writeData(loanDatFile, MiscOperations.loansToString(loanList), false);
                Book returnBook = searchBook(loan.getBookID());
                changeQuantity(returnBook.getBookTitle(),  1);
                loadData();
                return;
            }
            counter++;
        }
    }

    public void addNewBook(String title, String[] authorNames, int publishYear, int quantity) {
        if (title.length() > 255) {
            throw new RuntimeException("Book title is too long!");
        } else if (authorNames[0] == null) {
            throw new RuntimeException("There cannot be no authors!");
        } else if (publishYear > LocalDate.now().getYear()) {
            throw new RuntimeException("Book must have been already been published!");
        } else if (quantity < 0) {
            throw new RuntimeException("Book quantity must be positive!");
        } else {
            try {
                int newID = bookshelf.get(bookshelf.size() - 1).getBookID() + 1;
                Book newBook = new Book(newID, title, authorNames, publishYear, quantity);
                String entry = newBook.fileEntry();
                MiscOperations.writeData(bookDatFile, entry, true);
                loadData();
            } catch (Exception e) {
                throw new RuntimeException("An unknown error has occurred. Please ensure your entries are valid!");
            }
        }
    }

    public void changeQuantity(String bookTitle, int delta) {
        changeQuantity(searchBook(bookTitle).get(0).getBookID(), delta); //TODO Handle if search fails
    }

    public void changeQuantity(int bookID, int delta) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                if (book.getQuantity() + delta >= 0) {
                    book.setQuantity(delta);
                    MiscOperations.writeData(bookDatFile, MiscOperations.booksToString(bookshelf), false);
                } else {
                    throw new RuntimeException("There are no copies of this book available at this time!");
                }
            }
        }
    }
    
    public int getAvailableCopies(Book query){
        return (BookLoanQuant.get(query) == null)? query.getQuantity(): query.getQuantity()-BookLoanQuant.get(query);
    }

    public int getBookshelfSize(){
        return bookshelf.size();
    }

    public List<Book> getBookshelf(){
        return bookshelf;
    }


    //endregion

    //region Member Functions
    public void searchMember() {

    }

    public Member searchMember(String foreName, String lastName) {
        for (Member member : memberList) {
            if (member.getForeName().equals(foreName) && member.getLastName().equals(lastName)){
                return member;
            }
        }
        return null;
    }

    public ArrayList<Member> searchMember(String query) {
        ArrayList<Member> members = new ArrayList<>();
        query = query == null ? "" : query.toLowerCase();
        for (Member member : memberList) {
            String fullName = member.getFullName().toLowerCase();
            String memberID = Integer.toString(member.getID()).toLowerCase();
            if (fullName.contains(query) || memberID.contains(query)) {
                members.add(member);
            }
        }
        return members;
    }

    public Member searchMember(int userID) {
        for (Member member : memberList) {
            if (member.getID() == userID){
                return member;
            }
        }
        return null;
    }

    public void addNewMember(String foreName, String lastName,  LocalDate subDate) {
        int newID = memberList.get(memberList.size() - 1).getID() + 1;
        String entry = newID + "," + foreName + "," + lastName + "," + subDate.toString();
        MiscOperations.writeData(memberDatFile, entry, true);
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

    public List<Member> getAllMembers() {
        return memberList;
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
        MiscOperations.writeData(bookData, MiscOperations.booksToString(bookshelf), false);
        MiscOperations.writeData(memberData, MiscOperations.membersToString(memberList), false);
        MiscOperations.writeData(bookLoanData, MiscOperations.loansToString(loanList), false);
    }

    public void setSelectedBook(Book selection){
        this.selectedBook = selection;
    }
    
    public Book getSelectedBook(){
        return this.selectedBook;
    }
    //endregion

    //region Fucking stupid overloads



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
