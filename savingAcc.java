/**
 * Savings Account Processing Module.
 *
 * Handles:
 * - Interest eligibility tracking
 * - Interest calculation
 * - Savings account balance updates
 *
 * Uses daysCal to determine completed
 * interest periods before applying
 * compound interest.
 */
import java.util.*;
import java.io.*;
public class savingAcc{
    
    // Number of completed interest periods
    int year; 
    
    // Stored interest reference date
    int d1, m1, y1; 
    
    // Store current account balance
    double BAL;
    
    // Account status flags used for tracking the account SFT status...
    double totalDeposit =0.0;
    boolean accBAN;
    int DAYS=0;//calculate total days the ban is applied on the Savings Account...
    boolean check =false; //To check if the account is still BANNED OR NOT... [SAVINGS]
    /**
    * Determines the number of completed
    * interest periods since the last interest calculation.
    *
    * Reads the reference date from interestDate.dat and uses daysCal to compute elapsed time.
    */
    public void interestD() throws IOException{
        daysCal OBJ = new daysCal();
        // Read the last interest calculation date
        boolean eof= false;
        DataInputStream interestIn = new DataInputStream(new FileInputStream("interestDate.dat"));
        while(!eof){
          try{
            d1= interestIn.readInt();
            m1= interestIn.readInt();
            y1= interestIn.readInt();
          }
          catch(EOFException el){
              eof= true;
          }
        }
        interestIn.close();
        // Supply stored reference date to daysCal
        OBJ.dd1 = d1; 
        OBJ.mm1 = m1; 
        OBJ.yy1 = y1;
        // Calculate elapsed time since last interest application
        try{OBJ.Cal();}
        catch(Exception el){}
        // Store the number of completed interest periods returned by daysCal
        year = OBJ.n;
    }
    /**
    * Applies compound interest to the savings account balance.
    * 
    * Interest is only applied when one
    * or more complete interest periods have elapsed.
    *
    * After crediting interest, the
    * reference date is updated to prevent duplicate interest.
    */
    public void interestCal(){
        // Obtain current system date
       Calendar cs = Calendar.getInstance();
       int d = cs.get(Calendar.DATE);
       int m = cs.get(Calendar.MONTH) +1;
       int y = cs.get(Calendar.YEAR);
       // Retrieve current account balance
       try(DataInputStream balInput = new DataInputStream(new FileInputStream("bal.dat"))){
           BAL= balInput.readDouble();
        }
        catch(Exception el){}
       /*
       * Interest is applied only after at least one complete interest period has elapsed.
       */
       if(year>0){
           // Annual interest rate
          double rByN = 0.8/100;
          double amount = 0.0;
          // Apply compound interest for all completed interest periods
          amount = BAL*Math.pow((1.00+rByN),year);
          BAL = amount;
          try{Balance();}
          catch(Exception els){}
          // Update interest reference date to prevent duplicate credits
          try{
             DataOutputStream interest = new DataOutputStream(new FileOutputStream("interestDate.dat")); //CHANGE
             interest.writeInt(d);
             interest.writeInt(m);
             interest.writeInt(y);
             interest.close();
          }
          catch(Exception el){}
       }
    }
    /**
    * Stores the latest savings account balance in persistent storage.
    */
    public void Balance() throws IOException{
        DataOutputStream out1 = new DataOutputStream(new FileOutputStream("bal.dat"));
        out1.writeDouble(BAL);
        out1.close();
    }
    /**
    * Applies an SFT suspension to the account.
    *
    * Triggered when total deposits in the current
    * financial year exceed ₹10 lakh. [THIS FUNC. IS ONLY CALLED ONCE WHEN A SFT FLAG IS RAISED]
    *
    * Actions performed:
    * - Flags the account for SFT reporting.
    * - Suspends account transaction activity.
    * - Stores suspension status persistently.
    * - Records the suspension date.
    *
    * The recorded date is later used to determine
    * whether the 30-day suspension period has expired.
    *
    * @param B true to activate the suspension
    */
    public void ban(boolean B){
        // Proceed only when suspension is required
        if(B){
            /** Notify the user that the account has been flagged under SFT regulations */
            System.out.println("Account flagged for SFT [Statement of Financial Transaction(Exceeded 10lakh)] reporting to the Income Tax Department.");
            System.out.println("ACCOUNT TRANSACTION ACITVITY SUSPENDED...... \nSORRY FOR THE INCONVENIENCE........");
            try{
               DataOutputStream BAN = new DataOutputStream(new FileOutputStream("Ban.dat"));
               BAN.writeBoolean(true);
               BAN.close();
            }
            catch(Exception el){}
            
            // Capture the current system date.
            // This date marks the beginning of the 30-day suspension period.
            Calendar cs = Calendar.getInstance();
            int d = cs.get(Calendar.DATE);
            int m = cs.get(Calendar.MONTH)+1;
            int y = cs.get(Calendar.YEAR);
            try{
               DataOutputStream dat = new DataOutputStream(new FileOutputStream("banDate.dat"));
               dat.writeInt(d);
               dat.writeInt(m);
               dat.writeInt(y);
               dat.close();
            }
            catch(Exception el){}
            
            /**
            * Suspension date is stored separately because
            * the account may be accessed many days later.
            * The stored date allows daysCal to determine
            * when the 30-day restriction period has ended.
            */
        }
    }
    
