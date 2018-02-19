/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
/**
 *
 * @author maxpr
 * @version 1.0
 */
public class MiscOperations {
        
    public static ArrayList<String> readFile(String fileName) 
            throws FileNotFoundException, IOException{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            ArrayList<String> fileData = new ArrayList<>();  
            while ((line = br.readLine()) != null){
                fileData.add(line);
            }
            return fileData;
    }
    
    public static ArrayList<Member> initMembers(ArrayList<String> data){
        ArrayList<Member> memberList = new ArrayList<>();
         for  (String line: data){
                String[] memberProps = line.split(",");
                int memberID = Integer.parseInt(memberProps[0]);          //TODO implement Exceptioncase; (NumberFormatException)
                String memberFirstName = memberProps[1];                  //TODO implement Exceptioncase; (ArrayOutOfBounds)
                String memberLastName = memberProps[2];                   //TODO implement Exceptioncase; (ArrayOutOfBounds)
                LocalDate registerDate = LocalDate.parse(memberProps[3]); //TODO implement Exceptioncase; (ArrayOutOfBounds, DateTimeParseException) 
                Member member = new Member(memberID, memberFirstName, memberLastName, registerDate);
                memberList.add(member);
            }
         return memberList;   
    }
    
    public static ArrayList<Book> initBooks(ArrayList<String> data){ //TODO implement Exceptions, ArrayOutOfBounds, NumberFormatException
        ArrayList<Book> bookList = new ArrayList<>();        
        for  (String line: data){
                String[] bookProps = line.split(",");
                int ID = Integer.parseInt(bookProps[0]);
                String title = bookProps[1];
                String[] authors = bookProps[2].split(":");
                int year = Integer.parseInt(bookProps[3]);
                int quant = Integer.parseInt(bookProps[4]);
                Book book = new Book(ID, title, authors, year, quant);
                bookList.add(book);
            }
        return bookList;
    }
    
    public static ArrayList<Loan> initLoans(ArrayList<String> data){//TODO implement Exceptions (numberFormatExceptio, ArrayOutOfBounds)
        ArrayList<Loan> loanList = new ArrayList<>();
        for  (String line: data){
                String[] loanProps = line.split(",");
                int loanID = Integer.parseInt(loanProps[0]);
                int bookID = Integer.parseInt(loanProps[1]);
                int memberID = Integer.parseInt(loanProps[2]);
                LocalDate loanDate = LocalDate.parse(loanProps[3]);
                Loan loan = new Loan(loanID, bookID, memberID, loanDate);
                loanList.add(loan);
        }
        return loanList;
    }
 
    public static void writeData(String fileData, String text){
        try {
            Writer output = new BufferedWriter(new FileWriter(fileData, true));
            output.append("\n" + text);
            output.close();
        } catch (Exception e) {
            System.out.println("Could not find data files!");
        }
    }
    
    public static String getInput() throws InputException{
        Scanner input = new Scanner(System.in);
        String query = "";
        try{
            query = input.nextLine();
        }
        catch (NoSuchElementException e){
            throw new InputException("An error occured while waiting for input.", new NoSuchElementException());
        }
        catch (Exception e){
            throw new InputException("An error occured while waiting for input.", null);
        }
        finally{
            return query;
        }
    }
}
