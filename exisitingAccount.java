/**
 * Existing Account Module
 *
 * Handles operations for already-created bank accounts.
 * Provides account authentication, deposits, withdrawals,
 * balance inquiry, transaction history, annual interest
 * processing, financial-year deposit tracking, SFT monitoring,
 * account suspension checks, and current-account compliance checks.
 *
 * This class acts as the central controller between
 * Savings Account and Current Account functionality.
 */
import java.util.*;
import java.io.*;
public class exisitingAccount{
    Scanner S = new Scanner(System.in);
    // Stores account holder information
    String name1; 
    String accountType1; 
    int age1;
    String DOB1;
    String address1;
    long acc1; 
    int securityPin1;
    
    // Financial data
    double deposit1=0.0; 
    double balance1;
    double withdraw;
    double state;
    
    // Account creation date
    int date1, month1, year1;
    
    //Account flag Checks
    boolean penal =false; //Ensuring that the min. balance flag is written only once... [CURRENT]
    boolean check1 =false; //To check if the account is still BANNED OR NOT... [SAVINGS]
    
    /**
    * Reads account details from persistent storage files.
    *
    * Loads:
    * - Personal information
    * - Account number
    * - Security PIN
    * - Account creation date
    * 
    * Used whenever an existing account session begins.
    */
    public void readAccount()throws IOException{
        boolean EOF = false;
        DataInputStream fileInput = new DataInputStream(new FileInputStream("bank.dat"));
        while(!EOF){
          try{
            name1= fileInput.readUTF();
            age1= fileInput.readInt();
            DOB1= fileInput.readUTF();
            address1= fileInput.readUTF();
            accountType1= fileInput.readUTF();
            acc1 = fileInput.readLong();
          }
          catch(EOFException el){
              EOF= true;
          }
        }
        fileInput.close();
        try(DataInputStream secInput = new DataInputStream(new FileInputStream("security.dat"))){
           securityPin1= secInput.readInt();
        }
        catch(Exception el){}
        try(DataInputStream balInput = new DataInputStream(new FileInputStream("bal.dat"))){
           balance1= balInput.readDouble();
           balInput.close();
        }
        catch(Exception el){}
        /*try(DataInputStream AccHisInput = new DataInputStream(new FileInputStream("AccHistory.dat"))){
           deposit1= AccHisInput.readDouble();
        }
        catch(Exception el){}*/
        
        try{writedate();}
        catch(Exception eeel){}
    }
    /** Retrieves the account creation date from storage. */
    public void writedate()throws IOException{
        boolean EOF = false;
        FileInputStream filein1 = new FileInputStream("bankDate.dat");
        DataInputStream fileInput1 = new DataInputStream(filein1);
        while(!EOF){
          try{
            date1= fileInput1.readInt();
            month1= fileInput1.readInt();
            year1= fileInput1.readInt();
          }
          catch(EOFException el){
              EOF= true;
          }
        }
        fileInput1.close();
    }
    
