package library;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    
    private Book selectedBook = null;
    private Member selectedMember = null;

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

    //region Book Functions
    /**
     * 
     * @param query
     * @return 
     */
    public ArrayList<Book> searchBook(String query) {
        return searchBook(query, (bookshelf));
    }
    /**
     * 
     * @param bookID
     * @return 
     */
    public Book searchBook(int bookID) {
        for (Book book : bookshelf) {
            if (book.getBookID() == bookID) {
                System.out.println("\nFound book:");
                System.out.println(book.toString(loanList));
                return book;
            }
        }
        return null;
    }
    /**
     * 
     * @param query
     * @param library
     * @return 
     */
    public ArrayList<Book> searchBook(String query, List<Book> library){
        ArrayList<Book> matchingBooks = new ArrayList<>();
        query = query == null ? "" : query.toLowerCase();
        //search
        for (Book book : library) {
            String bookID = Integer.toString(book.getBookID()).toLowerCase();
            String title = book.getBookTitle().toLowerCase();
            String author = book.getBookAuthors(true).toLowerCase();
            if (bookID.contains(query) || title.contains(query) || author.contains(query)) {
                matchingBooks.add(book);
            }
        }   
        //output
        if (matchingBooks.size() == 1) {
            Book result = matchingBooks.remove(0);                              // if only one matching book was found
            System.out.println("Your search result is:");
            System.out.println(result.toString(this.getLoanList()));
            System.out.println("\nDo you want to select that book?[Y/N]");
            String input = "";
            try {
                input = MiscOperations.getInput();
            } catch (InputException e) {
                System.out.println(e.getMessage());
                return matchingBooks;
            }
            if (input.charAt(0) == 'Y' || input.charAt(0) == 'y') {
                this.selectedBook = result;
                System.out.println("The book was selected.");
            } else {
                System.out.println("The book was not selected. You will return "
                        + "to the main menu.");
            }
        } else if (matchingBooks.size() > 1) {                                  //multiple matches
            System.out.println("Your search results are:");
            for (Book book : matchingBooks) {
                System.out.println(book.toString());
            }
            System.out.println("Do you want to refine your search? [Y/N]");
            try {
                String input = MiscOperations.getInput();
                if (input.charAt(0) == 'Y' || input.charAt(0) == 'y') {
                    refineSearchBook(matchingBooks);
                } else {
                    System.out.println("No further search initiated, "
                            + "you will be redirected to the main menu.");
                }
            } catch (InputException e) {
                System.out.println("An error occured with your input.");
                return matchingBooks;
            }
        } else {                                                                //no matches
            System.out.println("There were no books found matching your query.");
            System.out.println("You will be redirected to the main menu.");
        }
        return matchingBooks;
    }
    /**
     * 
     * @param title
     * @return 
     */
    public Book searchBookExact(String title) {
        title = title == null ? "" : title.toLowerCase();
        if (title != null) {
            for (Book book : bookshelf) {
                if (book.getBookTitle().toLowerCase().equals(title)) {
                    return book;
                }
            }
        }
        return null;
    }
    /**
     * 
     * @param bookTitle
     * @param memberForeName
     * @param memberLastName 
     */
    public void borrowBook(String bookTitle, String memberForeName, String memberLastName){
        try {
            borrowBook(bookTitle, memberForeName, memberLastName, LocalDate.now());
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    public void borrowBook(String bookTitle, String memberForeName, String memberLastName, LocalDate borrowDate){
        ArrayList<Member> listMember = searchMember(memberForeName,  memberLastName);
        if (listMember.size() == 1) {
            Member member = listMember.get(0);
            if (!member.getForeName().equals(memberForeName) || !member.getLastName().equals(memberLastName) && !usingGUI) {
                System.out.println("\nClosest member match: " + member.getFullName() +". Is this the intended member? [Y] | [N]");
                try {
                    boolean intendedMember = MiscOperations.getInput().toLowerCase().equals("y");
                    if (!intendedMember) {
                        System.out.println("\nPlease refine your search!");
                        return;
                    }
                } catch (InputException ex) {
                    System.out.println("\nInput error!");
                    return;
                }
            }
            ArrayList<Book> books = searchBook(bookTitle);
            if (books.size() == 0) {
                System.out.println("\nNo book found. Please refine your search!");
                if (usingGUI) throw new RuntimeException("No book found!");
            } else if (books.size() > 1) {
                if (usingGUI) throw new RuntimeException("Multiple books found. Please be more specific!");
                else {
                    try {
                        System.out.println("\nMultiple books found. Please enter the intended book ID from the list above!");
                        int bookID = Integer.parseInt(MiscOperations.getInput());
                        borrowBook(bookID, member.getID(), borrowDate);
                    } catch (InputException ex) {
                        System.out.println("\nInput error!");
                        return;
                    }
                }
            } else {
                System.out.println("\nClosest book match: " + books.get(0).getBookTitle() +". Is this the intended book? [Y] | [N]");
                try {
                    boolean intendedBook = MiscOperations.getInput().toLowerCase().equals("y");
                    if (intendedBook) {
                        borrowBook(books.get(0).getBookID(), member.getID(), borrowDate);
                    }
                } catch (InputException ex) {
                    System.out.println("\nInput error!");
                    return;
                }
            }
        } else if (listMember.size() == 0){
            System.out.println("\nMember not found. Please refine your search!");
            if (usingGUI) throw new RuntimeException("Member not found!");
        } else if (listMember.size() > 1) {
            System.out.println("\nMultiple members found. Please be more specific!");
            if (usingGUI) throw new RuntimeException("Multiple members found. Please be more specific!");
        }

    }

    public void borrowBook(int bookID, int memberID, LocalDate borrowDate) {
        Book book = searchBook(bookID);
        if (book != null) {
            int borrowed = MiscOperations.getBooksBorrowed(loanList, memberID);
            if (borrowed < 5) {
                if (book.getAvailable(loanList) > 0) {
                    int newID = loanList.get(loanList.size() - 1).getLoanID() + 1;
                    Loan loan = new Loan(newID, bookID, memberID, borrowDate);
                    loanList.add(loan);
                    MiscOperations.writeData(loanPath, loan.toString(), true);
                    loadData();
                    System.out.println("\nBorrowed book successfully!");
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

    public void borrowBook(Book book, Member member){
        borrowBook(book.getBookID(), member.getID(), LocalDate.now());
    }

    public void returnBook(int loanID) {
        int counter = 0;
        for (Loan loan : loanList) {
            if (loan.getLoanID() == loanID) {
                Book returnBook = searchBook(loan.getBookID());
                if (returnBook != null) {
                    if (loan.getFine() > 0) {
                        if (usingGUI) { 
                            if (!GUI.paidFine(loan)){
                                return; 
                            } 
                        }
                        else { 
                            if (!paidFine(loan)) {
                                return; 
                            } 
                        }
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
                boolean existsAlready = searchBookExact(title) != null;
                if (existsAlready && usingGUI) {
                    if (GUI.cancelAddBook()) {
                        System.out.println("Cancelling book addition!" );
                        return;
                    }
                } else if (existsAlready && !usingGUI) {
                    if (this.cancelAddBook()) {
                        System.out.println("Cancelling book addition!" );
                        return;
                    }
                }
                int newID = bookshelf.get(bookshelf.size() - 1).getBookID() + 1;
                Book newBook = new Book(newID, title, authorNames, publishYear, quantity);
                String entry = newBook.toString();
                System.out.println("\nSuccessfully added new book:");
                System.out.println(newBook.toString(loanList));
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
        } else {
            System.out.println("Cannot change quantity: book does not exist!");
            if (usingGUI) throw new RuntimeException("Cannot change quantity: book does not exist!");
        }
    }

    public void changeQuantity(String bookTitle, int delta) {
        List<Book> books = searchBook(bookTitle);
        if (books.size() > 1) {
            System.out.println("Multiple books found. Please be more specific!");
            if (usingGUI) throw new RuntimeException("Multiple books found. Please be more specific!");
        } else if (books.size() == 0) {
            System.out.println("No book found. Please refine your search!");
            if (usingGUI) throw new RuntimeException("No book found. Please refine your search!");
        } else {
            changeQuantity(books.get(0), delta);
        }
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
                    System.out.println("\nBook quantity cannot be reduced to less than zero or to less books than currently available.");
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

    public int getAvailableCopies(Book query){
        return (BookLoanQuant.get(query) == null)? query.getQuantityTotal() : query.getAvailable(loanList);
    }

    public List<Book> getBookshelf(){
        return bookshelf;
    }



    //endregion

    //region Member Functions


    public Member searchMemberExact(String foreName, String lastName){
        for (Member member : memberList) {
            if (member.getForeName().equals(foreName) && member.getLastName().equals(lastName)){
                return member;
            }
        }
        return null;
    }

    public ArrayList<Member> searchMember(String foreName, String lastName) {
        System.out.println("\nMatching members:");
        ArrayList<Member> matchingLastNames = searchMember(lastName);
        ArrayList<Member> matchingNames = new ArrayList<>(searchMember(foreName));
        matchingNames.addAll(matchingLastNames);
        return matchingNames;
    }

    public ArrayList<Member> searchMember(String query) {
        return searchMember(query, (ArrayList)memberList);
    }

    public Member searchMember(int userID) {
        System.out.println("Searching using Member ID.");
        for (Member member : memberList) {
            if (member.getID() == userID){
                System.out.println("\nFound member:");
                System.out.println(member.formedString());
                outputMemberLoans(userID);
                return member;
            }
        }
        return null;
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

    public void outputMemberLoans(int memberID){
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

    //endregion

    //region KeyboardInputOverloads

    public void searchBook() {
        System.out.println("You are now in the book search tool, please enter a"
                + " book ID, a title or an author.");
        String query = " ";
        try {
            query = MiscOperations.getInput();
        } catch (InputException e) {
                System.out.println(e.getMessage());
            return;
        }
        searchBook(query);
    }

    public void refineSearchBook(ArrayList<Book> formerResults){
        System.out.println("Please give an additional search statement.");
        String refineInput = "";
        try{
            refineInput = MiscOperations.getInput();
        }
        catch (InputException e){
            System.out.println("An error occured with your input.");
            return;
        }
        searchBook(refineInput, formerResults);
    }

    public void borrowBook() {
        System.out.println("You are now in the book borrow tool.");
        System.out.println("To borrow a book, you need to select a book and a "
                + "member who borrows the book.");
        boolean bookSelected = false;
        boolean memberSelected = false;
        while(!bookSelected){
            if(selectedBook != null){
                System.out.println("Do want to use the previously selected Book?");
                selectedBook.toString(loanList);
                System.out.println("[Y]/[N]");
                try{
                    String answer = MiscOperations.getInput();
                    if( answer.matches("y|Y") ){
                        bookSelected = true;
                    }
                }
                catch(InputException e){
                    System.out.println(e.getMessage());
                }
            }
            else{
                System.out.println("You need to select a book, therefore you will "
                       + "be redirected to the book search tool. You will return "
                       + "automatically once you successfully selected a book.");
                searchBook();
            }
        }
        while(!memberSelected){
            if(selectedMember != null){
                System.out.println("Do want to use the previously selected Member?");
                selectedMember.formedString();
                System.out.println("[Y]/[N]");
                try{
                    String answer = MiscOperations.getInput();
                    if( answer.matches("y|Y") ){
                        memberSelected = true;
                    }
                }
                catch(InputException e){
                    System.out.println(e.getMessage());
                }
            }
            else{
                System.out.println("You need to select a member, therefore you will "
                       + "be redirected to the member search tool. You will return "
                       + "automatically once you successfully selected a book.");
                searchMember();
            }
        }
        borrowBook(selectedBook,selectedMember);
                
    }
    
    public void searchMember() {
        System.out.println("You are now in the member search tool.");
        System.out.println("Please enter the last name of your wanted member."
                + "\nIf you don't know it then leave it blank and press enter");
        String inputLastName = "";
        try{
            inputLastName = MiscOperations.getInput();
        }
        catch (InputException e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Please enter now the first name of your wanted member.");
        String inputFirstName = "";
        try{
            inputFirstName = MiscOperations.getInput();
        }
        catch (InputException e){
            System.out.println(e.getMessage());
            return;           
        }
        ArrayList<Member> results = searchMember(inputFirstName, inputLastName);
        if(results.isEmpty())
            System.out.println("Your search returned no results");
        

    }

    public void returnBook() {
        System.out.println("You are in the book return tool.");
        System.out.println("Please enter the Loan ID (a six digit number "
                + "starting with 3).");
        int loanID = -1;
        try{
            loanID = Integer.parseInt(MiscOperations.getInput());
            returnBook(loanID);
        }
        catch(InputException e){
            System.out.println(e.getMessage());
            return;
        }
        catch(NumberFormatException e){
            System.out.println("The input could not be converted into a number");
        }
    }

    public void addNewBook() {
        
    }

    public void addNewMember() {
        System.out.println("You are in the member registration tool.");
        System.out.println("Please enter the first name of the new member.");
        try{
            String firstName = MiscOperations.getInput();
            System.out.println("Please enter the last name of the new Member");
            String lastName = MiscOperations.getInput();
            LocalDate registrationDate = LocalDate.now();
            System.out.println("Do you want the registration to be put through "
                    + "today or do you want the account to be activated in the "
                    + "future? [1]/[2]");
            String answer = MiscOperations.getInput();
            if( answer.matches("2") ){
                System.out.println("Please enter a date on which the activation "
                        + "should become active in the form dd-mm-yyyy.");
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd-mm-yyyy");
                registrationDate = LocalDate.parse(MiscOperations.getInput(),inputFormat);   
            }
            addNewMember(firstName,lastName, registrationDate);
        }
        catch (InputException e){
            System.out.println(e.getMessage());
            return;
        }
        catch (DateTimeParseException e){
            System.out.println("Your entered date is of an invalid form.");
        }
        
    }

    public void changeQuantity() {
        System.out.println("You are in the book stock change tool.");
        boolean bookSelected = false;
        while(!bookSelected){
            if(selectedBook != null){
                System.out.println("Do want to use the previously selected Book?");
                selectedBook.toString(loanList);
                System.out.println("[Y]/[N]");
                try{
                    String answer = MiscOperations.getInput();
                    if( answer.matches("y|Y") ){
                        bookSelected = true;
                        System.out.println("Please state now how many books you wish"
                            + " to add or deduct from the stock (as positive or "
                            + "negative number).");
                        int delta = Integer.parseInt(MiscOperations.getInput());
                        changeQuantity(selectedBook, delta);
                    }
                }
                catch(InputException e){
                    System.out.println(e.getMessage());
                }
                catch(NumberFormatException e){
                    System.out.println("Your input could not be converted into a number.");
                }
            }
            else{
                System.out.println("You need to select a book, therefore you will "
                       + "be redirected to the book search tool. You will return "
                       + "automatically once you successfully selected a book.");
                searchBook();
            }
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

    public void refineSearchMember(ArrayList<Member> formerResults) {
        System.out.println("Please give an additional search statement.");
        String refineInput = "";
        try{
            refineInput = MiscOperations.getInput();
        }
        catch (InputException e){
            System.out.println("An error occured with your input.");
            return;
        }
        searchMember(refineInput, formerResults);
    }

    public ArrayList<Member> searchMember(String query, ArrayList<Member> memberCollection) {
        ArrayList<Member> members = new ArrayList<>();
        query = query == null ? "" : query.toLowerCase();
        for (Member member : memberCollection) {
            String fullName = member.getFullName().toLowerCase();
            String memberID = Integer.toString(member.getID()).toLowerCase();
            if (fullName.contains(query) || memberID.contains(query)) {
                members.add(member);
            }
        }
        if(members.size()==1){
            System.out.println("One matching member was found:");
            System.out.println(members.get(0).formedString());
            System.out.println("\nDo you want to select that Member?[Y/N]");
            try {
                String input = MiscOperations.getInput();
                if (input.charAt(0) == 'Y' || input.charAt(0) == 'y') {
                this.selectedMember = members.get(0);
                System.out.println("The member was selected.");
                } 
                else {
                System.out.println("The member was not selected. You will return "
                        + "to the main menu.");
                }
            } catch (InputException e) {
                System.out.println(e.getMessage());
                return members;
            }
        }
        else if(members.size()>1){
            System.out.println("Your search results are:");
            for (Member member : members) {
                System.out.println(member.formedString());
            }
            System.out.println("Do you want to refine your search? [Y/N]");
            try {
                String input = MiscOperations.getInput();
                if (input.charAt(0) == 'Y' || input.charAt(0) == 'y') {
                    refineSearchMember(members);
                } else {
                    System.out.println("No further search initiated, "
                            + "you will be redirected to the main menu.");
                }
            } catch (InputException e) {
                System.out.println("An error occured with your input.");
            }
        }
        else{
            System.out.println("No members matching your query could be found.");
        }
        return members;
    }


}