    /**
    * Checks whether an account suspension period has expired.
    *
    * Process:
    * 1. Reads current suspension status.
    * 2. Retrieves the recorded suspension date.
    * 3. Calculates days elapsed since suspension.
    * 4. Removes the suspension after 30 days.
    * 5. Resets FY deposit tracking after suspension expiry.
    *
    * @return true  -> account is still suspended
    * @return false -> account is active
    */
    public boolean offBan() throws IOException{
        // Stores recorded suspension date
        int d =0,m=0,y=0;
        
        // Read current suspension status
        try(DataInputStream BAN = new DataInputStream(new FileInputStream("Ban.dat"))){
           accBAN = BAN.readBoolean();
           BAN.close();
        }
        catch(Exception el){}
        
        boolean eof =false;
        // Retrieve stored suspension date
        DataInputStream dat = new DataInputStream(new FileInputStream("banDate.dat"));
        while(!eof){
           try{
              d=dat.readInt();
              m=dat.readInt();
              y=dat.readInt();
           }
           catch(EOFException el){
              eof= true;
           }
        }
        dat.close();
        /**   
        *   'daysCal' class is reused here to determine whether
        *   the mandatory 30-day suspension period has
        *   elapsed since the SFT violation.
        *   
        *   Provide suspension date as the reference date for daysCal
        */
        daysCal sui = new daysCal();
        sui.dd1 = d; 
        sui.mm1 = m; 
        sui.yy1 = y;
        sui.Cal(); 
        DAYS = sui.days;
        
        /** Proceed only if the account is currently under suspension */
        if(accBAN){
            if(sui.days >=30){
                // "Suspension period completed. Restore account access."
               accBAN = false;
               // Remove suspension status and persistently store updated account status
               try{
                  DataOutputStream BAN = new DataOutputStream(new FileOutputStream("Ban.dat")); //CHANGE
                  BAN.writeBoolean(accBAN);
                  BAN.close();
               }
               catch(Exception el){}
               
               /**
               * Once the suspension period ends,
               * the FY deposit tracker is reset.
               *
               * This starts a fresh monitoring cycle
               * for the current financial year.
               */
               totalDeposit =0.0; int FY =0;
               if(sui.mm2 >=4){
                  FY = sui.yy2;
               }
               else{
                  FY = sui.yy2-1;
               }
               try{
                  DataOutputStream FYin = new DataOutputStream(new FileOutputStream("FY.dat")); 
                  FYin.writeInt(FY);
                  FYin.writeDouble(totalDeposit);
                  FYin.close();
               }
               catch(Exception el){}
               
               // Suspension removed successfully
               return false;
            }
            else{
               // Suspension still active
               return true;
            }
        }
        else{
            // Account was never suspended
            return false;
        }
    }
    
    /** "Core Financial-Year Compliance Engine"
    * Financial Year Deposit Tracking System
    *
    * Maintains the total amount deposited during the current
    * financial year and monitors deposits for SFT compliance.
    *
    * Process:
    * 1. Reads previously stored financial-year data.
    * 2. Determines the current financial year(April to March).
    * 3. Resets tracking if a new financial year begins.
    * 4. Updates total deposits for the current FY.
    * 5. Stores updated FY information.
    * 6. Checks whether deposits exceed ₹10 lakh.
    * 7. Triggers account suspension if the SFT limit is crossed.
    *
    * Used only for Savings Accounts.
    */
    public void intCheck(double deposit) throws IOException{
        // Variables used for financial-year tracking
        int month, currentFY, storedFY =0;
        // Get current month from system calendar
        Calendar cs = Calendar.getInstance();
        month = cs.get(Calendar.MONTH)+1;
        // Read previously stored FY and deposit information
        DataInputStream FY = new DataInputStream(new FileInputStream("FY.dat")); boolean eof=false;
        while(!eof){
           try{
              storedFY = FY.readInt();
              totalDeposit = FY.readDouble();
           }
           catch(EOFException el){
              eof= true;
           }
        }
        FY.close();
        // Determine current financial year.
        // FY runs from April(4) to March(3).
        if(month>=4){
            currentFY = cs.get(Calendar.YEAR);
        }
        else{
            currentFY = cs.get(Calendar.YEAR) -1;
        }
        
        /** New financial year detected. Reset deposit tracking and remove any previous SFT-related suspension state. */
        if(storedFY != currentFY){
            totalDeposit = 0;
            check=false;
             try{
               DataOutputStream BAN = new DataOutputStream(new FileOutputStream("Ban.dat"));
               BAN.writeBoolean(false);
               BAN.close();
            }
            catch(Exception el){}
        }
        /** Same financial year. Add the current deposit to the FY total. */
        else{
            totalDeposit += deposit;
        }
        //WRITES DOWN THE NEW DETAILS RELATED TO THE SFT CALCULATIONS
        try{
             DataOutputStream FYin = new DataOutputStream(new FileOutputStream("FY.dat")); 
             FYin.writeInt(currentFY);
             FYin.writeDouble(totalDeposit);
             FYin.close();
        }
        catch(Exception el){}
        
        System.out.printf("TOTAL DEPOSIT --> "+ "%.2f%n",totalDeposit);
        /** Read If the Account is already BANNED for SFT or Not */
        try(DataInputStream BAN = new DataInputStream(new FileInputStream("Ban.dat"))){
           accBAN = BAN.readBoolean();
           BAN.close();
        }
        
        /** SFT Rule:
        *  If deposits exceed ₹10 lakh in the current financial year and the account is not already
        *  suspended, trigger a 30-day suspension. 
        */
        if(totalDeposit>1000000 && accBAN ==false){
            ban(true);
        }
    }
}