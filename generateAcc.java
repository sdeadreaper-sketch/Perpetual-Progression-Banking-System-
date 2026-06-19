/**
 * Account Creation Confirmation Module.
 *
 * Displays the details of a newly created account after successful account registration.
 */
import java.util.*;
import java.io.*;
public class generateAcc{
    /**
    * Retrieves stored account information and
    * displays a confirmation summary to the user.
    *
    * Includes:
    * - Account Number
    * - Account Type
    * - Current Balance
    * - Creation Timestamp
    */
    public void accCal() throws IOException{
        Date d = new Date();
        exisitingAccount Obj12 = new exisitingAccount();
        try{Obj12.readAccount();}
        catch(Exception eo){}
        System.out.println("\n---------------------------------------------");
        System.out.println("🎉 Congratulations! Your account has been created successfully.");
        System.out.println("Thank you for choosing Alcaerz Bank Union!");
        System.out.println("We value your trust and look forward to serving you.");
        System.out.println("Your Account Number --> " + Obj12.acc1);
        System.out.println("Account Type --> " + Obj12.accountType1);
        System.out.println("Account Balance --> ₹" + Obj12.balance1);
        System.out.println("Creation date and time --> " + d);
        System.out.println("---------------------------------------------");
        System.out.println("💡 Tip: Keep your account number and PIN safe.");
        System.out.println("You can now log in anytime to manage your account.");
        System.out.println("---------------------------------------------");
        
    }
}
