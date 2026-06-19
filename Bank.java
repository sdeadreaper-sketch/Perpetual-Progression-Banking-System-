/**
 * #Bank.java
 * #Main entry point of the banking application.
 * *Handles:
 * - Welcome screen
 * - Main menu navigation
 * - Existing account authentication
 * - User query submission
 * - Routing to account-specific operations
 */

import java.util.*;
import java.io.*;
public class Bank{
    // #Global variables used for menu navigation, authentication and user input
    static Scanner Scr = new Scanner(System.in);
    static char choice1; static int enterPin =0; static String query; static char choice2; static int flag =-1; static int count =0;
    static int remain =4;
    /** #Displays the primary banking menu and validates user input */
    public static void mainChoice(){
        flag=-1; int c1;
        System.out.println("How may we assist you with your banking needs💰 today? \n[Please type a number from the given list of options:]");
        System.out.println(" 1. Open a New Account. \n 2. Revisit the Exisiting Account. \n 3. Request for Queries. \n 4. EXIT!");
        while(true){
           try{
             c1 = Scr.nextInt();
             Bank.choice1(c1); //calls for the choice entered by the user
           }
           catch(Exception e){
              System.out.println("Wrong Input, TRY AGAIN.[ENTER A NUMBER AS DISPLAYED....] ");
              Scr.nextLine();
           }
        }
    }
    /** #Routes the user to the selected banking service */
    public static void choice1(int n){
        bankAccount obj = new bankAccount();
        exisitingAccount obj1 = new exisitingAccount();
        switch(n){
            case 1:// Open account creation services
            System.out.println("We would like to know what type of new banking service you may need.");
            System.out.println(" A. Bank Account \n B. Loan[For now only Personal loan available.] \n C. Insurance Plan \n D. Go Back.");
            while(true){
              try{
                 choice1= Scr.next().charAt(0);
                 Scr.nextLine();
                 obj.accountDetail(choice1); //Calling the method in bankAccount class for the choices the user entered.
                 break;
              }
              catch(Exception e){
                 System.out.println("Wrong Input, TRY AGAIN.[ENTER AN ALPHABET AS DISPLAYED....] ");
                 Scr.nextLine();
              }
            }
            case 2: // Existing account login and authentication
            try{
             obj1.readAccount(); //Reading all the data in files for later use,like security pin here.
            }
            catch(Exception eel){}
            // Here the security is entered and is not executed the customer has logged in...
            if(flag==-1){ 
                System.out.println("PLEASE ENTER YOUR SECURITY PASSWORD:");
                enterPin = Scr.nextInt();
                Scr.nextLine();
                flag=0;
            }
            // Verify entered PIN before granting account access
            if(obj1.securityPin1 == enterPin){
               /**
               *  THIS CHECKS IF THE CURRENT ACCOUNT IS PERMANENTLY CLOSED OR NOT.....
               */
               if(obj1.balance1 ==0 && obj1.accountType1.equalsIgnoreCase("Current")){
                   currentAcc banOBJ =new currentAcc();
                   try{banOBJ.currentCheck();}
                   catch(Exception e){}
                   if(banOBJ.permaBAN){
                      System.out.println("LOGIN DENIED...");
                      System.out.println("This account has been permanently closed due to prolonged inactivity and a zero balance beyond the permitted 200-day grace period. \nNo further deposits, withdrawals, or account services are available. \nPlease contact the bank or create a new account for future banking services.");
                      mainChoice(); 
                   }
               }
               
               System.out.println("WELCOME BACK: " + obj1.name1 +", what would you like to do today.");
               System.out.println(" A. View Personal Detail. \n B. View Account Info. \n C. Deposit Money. \n D. Withdraw Money. \n E. Check Account Balance. \n F. Account History. \n G. Exit. \n H. Back to Main Menu.");
               while(true){
                 try{
                    choice2= Scr.nextLine().charAt(0);
                    obj1.reVisit(choice2); //Calling the method in exisitingAccount class for the choices the user entered.
                 }
                 catch(Exception e){
                    System.out.println("Wrong Input, TRY AGAIN.[ENTER AN ALPHABET AS DISPLAYED....] ");
                    Scr.nextLine();
                 }
               }
            }
            else{
                if(count ==3){ // Terminate session after three unsuccessful login attempts
                    System.out.println("TOO MANY TRIED ATTEMPTS ABORTING!!!!");
                    System.exit(0);
                }
                else{
                    remain = remain-1;
                    System.out.println("WRONG PASSWORD \nTRY AGAIN!! "+ (remain)+" CHANCE REMAINING....");
                    count += 1;
                    mainChoice();
                }
            }
            break;
            case 3: // Collect customer queries for future support response
            System.out.println("Please submit your Query, \nWe will reply shortly!!:");
            query = Scr.nextLine();
            break;
            case 4:
            System.exit(0);
            default:
            System.out.println("ENTERED WRONG OPTION! /n PLEASE TRY AGAIN.....");
            break;
        }
    }
    /** #Program entry point */
    public static void main(String args[]){ 
        dateRefer obj = new dateRefer();
        obj.timeCompute(); String greet = "";
        if(obj.time<12){ // Generate greeting based on current system time
            greet = "Good Morning!! It’s great to see you today.";
        }
        else if(obj.time>=12 && obj.time<16){
            greet = "Good Afternoon!! It’s great to see you today.";
        }
        else{
            greet = "Good Evening!! It’s great to see you today.";
        }
        // #Reset session flags when application starts
        try{
            DataOutputStream CU = new DataOutputStream(new FileOutputStream("Flag.dat")); 
            CU.writeBoolean(false);
            CU.close();
        }
        catch(Exception el){}
        try{
            DataOutputStream CU = new DataOutputStream(new FileOutputStream("Flag1.dat")); 
            CU.writeBoolean(false);
            CU.close();
        }
        catch(Exception el){}
        System.out.println(greet);
        System.out.println("😊Welcome to ALCAERZ BANK UNION, where your financial wellness is our priority.😊");
        // Launch main banking menu
        mainChoice();
    }
}