package library;

import java.time.LocalDate;

public class Member {

    private int memberID;
    private String foreName;
    private String lastName;
    private LocalDate registerDate;

    public Member(int memberID, String foreName, String lastName, LocalDate registerDate) {
        this.memberID = memberID;
        this.foreName = foreName;
        this.lastName = lastName;
        this.registerDate = registerDate;
    }

    public String getForeName(){
        return foreName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getFullName(){
        return this.foreName + " " + this.lastName;
    }

    public int getID(){
        return this.memberID;
    }

    public LocalDate getRegisterDate(){
        return registerDate;
    }

    public String[] formatData(){
        String[] data = {Integer.toString(memberID),
                         foreName,
                         lastName,
                         registerDate.toString()};
        return data;
    }

    @Override
    public String toString(){
            return String.join(",", formatData());
    }

}