    /**
    * Main account session controller.
    *
    * Handles all operations available to an authenticated user,
    * including:
    *
    * - Interest processing
    * - SFT compliance checks
    * - Current account compliance checks
    * - Account information display
    * - Deposits
    * - Withdrawals
    * - Balance inquiry
    * - Statement generation
    * - Navigation and logout
    *
    * This method acts as the central operation hub
    * for existing account management.
    *
    * @param chh User-selected menu option.
    */
    public void reVisit(char chh) throws IOException{
        // Main banking system object used for navigation
        Bank Obj1 = new Bank();
        // Savings account operations object
        savingAcc inOBJ = new savingAcc();
        // Current account monitoring object
        currentAcc checkOBJ = new currentAcc();
        // Display current system timestamp
        Date sysDate = new Date();
        System.out.println("SYSTEM LOG: --> "+sysDate);
        
        /** Automatically check and apply annual interest before any account operation is performed. */
        if(accountType1.equalsIgnoreCase("Saving")){ 
            inOBJ.interestD();
            inOBJ.interestCal();
        }
        /**
        * Verify suspension status and updatefinancial-year deposit tracking.
        * Applicable only to Savings Accounts.
        */
        if(accountType1.equalsIgnoreCase("Saving")){
            try{
                check1 = inOBJ.offBan();
                inOBJ.intCheck(deposit1);
            } 
            catch(Exception e){}
        }
        /**
        * Current Accounts must maintain a minimum balance of ₹3000.
        * If balance falls below the threshold, penalty monitoring is activated.
        */
        if(accountType1.equalsIgnoreCase("Current")&& balance1<3000){
            checkOBJ.currentCheck();
            penal = checkOBJ.penalty;
        }
        /** 
         * Synchronize balance with persistent storage before executing user-selected operations.
        */
        try(DataInputStream balInput = new DataInputStream(new FileInputStream("bal.dat"))){
           balance1= balInput.readDouble();
        }
        catch(Exception el){}
        switch(chh){
            // Display personal account information
            case 'A':
                System.out.println("Name --> "+name1);
                System.out.println("Age --> "+age1);
                System.out.println("D.O.B --> "+DOB1);
                System.out.println("Address --> "+address1);
                Obj1.choice1(2);
                break;
            // Display banking and account details
            case 'B':
                System.out.println("Account Type --> "+accountType1);
                System.out.println("Account Number --> "+acc1);
                System.out.printf("Account Balance --> " + "%.2f%n",balance1);
                System.out.println("Account Creation date --> "+date1+ "/"+month1+ "/"+year1);
                Obj1.choice1(2);
                break;
            /**
            * Deposit Operation -->
            *
            * Allows funds to be added to the account.
            * Savings Accounts are additionally checked for financial-year deposit compliance.
            */
            case 'C':
                if(accountType1.equalsIgnoreCase("Saving")){ 
                  // Prevent transactions while account is suspended
                  if(check1 == true){  
                    System.out.println("YOUR ACCOUNT HAS BEEN SUSPENDED FOR EXCEEDING THE MAXIMUM TRANSACTION LIMIT FOR A FINANCIAL YEAR --> THEREFORE ALL TRANSACTION ACTIVITY HAS BEEN SUSPENDED FOR THE MOMENT....");
                    System.out.println("DAYS LEFT[For ban upliftment] --> "+ (30-inOBJ.DAYS));
                    Obj1.choice1(2);
                  }
                }
                System.out.println("Enter the amount to be deposited -->");
                deposit1 = S.nextDouble();
                /**
                * Current account revival detection.
                * Record the date when funds are reintroduce into a previously empty account.
                */
                if(accountType1.equalsIgnoreCase("Current")){
                    if(balance1 ==0.0 && deposit1>0.0){
                        Calendar cs = Calendar.getInstance();
                        int d = cs.get(Calendar.DATE);
                        int m = cs.get(Calendar.MONTH)+1;
                        int y = cs.get(Calendar.YEAR);
                        try{
                           DataOutputStream CU = new DataOutputStream(new FileOutputStream("cuDATE.dat")); 
                           CU.writeInt(d);
                           CU.writeInt(m);
                           CU.writeInt(y);
                           CU.writeBoolean(true);
                           CU.close();
                        }
                        catch(Exception el){}
                    }
                }
                // Update account balance
                balance1 += deposit1;
                System.out.printf("Remaining Balance --> " + "%.2f%n",balance1);
                try{
                    //Persist updated balance
                    Balance();
                    // Record credit transaction in account history
                    DataOutputStream AccHisOut = new DataOutputStream(new FileOutputStream("AccHistory.dat",true));
                    AccHisOut.writeDouble(deposit1);
                    AccHisOut.close();
                }
                catch(Exception els){}
                /** CHECKS THE SAVINGS ACCOUNT IF THE TOTAL DEPOSIT IS NOT ABOVE 10LAKH */
                if(accountType1.equalsIgnoreCase("Saving")){
                    try{inOBJ.intCheck(deposit1);}  // NEW......
                    catch(Exception e){}
                }
                deposit1 = 0.0;
                Obj1.choice1(2);
                break;
                /**
                * Withdrawal Operation -->
                * 
                * Allows funds to be removed from the account.
                * Also monitors current-account minimum balance compliance.
                */
            case 'D':
                if(accountType1.equalsIgnoreCase("Saving")){ 
                  if(check1 == true){
                      System.out.println("YOUR ACCOUNT HAS BEEN SUSPENDED FOR EXCEEDING THE MAXIMUM TRANSACTION LIMIT FOR A FINANCIAL YEAR --> THEREFORE ALL TRANSACTION ACTIVITY HAS BEEN SUSPENDED FOR THE MOMENT....");
                      System.out.println("DAYS LEFT[For ban upliftment] --> "+ (30-inOBJ.DAYS));
                      Obj1.choice1(2);
                  }
                }
                double BAL=0;
                BAL = balance1;
                System.out.println("Enter the amount to Withdraw -->");
                withdraw = S.nextDouble();
                // Prevent overdraft transactions
                if(balance1<withdraw){
                    System.out.println("😡WITHDRAWING MORE THAN AVAILABLE BALANCE.....");
                    reVisit('D');
                }
                else{
                    balance1 -= withdraw;
                }
                System.out.printf("Remaining Balance --> " + "%.2f%n",balance1);
                try{
                    Balance();
                    DataOutputStream AccHisOut = new DataOutputStream(new FileOutputStream("AccHistory.dat",true));
                    AccHisOut.writeDouble(-withdraw);
                    AccHisOut.close();
                }
                catch(Exception els){}
                /**
                * Current account minimum balance violation.
                * Start the 31-day compliance countdown.
                */
                if(accountType1.equalsIgnoreCase("Current")){
                  if(balance1<3000 && penal == false){
                    if(balance1!=0.0){
                       System.out.println("ALERT: Your account is currently non-compliant with the Minimum Balance Requirement.");
                       System.out.printf("Current Balance --> "+ "%.2f%n",balance1);
                       System.out.println("Required Minimum Balance: ₹3,000.00");
                       System.out.println("Failure to maintain a balance of ₹3,000 or above within the next 31 days may result in a Low Balance Penalty being charged to your account.");
                       Calendar cs = Calendar.getInstance();
                       int d = cs.get(Calendar.DATE);
                       int m = cs.get(Calendar.MONTH)+1;
                       int y = cs.get(Calendar.YEAR);
                       try{
                           DataOutputStream CU = new DataOutputStream(new FileOutputStream("cuDATE.dat")); 
                           CU.writeInt(d);
                           CU.writeInt(m);
                           CU.writeInt(y);
                           CU.writeBoolean(true);
                           CU.close();
                       }
                       catch(Exception el){}
                    }
                  }
                }
                /** 
                * STARTING THE GRACE PERIOD AS THE BALANCE RREACH ZERO AND 'BAL' KEEPS TRACK 
                * THAT IT IS WRITTEN ONLY ONCE.
                */
                if(BAL>0 && balance1 ==0.0){
                        System.out.println("ACCOUNT STATUS NOTICE: \nYour account balance has reached ₹0.00.");
                        Calendar cs = Calendar.getInstance();
                       int d = cs.get(Calendar.DATE);
                       int m = cs.get(Calendar.MONTH)+1;
                       int y = cs.get(Calendar.YEAR);
                       try{
                           DataOutputStream CU = new DataOutputStream(new FileOutputStream("cuDATE.dat")); 
                           CU.writeInt(d);
                           CU.writeInt(m);
                           CU.writeInt(y);
                           CU.writeBoolean(true);
                           CU.close();
                       }
                       catch(Exception el){}
                }
                Obj1.choice1(2);
                break;
            // Display latest account balance
            case 'E':
                if(accountType1.equalsIgnoreCase("Saving")){ 
                  inOBJ.interestD();
                  inOBJ.interestCal();
                }
                System.out.printf("Account Balance --> " + "%.2f%n",balance1);
                Obj1.choice1(2);
                break;
            case 'F':
                /**
                * Account Statement Viewer
                *
                * Displays all recorded credits and debits stored in transaction history.
                */
                System.out.println("Account Statement -->");
                boolean EOF = false;
                try{
                    DataInputStream AccHisInput = new DataInputStream(new FileInputStream("AccHistory.dat"));
                    while(!EOF){
                       state = AccHisInput.readDouble();
                       // Positive value represents a deposit
                       if(state>0){
                           System.out.println("Credit --> "+state);
                       }
                       // Negative value represents a withdrawal
                       else if(state<0){
                           System.out.println("Debit --> " + (-state));
                       }
                    }
                }
                catch(Exception e){
                    Obj1.choice1(2);
                }
                Obj1.choice1(2);
                break;
            case 'G':
                System.exit(0);
            case 'H':
                Obj1.mainChoice();
                break;
            default:
                System.out.println("Entered Wrong choice, TRY AGAIN!!");
                Obj1.choice1(2);
        }
    }
    
    /**
    * Writes the current account balance to bal.dat.
    *
    * Stores the latest account balance in persistent storage.
    * Called after deposit, withdrawal, interest credit,
    * penalty application, or any operation that modifies
    * the account balance.
    */
    public void Balance() throws IOException{
        DataOutputStream out1 = new DataOutputStream(new FileOutputStream("bal.dat"));
        out1.writeDouble(balance1);
        out1.close();
    }
}