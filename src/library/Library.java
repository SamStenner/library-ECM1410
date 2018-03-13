package library;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * This class represents a library system containing books, loan functionalities 
 * and therefore members too.
 * This class further supports console guidance besides regular function calls.
 * @author Sam Stenner, Max Prüstel
 */
public class Library {
    /**
     * The Main Form attribute to access the GUI
     */
    private Main GUI;
    
    /**
     * The list that stores all books in the library
     */
    private List<Book> bookshelf = new ArrayList<>();
    
    /**
     * The list that stores all members of the library
     */
    private List<Member> memberList = new ArrayList<>();
    
    /**
     * The list that stores all current loan activity
     */
    private List<Loan> loanList = new ArrayList<>();
    
    /**
     * A value HashMap to determine how many copies of a book are currently 
     * on loan.
     */
    private HashMap<Book, Integer> BookLoanQuant = new HashMap<>();
    
    /**
     * The file path for the book information data file.
     */
    private String bookPath;
    
    /**
     * The file path for the member information file.
     */
    private String memberPath;
    
    /**
     * The file path for the loan information file.
     */
    private String loanPath;
    
    /**
     * enum-like global variables for use in the payment system.
     */
    private static final int METHOD_CASH = 0;
    private static final int METHOD_CARD = 1;
    
    /**
     * boolean on whether to use the GUI
     */
    private boolean usingGUI = false;
    
    /**
     * The constructor for a library object, with all the information needed to start the library application:
     * the three files containing the data for the library
     * @param bookData file path for the book information file
     * @param memberData file path for the member information file
     * @param bookLoanData file path for the member information file
     */
    public Library(String bookData, String memberData, String bookLoanData) {
        this.bookPath = bookData;
        this.memberPath = memberData;
        this.loanPath = bookLoanData;
        loadData();
        System.out.println("Would you like to use the GUI? [Y] | [N]");
        try {
            String query = MiscOperations.getInput().toLowerCase();
            if (query.equals("y")) {
                showGUI();
            }
        } catch (InputException ex) {
            return;
        }
    }


