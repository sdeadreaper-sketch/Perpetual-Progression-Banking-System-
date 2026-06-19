/**
 * Current Account Compliance Module.
 *
 * Handles:
 * - Minimum balance monitoring
 * - Low balance penalty processing
 * - Zero balance detection
 * - Grace period tracking
 * - Permanent account closure checks
 */
import java.util.*;
import java.io.*;
public class currentAcc{
    // Number of days since compliance monitoring began
    int cuD=0; 
    
    // Indicates whether a penalty state is active
    boolean penalty = false; 
    
    // Indicates whether the account has been permanently closed
    boolean permaBAN = false; 
    
    // Prevents repeated warning messages
    private boolean flag= false; 
    private boolean flag1 = false;
    
    // Account status
    boolean accBAN;
    
    // Current account balance
    double BALANCE;
    /**
    * Applies low-balance penalties to Current Accounts.
    *
    * Accounts remaining below ₹3000 for more than
    * 31 days are charged ₹300 for each completed penalty period.
    *
    * Also initiates the 200-day grace period when balance reaches ₹0.
    */
    public void penImp() throws IOException{ 
        // Load latest account balance
        try(DataInputStream balInput = new DataInputStream(new FileInputStream("bal.dat"))){
           BALANCE= balInput.readDouble();
        }
        catch(Exception el){}
        // Calculate completed penalty cycles (1 penalty every 31 days)
        int Q = cuD/31;
        // Apply penalty once the compliance period has expired
        if(cuD>=31){
            // Deduct accumulated low-balance penalties
            BALANCE = BALANCE -(Q*300.00);
            if(BALANCE<0){
                BALANCE =0.0;
                Balance();
            }
            else{
                Balance();
            }
            /*
            * Balance exhausted -->
            * Begin 200-day account reactivation grace period.
            */
            if(BALANCE ==0){
                System.out.println("THE GRACE PERIOD OF 200 DAYS STARTS NOW IF BALANCE IS NOT ABOVE 0.0 \nACCOUNT WILL BE PERMANENTLY BANNED --> ");
                Calendar cs = Calendar.getInstance();
                int d = cs.get(Calendar.DATE);
                int m = cs.get(Calendar.MONTH)+1;
                int y = cs.get(Calendar.YEAR);
                try{
                    // Store grace-period start date
                    DataOutputStream CU = new DataOutputStream(new FileOutputStream("cuDATE.dat")); 
                    CU.writeInt(d);
                    CU.writeInt(m);
                    CU.writeInt(y);
                    CU.writeBoolean(true);
                    CU.close();
                }
                catch(Exception el){}
            }
            else{
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
    * Evaluates Current Account compliance status.
    *
    * Checks:
    * - Minimum balance compliance
    * - Penalty eligibility
    * - Grace period status
    * - Permanent account closure conditions
    *
    * Uses daysCal to determine elapsed time
    * since compliance monitoring began.
    */
    public void currentCheck() throws IOException{
        // Synchronize balance from storage
        try(DataInputStream balInput = new DataInputStream(new FileInputStream("bal.dat"))){
           BALANCE= balInput.readDouble();
           balInput.close();
        }
        catch(Exception el){}
        // Load warning display status
        try(DataInputStream F = new DataInputStream(new FileInputStream("Flag.dat"))){
           flag= F.readBoolean();
           F.close();
        }
        catch(Exception el){}
        try(DataInputStream F = new DataInputStream(new FileInputStream("Flag1.dat"))){
           flag1= F.readBoolean();
           F.close();
        }
        catch(Exception el){}
        // Retrieve compliance monitoring date
        int cd=0, cm=0, cy=0;
        boolean eof =false;
        DataInputStream cu = new DataInputStream(new FileInputStream("cuDATE.dat"));
        while(!eof){
           try{
              cd=cu.readInt();
              cm=cu.readInt();
              cy=cu.readInt();
              penalty = cu.readBoolean();
           }
           catch(EOFException el){
              eof= true;
           }
        }
        // Calculate elapsed days since monitoring began
        daysCal O7= new daysCal(); 
        O7.dd1 =cd; O7.mm1 =cm; O7.yy1 = cy;
        O7.Cal(); cuD = O7.days;
        /**
        * Account is below the required minimum balance threshold but still contains funds.
        */
        if(BALANCE<3000 && BALANCE !=0){
            // Display compliance warning only once
            if(!flag){
                // Calculate days remaining before penalty application
                int DD = 31-cuD; String display = "";
                if(DD<0){
                    display = "Penalty Overdue By " + Integer.toString(-DD);
                }
                else{
                    display = "Days Remaining Before Penalty --> " + Integer.toString(DD);
                }
                System.out.println("ALERT: Your account is currently non-compliant with the Minimum Balance Requirement.");
                System.out.println("Failure to maintain a balance of ₹3,000 or above within the next 31 days may result in a Low Balance Penalty being charged to your account.");
                System.out.println(display);
                try{
                  DataOutputStream CU = new DataOutputStream(new FileOutputStream("Flag.dat")); 
                  CU.writeBoolean(true);
                  CU.close();
                }
                catch(Exception el){}
            }
            // Apply penalty rules if necessary
            penImp();
            System.out.printf("Current Balance --> "+ "%.2f%n",BALANCE);
            System.out.println("Required Minimum Balance: ₹3,000.00");
        }
        /**
        * Account has reached a zero balance.
        * Monitor 200-day grace period before permanent closure.
        */
        else if(BALANCE==0){
            // Grace period expired.
            // Permanently disable account access.
            if(cuD>=200){
                permaBAN = true;
            }
            // Display zero-balance warning only once
            if(!flag1){
              System.out.println("ACCOUNT STATUS NOTICE --> ");
              System.out.println("A grace period of 200 days has been granted to allow account reactivation through a deposit transaction.");
              System.out.println("If no deposit activity is recorded during this period, the account will be permanently closed in accordance with account maintenance policies.");
              System.out.println("Grace Period Remaining: --> " + (200-cuD));
              try{
                  DataOutputStream CU = new DataOutputStream(new FileOutputStream("Flag1.dat")); 
                  CU.writeBoolean(true);
                  CU.close();
                }
              catch(Exception el){}
            }
        }
    }
    /**
    * Stores the latest current-account balance.
    * Used after penalties or balance updates.
    */
    public void Balance() throws IOException{
        DataOutputStream out1 = new DataOutputStream(new FileOutputStream("bal.dat"));
        out1.writeDouble(BALANCE);
        out1.close();
    }
}