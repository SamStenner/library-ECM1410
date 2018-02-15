import library.Library;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
/**
 * LibraryApp is used to test the libaray management system.
 * All its functions are implemented in Library class
 *
 * @author Hongping Cai
 * @version 2018-02-12
 */
public class LibraryApp
{
    /**
     * execute the program
     *
     * @param args String array for input
     */
    public static void main(String[] args)
    {  
       // STEP1: initiate the library by loading data from 3 text files
       System.out.println("**************************************************");
       System.out.println("* Loading data ...");
       System.out.println("**************************************************");
       Library lib = new Library("data/books.txt","data/members.txt",
       "data/bookloans.txt");
       lib.showAllBooks();    // show all the book records
       lib.showAllMembers();  // show all the member records
       lib.showAllBookLoans();// show all the book loan records
       
       // STEP2: test the library system
       System.out.println("\n* Start testing ...\n");
       boolean isFixInput = false;
       if (isFixInput){
           // OPTIONT 1: Test the library system with fixed input.
           // When you start programming, you may pick this test mode.
           testWithFixInput(lib);
       }
       else{
           // OPTION 2: Test the library system with all keyboard input.
           // This test mode is closer to the real scenario.
           testWithKeyboardInput(lib);
       }
       
       // STEP3: save the changes before quitting the system
       System.out.println("**************************************************");
       System.out.println("* Save the changes ...");
       System.out.println("**************************************************");
       lib.saveChanges("data/books.txt","data/members.txt","data/bookloans.txt");
       
       System.out.println("Bye ...");  
    } 
    
    /**
     * This method is used for testing the library system with fixed input.
     * During my testing, I will likely to change the input for each option.
     * You may use this method for testing your codes.
     * 
     * @param lib the tested library object 
     */
    public static void testWithFixInput(Library lib){
       boolean isValidInput = true;
       char inChar;
       boolean moreRequirement = false;
       do {
           isValidInput = true;
           inChar = getUserInput(); 
           switch (inChar){
               case 's': case 'S'://search for one book
                    lib.searchBook("java");
                    break;
               case 'i': case 'I'://search for member information
                    lib.searchMember("Sarah","Hoopern");
                    break;
               case 'b': case 'B'://borrow one book
                    lib.borrowBook("Data","Jodan","Cooper");
                    break;
               case 'r': case 'R'://return one book via the book loan Id
                    lib.returnBook(300002);
                    break;
               case 'a': case 'A'://add a new book
                    String [] authorNames = new String [] {"Jaideva Goswami"};
                    lib.addNewBook("Fundamentals of Wavelets",authorNames,
                    2010,4);
                    break;
               case 'n': case 'N'://add a new member
                    lib.addNewMember("Hongping","Cai",LocalDate.now());
                    break;
               case 'c': case 'C'://change the stock for a book
                    lib.changeQuantity("Data Mining",-2);
                    break;
               case 'q': case 'Q'://quit
                   return;
               default:
                   isValidInput = false;
           }
           if (isValidInput){
               moreRequirement = anyOtherRequirement();
            }
       } while ((!isValidInput)||(moreRequirement));
    }
    
    /**
     * This method is used for testing the library system with keyboard input.
     * You should offer this method (instead of testWithFixInput())for real scenario,
     * which allows the user for keyboard input when choosing any offered options.
     *
     * @param lib the tested library object
     */
    public static void testWithKeyboardInput(Library lib){
       boolean isValidInput = true;
       char inChar;
       boolean moreRequirement = false;
       do {
           isValidInput = true;
           inChar = getUserInput(); 
           switch (inChar){
               case 's': case 'S'://search for one book
                    lib.searchBook();
                    break;
               case 'i': case 'I'://search for member information
                    lib.searchMember();
                    break;
               case 'b': case 'B'://borrow one book
                    lib.borrowBook();
                    break;
               case 'r': case 'R'://return one book via the book loan Id
                    lib.returnBook();
                    break;
               case 'a': case 'A'://add a new book
                    lib.addNewBook();
                    break;
               case 'n': case 'N'://add a new member
                    lib.addNewMember();
                    break;
               case 'c': case 'C'://change the stock for a book
                    lib.changeQuantity();
                    break;
               case 'q': case 'Q'://quit
                   return;
               default:
                   isValidInput = false;
           }
           if (isValidInput){
               moreRequirement = anyOtherRequirement();
            }
       } while ((!isValidInput)||(moreRequirement));
    }
    /**
     * Display all functions of the library management system.
     * Get the user's input (one charactor from a list of charactors)
     *
     * @return  the user's input charactor  
     */
    private static char getUserInput(){ 
        System.out.println("**************************************************");
        System.out.println("* Choose from the following options:");
        System.out.println("**************************************************");
        System.out.println("*Enter s: [s]earch for one book.");
        System.out.println("*Enter i: search for member [i]nformation.");
        System.out.println("*Enter b: [b]orrow one book.");
        System.out.println("*Enter r: [r]eturn one book.");
        System.out.println("*Enter a: [a]dd a new book into the library.");
        System.out.println("*Enter n: add a [n]ew member into the library.");
        System.out.println("*Enter c: [c]hange the stock quantity for an existing book.");
        System.out.println("*Enter q: [q]uit.");
        Scanner in = new Scanner(System.in);
        char inchar = in.nextLine().charAt(0);
        in.close();
        return inchar;
    }
    /**
     * After finishing one service, ask the user if any other requiements
     *
     * @return yes (true) or no (false)
     */
    private static boolean anyOtherRequirement(){
       boolean moreRequirement = true;
       boolean isYesOrNoInput;
       Scanner in = new Scanner(System.in);
       do {
           System.out.println("\n* Any other requirements?[y/n]");
           char inCh = in.nextLine().charAt(0);
           isYesOrNoInput = true;
           if ((inCh == 'y') || (inCh == 'Y')){
               moreRequirement = true;
           }
           else if ((inCh == 'n') || (inCh == 'N')){
               moreRequirement = false;
           } else {
               System.out.println("******** Wrong input. Try again.");
               isYesOrNoInput = false;
            }
           
        } while (!isYesOrNoInput);
        in.close();
        return moreRequirement;
    }
}