    //region Book Functions
    /**
     * This function searches the bookshelf for a book with a matching ID
     * @param bookID book ID that is searched for
     * @see Book
     * @return Book if a book was found, otherwise null
     */
    public Book searchBook(int bookID) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                System.out.println("\nBook Information:\n" + book.formedString(loanList));
                return book;
            }
        }
        return null;
    }
    
    /**
     * This function searches the bookshelf for books who contain the query 
     * either in their ID or in their title. It recurses as long as it cant find 
     * a single matching book
     * @param query a search string for the books
     * @return a single book
     */
    public Book searchBook(String query) {
        query = query == null ? "" : query.toLowerCase();
        if (query != null) {
            for (Book book : bookshelf) {
                if (book.getBookTitle().toLowerCase().equals(query) ||
                        Integer.toString(book.getBookID()).equals(query)) {
                    System.out.println("\nBook Information:\n" + book.formedString(loanList));
                    return book;
                }
            }
        }
        System.out.println("\nMatching books:");
        ArrayList<Book> matchingBooks = matchBook(query);
        if (matchingBooks.isEmpty()) {
            System.out.println("\nNo book with that name exists");
            if (usingGUI) throw new RuntimeException("Book not found!");
            return null;
        }if (matchingBooks.size() == 1){
            try {
                System.out.println("\nNo exact match found. Is this the intended book? [Y] | [N]");
                boolean foundBook = MiscOperations.getInput().toLowerCase().equals("y");
                if (foundBook) {
                    return searchBook(matchingBooks.get(0).getBookID());
                }
            } catch (InputException ex) {
                System.out.println("\nInput error!");
            }
            return null;
        } else {
            System.out.println("\nPlease refine your search using the list above!");
            return searchBook();

        }
    }
    
    /**
     * This function searches the bookshelf for books who contain the query 
     * either in their title, authors or ID and has the ability of returning 
     * multiple books
     * @param query a search string 
     * @return a list of books that match the query either fully or partially
     */
    public ArrayList<Book> matchBook(String query){
        ArrayList<Book> matchingBooks = new ArrayList<>();
        query = query == null ? "" : query.toLowerCase();
        for (Book book : bookshelf) {
            String bookID = Integer.toString(book.getBookID()).toLowerCase();
            String title = book.getBookTitle().toLowerCase();
            String author = book.getBookAuthors(true).toLowerCase();
            if (bookID.contains(query) || title.contains(query) || author.contains(query)) {
                matchingBooks.add(book);
                System.out.println("\nBook Title: " + book.getBookTitle() + "\nBook ID: " + bookID);
            }
        }
        return matchingBooks;
    }
    
    /**
     * This function allows borrowing a book using the current date as a 
     * borrowing date
     * @param bookTitle title of book to be borrowed
     * @param memberForeName first name of member to take the loan
     * @param memberLastName last name of member taking the loan
     * @see Loan
     */
    public void borrowBook(String bookTitle, String memberForeName, String memberLastName){
        try {
            borrowBook(bookTitle, memberForeName, memberLastName, LocalDate.now());
        } catch (RuntimeException ex) {
            throw ex;
        }
    }
    
    /**
     * This function allows borrowing a book including a user defined borrowing 
     * date 
     * @param bookTitle title of book to be borrowed
     * @param memberForeName first name of member to take the loan
     * @param memberLastName last name of member taking the loan
     * @param borrowDate borrowing date
     * @see Loan
     */
    public void borrowBook(String bookTitle, String memberForeName, String memberLastName, LocalDate borrowDate){
        Member member = searchMember(memberForeName, memberLastName);
        if (member != null) {
            Book book = searchBook(bookTitle);
            if (book != null) {
                borrowBook(book, member.getID(), borrowDate);
            }else {
                System.out.println("No book found!");
            }
        } else {
            System.out.println("No member found!");
        }
    }
    
    /**
     * This function allows borrowing a book via a book Object and a member ID
     * @param book the book to be lent
     * @param memberID the member ID of the member to be lent to
     * @param borrowDate the date of the loan being taken out
     * @see Loan
     */
    public void borrowBook(Book book, int memberID, LocalDate borrowDate) {
        if (book != null) {
            int borrowed = MiscOperations.getBooksBorrowed(loanList, memberID);
            if (borrowed < 5) {
                if (book.getAvailable(loanList) > 0) {
                    int newID = loanList.get(loanList.size() - 1).getLoanID() + 1;
                    Loan loan = new Loan(newID, book.getBookID(), memberID, borrowDate);
                    loanList.add(loan);
                    MiscOperations.writeData(loanPath, loan.toString(), true);
                    System.out.println("\nBorrowed book successfully!");
                    loadData();
                } else {
                    System.out.println("\nThere are no copies of this book available at this time!");
                    if (usingGUI) throw new RuntimeException("There are no copies of this book available at this time!");
                }
            } else {
                System.out.println("\nYou are already loaning the maximum number of books!");
                if (usingGUI) throw new RuntimeException("You are already loaning the maximum number of books!");
            }
        } else {
            System.out.println("\nBook does not currently exist in the library!");
            if (usingGUI) throw new RuntimeException("Book does not currently exist in the library!");
        }
    }
    
    /**
     * This function allows to return a book using the loanID and will 
     * automatically trigger the fine payment if necessary
     * @param loanID unique Loan number
     */
    public void returnBook(int loanID) {
        int counter = 0;
        for (Loan loan : loanList) {
            if (loan.getLoanID() == loanID) {
                Book returnBook = searchBook(loan.getBookID());
                if (returnBook != null) {
                    if (loan.getFine() > 0) {
                        if (usingGUI) { if (!GUI.paidFine(loan)){ return; } }
                        else { if (!paidFine(loan)) { return; } }
                    }
                    loanList.remove(counter);
                    MiscOperations.writeData(loanPath, MiscOperations.listToString(new ArrayList<>(loanList)), false);
                    System.out.println("\nSuccessfully returned book!");
                    if (usingGUI) GUI.showMessage("Successfully returned book!");
                } else {
                    System.out.println("\nBook ID not recognised! Keep it!");
                }
                loadData();
                return;
            }
            counter++;
        }
        System.out.println("Loan ID not recognised!");
        if (usingGUI) throw new RuntimeException("Loan ID not recognised!");
    }
    
    /**
     * This function allows returning a book using a loan object,  and will
     * automatically trigger the fine payment if necessary
     * @param loan Loan object that is concerned
     */
    public void returnBook(Loan loan) {
        returnBook(loan.getLoanID());
    }

    /**
     * This function allows adding a new book to the library needing the 
     * following information:
     * book title, the name(s) of the author(s) , the publicationYear and the 
     * initial stock quantity
     * A new book object with a unique book ID will then be added to all 
     * relevant list.
     * @param title book title
     * @param authorNames a string array with the names of the authors
     * @param publishYear the year of publication for the book
     * @param quantity the number of books initially supplied to the library
     */
    public void addNewBook(String title, String[] authorNames, int publishYear, int quantity) {
        if (title.length() == 0) {
            System.out.println("\nBook must have a title!");
            if (usingGUI) throw new RuntimeException("Book must have a title!");
        } else if (authorNames.length == 0 || authorNames[0].equals("")) {
            System.out.println("\nThere cannot be no authors!");
            if (usingGUI) throw new RuntimeException("There cannot be no authors!");
        } else if (publishYear > LocalDate.now().getYear()) {
            System.out.println("\nBook must have been already been published!");
            if (usingGUI) throw new RuntimeException("Book must have been already been published!");
        } else if (quantity < 0) {
            System.out.println("\nBook quantity must be positive!");
            if (usingGUI) throw new RuntimeException("Book quantity must be positive!");
        } else {
            try {
                boolean existsAlready = searchBook(title) != null;
                if (existsAlready && usingGUI) {
                    if (GUI.cancelAddBook()) {
                        return;
                    }
                } else if (existsAlready && !usingGUI) {
                    if (this.cancelAddBook()) {
                        System.out.println("Cancelled book addition!" );
                        return;
                    }
                }
                int newID = bookshelf.get(bookshelf.size() - 1).getBookID() + 1;
                Book newBook = new Book(newID, title, authorNames, publishYear, quantity);
                String entry = newBook.toString();
                System.out.println("\nSuccessfully added new book:");
                System.out.println(newBook.formedString(loanList));
                MiscOperations.writeData(bookPath, entry, true);
                loadData();
            } catch (Exception ex) {
                System.out.println("\nError: Unable to access book list!");
                if (usingGUI) throw ex;
            }
        }
    }

    /**
     * This procedure removes a book from the library
     * @param bookID the unique book ID
     */
    public void removeBook(int bookID) {
        for (int i = 0; i < bookshelf.size(); i++) {
            if (bookshelf.get(i).getBookID() == bookID){
                bookshelf.remove(i);
                break;
            }
        }
        for (int i = 0; i < loanList.size(); i++) {
            if (loanList.get(i).getBookID() == bookID){
                loanList.remove(i);
                i--;
            }
        }
        MiscOperations.writeData(bookPath, MiscOperations.listToString(new ArrayList<>(bookshelf)), false);
        MiscOperations.writeData(loanPath, MiscOperations.listToString(new ArrayList<>(loanList)), false);
        loadData();

    }

    /**
     * This procedure removes a book from the library using a Book object
     * @param book Book to be deleted
     */
    public void removeBook(Book book) {
        removeBook(book.getBookID());
    }

    /**
     * This function changes the quantity of a stocked book, by adding the delta to the current book quantity
     * @param book a book object which will be altered
     * @param delta the change in book quantity
     */
    public void changeQuantity(Book book, int delta) {
        if (book != null) {
            changeQuantity(book.getBookID(), delta);
        }
    }

    /**
     * This function changes the quantity of a stocked book, by adding the delta
     * to the current book quantity
     * @param bookTitle a book title of the book which will be altered
     * @param delta the change in book quantity
     */
    public void changeQuantity(String bookTitle, int delta) {
        Book book = searchBook(bookTitle);
        changeQuantity(book, delta);
    }

    /**
     * This function changes the quantity of a stocked book, by adding the delta
     * to the current book quantity
     * @param bookID book ID of the book which will be altered
     * @param delta the change in book quantity
     */
    public void changeQuantity(int bookID, int delta) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                if (book.getAvailable(loanList) + delta >= 0) {
                    book.setQuantity(delta);
                    MiscOperations.writeData(bookPath, 
                            MiscOperations.listToString(
                                    new ArrayList<>(bookshelf)), false);
                    System.out.println("\nSucessfully changed quantity!");
                    System.out.println("\nNew quantity: " + book.getQuantityTotal() );
                } else {
                    System.out.println("\nBook quantity cannot be reduced to "
                            + "less than zero available books.");
                    if (usingGUI) throw new RuntimeException("Book quantity "
                            + "cannot be reduced to less than zero available books.");
                }
            }
        }
        loadData();
    }

    /**
     * This function will set a books quantity to a certain value newQuantity, 
     * if possible and this wouldnt cause less books to be in the library than 
     * are currently on loan.
     * @param book book to be altered
     * @param newQuantity amount of books to exist 
     */
    public void setQuantity(Book book, int newQuantity) {
        if (newQuantity - book.getQuantityTotal() + book.getAvailable(loanList) >= 0) {
            System.out.println(newQuantity - book.getQuantityTotal() + book.getAvailable(loanList));
            changeQuantity(book.getBookID(), newQuantity - book.getQuantityTotal() );
        } else {
            throw new RuntimeException("Available quantity cannot be reduced to less than zero");
        }
    }
    
    /**
     * Getter for the overall list of books in the library
     * @return List&ltBook&rt of all books in the library
     */
    public List<Book> getBookshelf(){
        return bookshelf;
    }

    //endregion

    //region Member Functions

    /**
     * This function allows searching for a member using it's memberID
     * @param userID a member ID to search for
     * @return a matching member object or null
     */
    public Member searchMember(int userID) {
        for (Member member : memberList) {
            if (member.getID() == userID){
                System.out.println("\nFound member:");
                System.out.println(member.formedString());
                showMemberLoans(member.getID());
                return member;
            }
        }
        return null;
    }

    /**
     * This function allows searching for a member using it's first and last 
     * name, and either returns an exact match or asks for a refining search.
     * @param foreName first name of the member
     * @param lastName last name of the member
     * @return a single member who fits the query
     */
    public Member searchMember(String foreName, String lastName) {
        for (Member member : memberList) {
            if (member.getForeName().toLowerCase().equals(foreName.toLowerCase())
                    && member.getLastName().toLowerCase().equals(lastName.toLowerCase())){
                System.out.println("\nFound member:");
                System.out.println(member.formedString());
                showMemberLoans(member.getID());
                return member;
            }
        }
        ArrayList<Member> listMembers = matchMember(foreName,  lastName);
        if (listMembers.size() == 0) {
            System.out.println("\nMember not found. Please refine your search!");
            if (usingGUI) throw new RuntimeException("Member not found!");
            return null;
        } else if (listMembers.size() == 1){
            try {
                System.out.println("\nNo exact match found. Is this the intended member? [Y] | [N]");
                boolean foundMember = MiscOperations.getInput().toLowerCase().equals("y");
                if (foundMember) {
                    return searchMember(listMembers.get(0).getID());
                }
            } catch (Exception ex) {
                System.out.println("\nInput error!");
            }
            return null;
        } else {
            try {
                System.out.println("\nPlease refine your search using the list above!");
                return searchMember();
            } catch (Exception ex) {
                System.out.println("\nInput error!");
                return null;
            }
        }
    }
    
    /**
     * This function allows searching for multiple members who partially fit a 
     * description of fore and last name
     * @param foreName first name of the member
     * @param lastName last name of the member
     * @return a list of matching members
     */
    public ArrayList<Member> matchMember(String foreName, String lastName) {
        System.out.println("\nPartial Matches");
        ArrayList<Member> matchingFirstNames = matchMember(foreName);
        ArrayList<Member> matchingLastNames = matchMember(lastName);
        for (Member currentMember : matchingLastNames) {
            boolean exists = false;
            for (Member otherMember : matchingFirstNames) {
                if (currentMember.getID() == otherMember.getID()) {
                    exists = true;
                }
            }
            if (!exists){
                matchingFirstNames.add(currentMember);
            }
        }
        for (Member member : matchingFirstNames) {
            System.out.println("\nMember Name: " + member.getFullName() + "\nMember ID: " + member.getID());
        }
        return matchingFirstNames;
    }
    
    /**
     * This function allows searching for multiple members who partially fit a 
     * description in the form of a string
     * @param query a search string
     * @return a list of matching members
     */
    public ArrayList<Member> matchMember(String query) {
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
    
    /**
     * This function allows adding a new member using his first and last name 
     * but also a LocalDate object to specify when the registration will be in 
     * effect.
     * @param foreName first name of the new member
     * @param lastName last name of the new member
     * @param regDate registration date as a LocalDate
     */
    public void addNewMember(String foreName, String lastName,  LocalDate regDate) {
        try {
            if (foreName.length() == 0 || lastName.length() == 0) {
                System.out.println("Name must not be blank!");
                if (usingGUI) throw new RuntimeException("Name must not be blank!");
                return;
            } else if ((foreName + lastName).matches("[0-9]+")) {
                System.out.println("Name must not contain numbers!");
                if (usingGUI) throw new RuntimeException("Name must not contain numbers!");
                return;
            }
            int newID = memberList.get(memberList.size() - 1).getID() + 1;
            Member member = new Member(newID, foreName, lastName, regDate);
            System.out.println("Successfully added new member:");
            System.out.println(member.formedString());
            MiscOperations.writeData(memberPath, member.toString(), true);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    /**
     * Getter for the overall list of loans in the library
     * @return list of loans in the library
     */
    public List<Loan> getLoanList(){
        return loanList;
    }
    
    /**
     * This function returns a list of all the loan objects taken out by a 
     * single member with a certain member ID.
     * @param memberID member ID to be evaluated
     * @return a list of Loan Objects taken out by that member
     */
    public List<Loan> getMemberLoanList(int memberID){
        ArrayList<Loan> memberLoans = new ArrayList<>();
        for (Loan loan : loanList) {
            if (loan.getMemberID() == memberID) {
                memberLoans.add(loan);
            }
        }
        return memberLoans;
    }
    
    /**
     * A console function which prints all loans taken out by a member having 
     * the memberID.
     * @param memberID member ID to be evaluated. 
     */
    public void showMemberLoans(int memberID){
        System.out.println("\nMember's Loans:");
        boolean hasLoans = false;
        for (Loan loan :loanList) {
            if (loan.getMemberID() == memberID){
                hasLoans = true;
                System.out.println("\n" + loan.formedString());
            }
        }
        if (!hasLoans) {
            System.out.println("Member has no loans!");
        }

    }
    
    /**
     * This function allows to renew a loan and to thereby reset the 30 day 
     * return policy.
     * @param loan Loan to be renewed
     */
    public void renewLoan(Loan loan){
        for (int i = 0; i < loanList.size(); i++) {
            if (loanList.get(i).getLoanID() == loan.getLoanID()) {
                loanList.set(i, new Loan(loan.getLoanID(), loan.getBookID(), loan.getMemberID(), LocalDate.now()));
                MiscOperations.writeData(loanPath, MiscOperations.listToString(new ArrayList<>(loanList)), false);
            }
        }
    }

    //endregion

    //region Misc
    /**
     * This function allows the user to pay his fine by either card or cash
     * @param loan the book loan to be returned and to be resolved
     * @param method whether to pay cash or card
     */
    public void payFine(Loan loan, int method) {
        double fine = loan.getFine();
        if (method == this.METHOD_CASH) {
            try {
                System.out.println("------- Cash Payment System -------");
                System.out.println("Opened coin acceptance slot");
                System.out.println("Calculating entered amount");
                System.out.println("Waiting for entered amount >= fine");
                System.out.println("Giving necessary change");
                System.out.println("Closed coin acceptance slot");
                System.out.println("-----------------------------------");
            } catch (Exception ex) {
                System.out.println("Payment machine is broken!");
                if (usingGUI) throw new RuntimeException("Payment machine is broken!");
            }
        } else if (method == this.METHOD_CARD) {
            try {
                System.out.println("------ Bank Transfer System ------");
                System.out.println("Established contact with bank");
                System.out.println("Requested funds transfer");
                System.out.println("Confirmed funds have transferred");
                System.out.println("Closed connection to bank");
                System.out.println("----------------------------------");
            } catch (Exception ex) {
                System.out.println("Unable to contact bank!");
                if (usingGUI) throw new RuntimeException("Unable to contact bank!");
            }
        }
    }
    
    /**
     * A simple function printing out all the books' informations in the library 
     * onto the console. 
     */
    public void showAllBooks() {
        System.out.println("All Books:");
        for (Book book : bookshelf) {
            System.out.println(book.toString());
        }
        System.out.println("");
    }
    
    /**
     * A simple function printing out all the members' informations in the library 
     * onto the console. 
     */
    public void showAllMembers() {
        System.out.println("All Members:");
        for (Member member : memberList) {
            System.out.println(member.toString());
        }
        System.out.println("");
    }
    
    /**
     * A simple function printing out all the loans' informations in the library 
     * onto the console. 
     */
    public void showAllBookLoans() {
        System.out.println("All Loans:");
        for (Loan loan : loanList) {
            System.out.println(loan.toString());
        }
        System.out.println("");
    }
    
    /**
     * A console use function to let the user decide whether to overwrite or 
     * keep an existing book
     * @return whether the book is overwritten or not.
     */
    private boolean cancelAddBook(){
        System.out.println("\nBook already exists! Continue anyway? [Y] | [N]");
        try {
            String query = MiscOperations.getInput().toLowerCase();
            return !query.equals("y");
        } catch (InputException ex) {
            return true;
        }
    }
    
    /**
     * This function takes a Loan object to let the user know about the fine 
     * and how to pay it.
     * @param loan the loan for which the fine will be calculated
     * @return whether the fine was paid now or not.
     */
    public boolean paidFine(Loan loan){
        System.out.println("You have an outstanding fine of: " + MiscOperations.fineToString(loan.getFine()) + "\nPay now? [Y] | [N]");
        try {
            String query = MiscOperations.getInput().toLowerCase();
            if(query.equals("y")){
                System.out.println("How would you like to pay the fine? Cash[1] | Card[2]");
                String method = MiscOperations.getInput().toLowerCase();
                payFine(loan, method.equals("1") ? METHOD_CASH : METHOD_CARD);
                return true;
            }
            System.out.println("Cannot continue until the fine is paid!");
            return false;
        } catch (InputException ex) {
            System.out.println("Error: Could not complete fining process!\nReason: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * This function saves the programs data (books, members, loans) into the 
     * three locations stated as parameters
     * @param bookPath location for book information to be saved
     * @param memberPath location for member information to be saved
     * @param loanPath  location for loan information to be saved
     */
    public void saveChanges(String bookPath, String memberPath, String loanPath) {
        MiscOperations.writeData(bookPath, MiscOperations.listToString(new ArrayList<>(bookshelf)), false);
        MiscOperations.writeData(memberPath, MiscOperations.listToString(new ArrayList<>(memberList)), false);
        MiscOperations.writeData(loanPath, MiscOperations.listToString(new ArrayList<>(loanList)), false);
    }
    
    /**
     * This procedure saves the data (book, loan and member information) to the 
     * initally given files
     */
    public void saveChanges() {
        saveChanges(bookPath, memberPath, loanPath);
    }
    
    /**
     * This functions loads data from the files given in the constructor to 
     * fill the library's internal files
     */
    public void loadData() {
        bookshelf.clear();
        memberList.clear();
        loanList.clear();
        bookshelf = MiscOperations.initBooks(bookPath);
        memberList = MiscOperations.initMembers(memberPath);
        loanList = MiscOperations.initLoans(loanPath);
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

    //endregion

    //region Keyboard Input Overloads
    /**
     * The console overload for the function borrowBook, allowing the console 
     * user to borrow a book.
     */
    public void borrowBook() {
        try {
            String bookTitle = MiscOperations.getInput("\nBook Title/ID:");
            String memberFirstName = MiscOperations.getInput("Member First Name:");
            String memberLastName = MiscOperations.getInput("Member Last Name:");
            borrowBook(bookTitle, memberFirstName, memberLastName);
        } catch (InputException ex){
         System.out.println("Error");
        }
    }

    /**
     * The console overload of the searchBook function allowing a console user 
     * to search for a book
     * @return a single book that fullfills the internally entered conditions 
     */
    public Book searchBook() {
        try {
            String query = MiscOperations.getInput("Book Title/ID:");
            return searchBook(query);
        } catch (InputException ex) {
            System.out.println("Error");
            System.out.println(ex.getClass());
            return null;
        }
    }
    
    /**
     * The console overload of the searchMember function allowing a console user 
     * to search for a member
     * @return a single member that fullfills the internally entered conditions
     */
    public Member searchMember() {
        try {
            String memberFirstName = MiscOperations.getInput("\nFirst Name:");
            String memberLastName = MiscOperations.getInput("Last Name:");
            return searchMember(memberFirstName, memberLastName);
        } catch (InputException ex){
            System.out.println("Error");
            return null;
        }
    }
    
    /**
     * The console overload of the returnBook function allowing a console user 
     * to return a book
     */
    public void returnBook() {
        try {
            String loanID = MiscOperations.getInput("\nLoan ID:");
            returnBook(Integer.parseInt(loanID));
        } catch (InputException ex){
            System.out.println("Error");
        }
    }
    
    /**
     * The console overload of the addNewBook function allowing a console user 
     * to add a book  to the library
     */
    public void addNewBook() {
        try {
            String bookTitle = MiscOperations.getInput("\nBooks Title:");
            String rawAuthors = MiscOperations.getInput("Please enter author names [comma separated]");
            String[] allAuthors = rawAuthors.split(",");
            for (int i = 0; i < allAuthors.length; i++) {
                allAuthors[i] = allAuthors[i].trim();
            }
            int year = Integer.parseInt(MiscOperations.getInput("Publication Year:"));
            int quantity = Integer.parseInt(MiscOperations.getInput("Quantity:"));
            addNewBook(bookTitle, allAuthors, year, quantity);
        } catch (InputException ex){
            System.out.println("Error");
        }
    }
    
    /**
     * The console overload of the addNewMember function allowing a console user 
     * to add a new member
     */
    public void addNewMember() {
        try {
            String firstName = MiscOperations.getInput("\nFirst Name:");
            String lastName = MiscOperations.getInput("Last Name:");
            String regDate = MiscOperations.getInput("Registration Date [yyyy-mm-dd] [Leave blank for today]");
            LocalDate date = LocalDate.now();
            if (regDate != "") {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(regDate, formatter);
            }
            addNewMember(firstName, lastName, date);
        } catch (Exception ex) {
            System.out.println("Error");
        }
    }
    /**
     * The console overload of the change quantity function allowing a console user 
     * to change the total stock quantity of a book
     */
    public void changeQuantity() {
        try {
            String query = MiscOperations.getInput("\nBook Title/ID:");
            Book book = searchBook(query);
            boolean resetQuantity = MiscOperations.getInput("\bSet quantity or increase/decrease quantity? [S] | [I]").toLowerCase().equals("s");
            int quantity = Integer.parseInt(MiscOperations.getInput("\nQuantity:"));
            if (resetQuantity) {
                setQuantity(book, quantity);
            } else {
                changeQuantity(book, quantity);
            }

        } catch (Exception ex) {
            System.out.println("Error taking input. Please try again!");
        }
    }

    //endregion
    /**
     * the main function for starting the GUI use.
     */
    public void showGUI(){
        try {
            UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[1].getClassName());
        } catch (Exception ex) {
            System.out.println("Could not load theme!");
        }
        usingGUI = true;
        GUI = new Main(this);
        GUI.showLogin();
    }

}
