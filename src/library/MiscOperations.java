/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author maxpr
 * @version 1.0
 */
public class MiscOperations {
        
    public static List<String> readFile(String fileName) throws IOException{
        ArrayList<String> fileData = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        return fileData;
    }

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
    
    public static String getInput() throws InputException{
        Scanner input = new Scanner(System.in);
        String query = "";
        try{
            query = input.nextLine();
        }
        catch (NoSuchElementException ex){
            throw new InputException("An error occured while waiting for input."
                    + "\nYou may be redirected.", new NoSuchElementException());
        }
        catch (Exception ex){
            throw new InputException("An error occured while waiting for input."
                    + "\nYou may be redirected.", null);
        }
        finally{
            return query;
        }
    }

    public static double calculateFine(LocalDate borrowDate){
        LocalDate returnDate = borrowDate.plusDays(30);
        long daysPassed = DAYS.between(returnDate, LocalDate.now());
        if (daysPassed > 0){
            return Math.round(daysPassed * 0.1 * 100.0) / 100.0;
        }
        return 0;
    }

    public static String fineToString(double fine) {
        return  fine > 0 ? "£" + String.format("%.2f", fine) : "-";
    }

    public static String fineToString(LocalDate borrowDate){
        double fine = calculateFine(borrowDate);
        return fineToString(fine);
    }

    public static String listToString(List<Object> list){
        String result = "";
        for (Object object : list) {
            result += object.toString() + "\n";
        }
        return result;
    }

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
