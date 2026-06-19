/**
 * New Account Creation Module.
 *
 * Responsible for:
 * - Collecting customer information
 * - Creating Savings and Current Accounts
 * - Generating account numbers
 * - Initializing account balance
 * - Creating required data files
 * - Recording account creation date
 */
import java.util.*;
import java.io.*;;
public class bankAccount{
    Scanner scr = new Scanner(System.in);
    // Customer personal information
    String name; 
    String accountType; 
    int age; 
    String DOB;
    String address;
    // Account information
    long acc; 
    int securityPin;
    String confirmation;
    // Financial information
    double deposit=0.0; 
    double balance; 
    /**
    * Handles the new account creation process.
    *
    * Collects customer information, validates
    * account requirements, generates an account number, and stores all account data.
    *
    * @param ch User selected option.
    */
    public void accountDetail(char ch){
        Bank obj = new Bank();
        switch(ch){
            // Create a new bank account
            case'A':
            System.out.println("Thank you choosing to open an account with our bank!"); //entering all the data 
            System.out.println("We value your trust and look forward to serving you."); //from the user to form the bank account.
            System.out.println("Please enter your details as follows: ");
            System.out.println(" 1. Enter Personal Details: ");
            System.out.println("     A. Name:");
            name = scr.nextLine();
            System.out.println("     B. Age:");
            age = scr.nextInt();
            scr.nextLine();
            System.out.println("     C.Date Of Birth:");
            DOB = scr.nextLine();
            System.out.println("     D.Address:");
            address = scr.nextLine();
            // Display account options and features
            System.out.println(" 2. Choose Account Type --> ");
            System.out.println("\n--- Savings Account Details ---");
            System.out.println("• Benefit: Earn 0.8% p/a compounded annualy.");
            System.out.println("• Interest is calculated monthly and added to your balance.");
            System.out.println("• Maximum deposit limit: ₹10,00,000 per financial year.");
            System.out.println("• Ideal for individuals who want to grow their money safely.");
            System.out.println("\n--- Current Account Details ---");
            System.out.println("• Benefit: Unlimited transactions with no interest.");
            System.out.println("• Minimum balance requirement: ₹3000.");
            System.out.println("• Accounts with balance less than ₹3000 will be penalised.");
            System.out.println("• Designed for businesses and frequent transactions.");
            accountType = scr.nextLine();
            // Current Account validation:
            // Minimum opening balance ₹3000
            if(accountType.equalsIgnoreCase("Current")){
               System.out.println(" 3. Set Initial Deposit");
               deposit = scr.nextDouble();
               if(deposit<3000){
                   System.out.println(" Invalid Amount[Min. - 3000] \n TRY AGAIN \n EXITING THE SERVER!!!");
                   System.exit(0);
               }
            }
            // Savings Account validation:
            // Maximum deposit ₹10 lakh per FY
            else if(accountType.equalsIgnoreCase("Saving")){
               System.out.println(" 3. Set Initial Deposit");
               deposit = scr.nextDouble();
               if(deposit>1000000){
                   System.out.println("AMOUNT EXCEEDED THE MAX. LIMIT. \n ABORTING....");
                   System.exit(0);
               }
            }
            // Set account authentication PIN
            System.out.println(" 4. Set Security PIN[Six Digit]");
            securityPin = scr.nextInt();
            scr.nextLine();
            System.out.println(" 5. Confirm Account Creation[Type Yes.]");
            confirmation = scr.nextLine();
            if(confirmation.equalsIgnoreCase("No")){
                System.exit(0);
            }
            // Generate a random 11-digit account number
            for(int i=1;i<=11; i++){ 
                acc = (long)((acc*10)+(Math.random()*10));
            }
            generateAcc obbj = new generateAcc();
            /**
             * saving all the data entered by the user using the methods and
             * also calling the class generaeaAcc for printing the deatils. 
            */
            try{ 
              Save(); 
              bankDate();
              obbj.accCal();
            }
            catch(Exception e){}
            break;
            
            case 'B':
                System.out.println("Service Currently Unavilable.....");
                obj.mainChoice();
                break;
            case 'C':
                System.out.println("Service Currently Unavilable.....");
                obj.mainChoice();
                break;
            case 'D':
                obj.mainChoice();
                break;
            default:
                System.out.println("ENTERED WRONG OPTION! /n PLEASE TRY AGAIN.....");
                obj.mainChoice();
                break;
        }
    }
    /**
    * Stores newly created account information.
    *
    * Creates and initializes:
    * - bank.dat
    * - security.dat
    * - bal.dat
    * - AccHistory.dat
    *
    * Also records the opening deposit as the
    * first account transaction.
    */
    public void Save() throws IOException{
        // Initialize account balance using the opening deposit amount
        balance= balance+ deposit;
        // Store customer and account details
        DataOutputStream out = new DataOutputStream(new FileOutputStream("bank.dat"));
        out.writeUTF(name);
        out.writeInt(age);
        out.writeUTF(DOB);
        out.writeUTF(address);
        out.writeUTF(accountType);
        out.writeLong(acc);
        out.close();
        // Store security PIN separately
        DataOutputStream secOut = new DataOutputStream(new FileOutputStream("security.dat"));
        secOut.writeInt(securityPin);
        secOut.close();
        // Store opening account balance
        DataOutputStream balOut = new DataOutputStream(new FileOutputStream("bal.dat"));
        balOut.writeDouble(balance);
        balOut.close();
        // Record opening deposit in transaction history
        DataOutputStream AccHisOut = new DataOutputStream(new FileOutputStream("AccHistory.dat"));
        AccHisOut.writeDouble(deposit);
        AccHisOut.close();
    }
    /**
    * Initializes all date-related account records.
    *
    * Creates:
    * - bankDate.dat
    * - interestDate.dat
    * - FY.dat
    * - Ban.dat
    *
    * These files are later used for:
    * - Interest calculations
    * - Financial-year tracking
    * - SFT monitoring
    * - Account suspension management
    */
    public void bankDate() throws IOException{
        // Obtain current system date
        dateRefer d = new dateRefer();
        d.dateCompute();
        FileOutputStream outF = new FileOutputStream("bankDate.dat");
        DataOutputStream date = new DataOutputStream(outF);
        date.writeInt(d.date);
        date.writeInt(d.month);
        date.writeInt(d.year);
        date.close();
        // Initialize interest reference date
        DataOutputStream interest = new DataOutputStream(new FileOutputStream("interestDate.dat"));
        interest.writeInt(d.date);
        interest.writeInt(d.month);
        interest.writeInt(d.year);
        interest.close();
        /**
        * Initialize Financial Year tracking.
        * FY runs from April to March.
        * Store the opening deposit as the first FY deposit amount.
        */
        DataOutputStream FYinn = new DataOutputStream(new FileOutputStream("FY.dat")); //NEW CHANGE........
        if(d.month >= 4){
            FYinn.writeInt(d.year);
        }
        else{
            FYinn.writeInt((d.year)-1);
        }
        FYinn.writeDouble(deposit);
        FYinn.close();
        // New accounts start with no suspension
        DataOutputStream BAN = new DataOutputStream(new FileOutputStream("Ban.dat"));
        BAN.writeBoolean(false);
        BAN.close();
    }
}