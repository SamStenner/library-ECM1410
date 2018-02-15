package library;

import java.time.LocalDate;

public class Library {

    public Library() {

    }

    public Library(String bookData, String memberData, String bookLoanData) {

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

    //endregion

    //region Member Functions

    public void searchMember(String lastName, String foreName) {

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
