/**
 * Utility class used to calculate the number of days
 * between a stored reference date and the current system date.
 *
 * This class is used throughout the banking system for:
 * - Savings account interest calculation
 * - Financial year tracking
 * - SFT suspension periods
 * - Current account penalty timers
 * - Grace period monitoring
 * - Account closure checks
 */
import java.util.*;
import java.io.*;
public class daysCal{
    //Global variables used to store days calculated and the store the file date in dd1, mm1, yy1.
    public int days; public double year;
    public int dd1, dd2, mm1, mm2, yy1, yy2; int n;
    
    /**
    * Calculates the number of completed days between the
    * reference date (dd1/mm1/yy1) and the current system date.
    *
    * The method automatically:
    * - Retrieves the current date from the system calendar
    * - Handles leap years
    * - Handles month transitions
    * - Handles year transitions
    * - Stores the final day difference in 'days'
    *
    * @throws IOException if an input/output operation fails
    */
    public void Cal() throws IOException{
        days =0;
        // Obtain the current system date
        Calendar cs1 = Calendar.getInstance();
        dd2 = cs1.get(Calendar.DATE);
        mm2 = cs1.get(Calendar.MONTH)+1;
        yy2 = cs1.get(Calendar.YEAR);
        int Arr[] = {31,28,31,30,31,30,31,31,30,31,30,31};  
        n = yy2 - yy1;
        if(mm2 < mm1)
           n = n-1;

        else if(mm2 == mm1 && dd2 < dd1)
           n = n-1;
        /** Case 1: Both dates belong to the same year */
        if(yy2-yy1==0){
            if(mm1==mm2){
                days = dd2-dd1;
                return;
            }
            // Leap year handling for February
            if(yy1%4 ==0){
                // Add days month by month until the target month is reached
                for(int i=mm1; i<mm2; i++){
                  if(i==2){
                      /** Days remaining in current month + days elapsed in target month plus the extra day in leap year */
                     days += (Arr[i-1] -dd1)+dd2 +1;
                     /** Current month has been processed,use dd2 as the new reference day */
                     dd1 =dd2;
                  }
                  else{
                     days += (Arr[i-1] -dd1)+dd2;
                     dd1 =dd2; 
                  }
                }
            }
            else{
               for(int i=mm1; i<mm2; i++){
                   /** Days remaining in current month + days elapsed in target month */
                  days += (Arr[i-1] -dd1)+dd2;
                  dd1 =dd2;
               }
            }
        }
        /** Case 2: Dates span across multiple years */
        else if(yy1 != yy2){
             outer:
             /** Process each year individually until the current year is reached */
             for(int i=yy1; i<=yy2; i++){
                 inner:
                 for(int j=mm1; j<=12; j++){
                     if(i%4 ==0){
                         /** If the year counter reaches the end of the year... */
                         if(j==12){
                             /** Days remaining in current month + days elapsed in target month */
                             days += Arr[j-1] - dd1 +dd2;
                             dd1= dd2;
                             //Resets the month counter for a new year...
                             mm1 =1;
                             break inner;
                            }
                         else if(j==2){
                             days += Arr[j-1] -dd1 +dd2 +1;
                             dd1=dd2;
                         }
                         else{
                             if((j==mm2) && (i==yy2)){
                                 break outer;
                             }
                             days += Arr[j-1] -dd1 +dd2;
                             dd1=dd2;
                         }
                     }
                     else{
                         if(j==12){
                             days += Arr[j-1] - dd1 +dd2;
                             dd1= dd2;
                             mm1 =1;
                             break inner;
                         }
                         else{
                             if((j==mm2) && (i==yy2)){
                                 break outer;
                             }
                             days += Arr[j-1] -dd1 +dd2;
                             dd1=dd2;
                         }
                     }
                 }
             }
        }
    }
}

/**
 * Important:
 * yy1 must contain the reference year and yy2 must contain
 * the current year. Reversing these values can produce
 * extremely large and incorrect day counts.
 */