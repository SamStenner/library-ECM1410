package library;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This class combines all functions that will be used throughout the use of the
 * library class whilst not being able to be in the library class itself.
 * @author Sam Stenner, Max Prüstel
 * @version 1.1
 */
public class MiscOperations {
    /**
     * This function takes a file name/ file path and reads the file's data, 
     * outputting it linewise in a List 
     * @param fileName String, file name or file path of the file to be read
     * @return List&ltString&rt with the file lines as elements
     * @throws IOException due to reading a file
     */    
    public static List<String> readFile(String fileName) throws IOException{
        ArrayList<String> fileData = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        return fileData;
    }

    /**
     * takes a file name/ path of a file containing member information in the form:
     * memberID, firstName, LastName, registrationDate 
     * for limitations on these please refer to Member. The return is a 
     * List&ltMember&rt containing all members and their respective information 
     * in the file
     * @see Member
     * @param filename file name / file path pf of the input data
     * @return List with Members
     */
    public static List<Member> initMembers(String filename) {
        ArrayList<Member> memberList = new ArrayList<>();
        try {
            List<String> data = readFile(filename);
            for (String line : data) {
                String[] memberProps = line.split(",");
                int memberID = Integer.parseInt(memberProps[0]);
                String memberFirstName = memberProps[1];
                String memberLastName = memberProps[2];
                LocalDate registerDate = LocalDate.parse(memberProps[3]);
                Member member = new Member(memberID, memberFirstName, memberLastName, registerDate);
                memberList.add(member);
            }
        } catch (IOException ex) {
            System.out.println("Error: Could not load members: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("Error: Member ID incorrectly formatted!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Error: Missing member data!");
        } catch (DateTimeException ex) {
            System.out.println("Error: Member join date invalid!");
        } finally {
            return memberList;
        }
    }
    
    /**
     * takes a file name/ path of a file containing book information in the form:
     * bookID, title, author(s) (seperated by : ), year of publication, total quantity 
     * for limitations on these please refer to Book. The return is a 
     * List&ltBook&rt containing all books and their respective information 
     * in the file
     * @see Book
     * @param filename file name / file path pf of the input data
     * @return List with Books
     */
    public static List<Book> initBooks(String filename) {
        ArrayList<Book> bookList = new ArrayList<>();
        try {
            List<String> data = readFile(filename);
            for (String line : data) {
                String[] bookProps = line.split(",");
                int ID = Integer.parseInt(bookProps[0]);
                String title = bookProps[1];
                String[] authors = bookProps[2].split(":");
                int year = Integer.parseInt(bookProps[3]);
                int quantity = Integer.parseInt(bookProps[4]);
                Book book = new Book(ID, title, authors, year, quantity);
                bookList.add(book);
            }
        } catch (IOException ex) {
            System.out.println("Error: Could not load books: " + ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Error: Missing book data!");
        } catch (NumberFormatException ex) {
            System.out.println("Error: Book data incorrectly formatted!");
        } finally {
            return bookList;
        }
    }
    
    /**
     * takes a file name/ path of a file containing loan information in the form:
     * loanId, bookID, memberID, LoanDate (yyyy-mm-dd), total quantity 
     * for limitations on these please refer to Loan. The return is a 
     * List&ltLoan&rt containing all loans and their respective information 
     * in the file
     * @see Loan
     * @see Member
     * @see Book
     * @param filename file name / file path pf of the input data
     * @return List with Loans
     */
    public static List<Loan> initLoans(String filename) {
        ArrayList<Loan> loanList = new ArrayList<>();
        try {
            List<String> data = readFile(filename);
            for (String line : data) {
                String[] loanProps = line.split(",");
                int loanID = Integer.parseInt(loanProps[0]);
                int bookID = Integer.parseInt(loanProps[1]);
                int memberID = Integer.parseInt(loanProps[2]);
                LocalDate loanDate = LocalDate.parse(loanProps[3]);
                Loan loan = new Loan(loanID, bookID, memberID, loanDate);
                loanList.add(loan);
            }
        } catch (IOException ex) {
            System.out.println("Error: Could not load loans: " + ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Error: Missing loan data!");
        } catch (NumberFormatException ex) {
            System.out.println("Error: Loan data incorrectly formatted!");
        }
        finally {
            return loanList;
        }
    }
 
    /**
     * this function writes data to a designated file
     * @param fileData the file name / path to write to.
     * @param text the data to write
     * @param append whether to append the file or not.
     */
    public static void writeData(String fileData, String text, boolean append){
        try {
            text = text.trim();
            Writer output = new BufferedWriter(new FileWriter(fileData, append));
            output.append(append ? "\n" + text: text);
            output.close();
        } catch (Exception ex) {
            System.out.println("Could not find data files!");
        }
    }
    
    /**
     * This function asks the user for input
     * @param message a specialized message to be prompted to the user
     * @return the user input
     * @throws InputException gets thrown if the scanner and input create an error. 
     */
    public static String getInput(String message) throws InputException{

        if (message != null) {
            System.out.println(message);
        }
        Scanner input = new Scanner(System.in);
        String query = "";
        try{
            query = input.nextLine();
            return query;
        }
        catch (NoSuchElementException ex){
            throw new InputException("An error occured while waiting for input."
                    + "\nYou may be redirected.", new NoSuchElementException());
        }
        catch (Exception ex){
            throw new InputException("An error occured while waiting for input."
                    + "\nYou may be redirected.", null);
        }
    }
    
    /**
     * overload of getInput(String message) if no message needs to be printed
     * @return user input
     * @throws InputException gets thrown if the scanner and input create an error. 
     */
    public static String getInput() throws InputException{
        return getInput(null);
    }
    
    /**
     * function to calculate a fine (double), based of the borrow date,taking into 
     * account that a book return fine will only occur after 30 days since the borrow date
     * @param borrowDate the LocalDate when the book was borrowed
     * @see Loan
     * @return fine in an arbitrary unit with cents
     */
    public static double calculateFine(LocalDate borrowDate){
        LocalDate returnDate = borrowDate.plusDays(30);
        long daysPassed = DAYS.between(returnDate, LocalDate.now());
        if (daysPassed > 0){
            return Math.round(daysPassed * 0.1 * 100.0) / 100.0;
        }
        return 0;
    }
    
    /**
     * takes a double (or fine value) and converts it into a String
     * @param fine amount to be converted
     * @return String with concatenations 
     */
    public static String fineToString(double fine) {
        return  fine > 0 ? "£" + String.format("%.2f", fine) : "-";
    }

    /**
     * takes a LocalDate to determine the due fine and output it as a string
     * @param borrowDate the date the loan was taken out
     * @return the fine formated as a string
     * @see Loan
     */
    public static String fineToString(LocalDate borrowDate){
        double fine = calculateFine(borrowDate);
        return fineToString(fine);
    }
    
    /**
     * This function takes a list and formats it nicely for console
     * @param list the list to be formated
     * @return a string containing all the list's informatin
     */
    public static String listToString(List<Object> list){
        String result = "";
        for (Object object : list) {
            result += object.toString() + "\n";
        }
        return result;
    }
    
    /**
     * This function calculates how many loans a single member has taken out of 
     * the library
     * @param listLoan list of all loans taken out
     * @param memberID id of the member in question
     * @return number of loans taken out
     */
    public static int getBooksBorrowed(List<Loan> listLoan, int memberID) {
        int counter = 0;
        for (Loan loan : listLoan){
            if (loan.getMemberID() == memberID) {
                counter ++;
            }
        }
        return counter;
    }

}
