package library;

import java.time.LocalDate;
/**
 * This class represents a member in the library system
 * @author Sam Stenner, Max Prüstel
 */
public class Member {

    private int memberID;
    private String foreName;
    private String lastName;
    private LocalDate registerDate;
    
    /**
     * The constructor, taking all relevant information for the creation of a 
     * member object.
     * @param memberID the ID the member should have
     * @param foreName the members first name
     * @param lastName the members last name
     * @param registerDate the date of registration
     */
    public Member(int memberID, String foreName, String lastName, LocalDate registerDate) {
        this.memberID = memberID;
        this.foreName = foreName;
        this.lastName = lastName;
        this.registerDate = registerDate;
    }
    
    /**
     * Getter for the members first name.
     * @return first name of the member
     */
    public String getForeName(){
        return foreName;
    }
    
    /**
     * Getter for the members last name.
     * @return last name of the member
     */
    public String getLastName(){
        return lastName;
    }
    
    /**
     * Function to return a concatenation of the members full name (first name 
     * + last name)
     * @return full name of the member
     */
    public String getFullName(){
        return this.foreName + " " + this.lastName;
    }
    
    /**
     * Getter for the member's ID 
     * @return member ID
     */
    public int getID(){
        return this.memberID;
    }
    
    /**
     * Getter for the registration date of the member
     * @return LocalDate object containing the members registration date
     */
    public LocalDate getRegisterDate(){
        return registerDate;
    }
    
    /**
     * Function formatDate produces a String array containing all relevant member information:
     * member ID, fore name, last name and registration date.
     * @return member data in a String array
     */
    public String[] formatData(){
        String[] data = {Integer.toString(memberID),
                         foreName,
                         lastName,
                         registerDate.toString()};
        return data;
    }

    /**
     * formedString produces a console formatted output of the member data:
     * MemberID, first name, last name, registration date
     * @return a concatenated string containing book information
     */
    public String formedString() {
        String returnString = String.format("Member ID: %d " +
                        "\nFirst Name: %s " +
                        "\nLast Name: %s " +
                        "\nRegistration Date: %s",
                        getID(),
                        getForeName(),
                        getLastName(),
                        getRegisterDate().toString());
        return returnString;
    }
    
    /**
     * returns a string with the member data, which is formated for file output
     * @override Object.toString()
     * @return a String of the member data, commaseperated
     */
    @Override
    public String toString(){
            return String.join(",", formatData());
    }

}
