package library;

import jdk.jshell.execution.LocalExecutionControl;

import javax.print.attribute.IntegerSyntax;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Library {

    private Main GUI;

    private List<Book> bookshelf = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private List<Loan> loanList = new ArrayList<>();
    private HashMap<Book, Integer> BookLoanQuant = new HashMap<>();

    private String bookPath;
    private String memberPath;
    private String loanPath;

    private int METHOD_CASH = 0;
    private int METHOD_CARD = 1;

    private boolean usingGUI = false;

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

    public Book searchBook(int bookID) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                System.out.println("\nBook Information:\n" + book.formedString(loanList));
                return book;
            }
        }
        return null;
    }

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
        if (matchingBooks.size() == 0) {
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
            } catch (Exception ex) {
                System.out.println("\nInput error!");
            }
            return null;
        } else {
            try {
                System.out.println("\nPlease refine your search using the list above!");
                return searchBook();
            } catch (Exception ex) {
                System.out.println("\nInput error!");
                return null;
            }
        }
    }

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

    public void borrowBook(String bookTitle, String memberForeName, String memberLastName){
        try {
            borrowBook(bookTitle, memberForeName, memberLastName, LocalDate.now());
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

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

    public void returnBook(Loan loan) {
        returnBook(loan.getLoanID());
    }

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

    public void removeBook(Book book) {
        removeBook(book.getBookID());
    }

    public void changeQuantity(Book book, int delta) {
        if (book != null) {
            changeQuantity(book.getBookID(), delta);
        }
    }

    public void changeQuantity(String bookTitle, int delta) {
        Book book = searchBook(bookTitle);
        changeQuantity(book, delta);
    }

    public void changeQuantity(int bookID, int delta) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                if (book.getAvailable(loanList) + delta >= 0) {
                    book.setQuantity(delta);
                    MiscOperations.writeData(bookPath, MiscOperations.listToString(new ArrayList<>(bookshelf)), false);
                    System.out.println("\nSucessfully changed quantity!");
                    System.out.println("\nNew quantity: " + book.getQuantityTotal() );
                } else {
                    System.out.println("\nBook quantity cannot be reduced to less than zero");
                    if (usingGUI) throw new RuntimeException("Book quantity cannot be reduced to less than zero");
                }
            }
        }
        loadData();
    }

    public void setQuantity(Book book, int newQuantity) {
        if (newQuantity - book.getQuantityTotal() + book.getAvailable(loanList) >= 0) {
            System.out.println(newQuantity - book.getQuantityTotal() + book.getAvailable(loanList));
            changeQuantity(book.getBookID(), newQuantity - book.getQuantityTotal() );
        } else {
            throw new RuntimeException("Available quantity cannot be reduced to less than zero");
        }
    }

    public List<Book> getBookshelf(){
        return bookshelf;
    }

    //endregion

    //region Member Functions

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

    public void showAllBooks() {
        System.out.println("All Books:");
        for (Book book : bookshelf) {
            System.out.println(book.toString());
        }
        System.out.println("");
    }

    public void showAllMembers() {
        System.out.println("All Members:");
        for (Member member : memberList) {
            System.out.println(member.toString());
        }
        System.out.println("");
    }

    public void showAllBookLoans() {
        System.out.println("All Loans:");
        for (Loan loan : loanList) {
            System.out.println(loan.toString());
        }
        System.out.println("");
    }

    private boolean cancelAddBook(){
        System.out.println("\nBook already exists! Continue anyway? [Y] | [N]");
        try {
            String query = MiscOperations.getInput().toLowerCase();
            return !query.equals("y");
        } catch (InputException ex) {
            return true;
        }
    }

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

    public void saveChanges(String bookPath, String memberPath, String loanPath) {
        MiscOperations.writeData(bookPath, MiscOperations.listToString(new ArrayList<>(bookshelf)), false);
        MiscOperations.writeData(memberPath, MiscOperations.listToString(new ArrayList<>(memberList)), false);
        MiscOperations.writeData(loanPath, MiscOperations.listToString(new ArrayList<>(loanList)), false);
    }

    public void saveChanges() {
        saveChanges(bookPath, memberPath, loanPath);
    }

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

    public void returnBook() {
        try {
            String loanID = MiscOperations.getInput("\nLoan ID:");
            returnBook(Integer.parseInt(loanID));
        } catch (InputException ex){
            System.out.println("Error");
        }
    }

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
