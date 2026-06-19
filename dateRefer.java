/**
 * Utility class used to fetch the current
 * system date and time.
 */

import java.util.*;
public class dateRefer{
    // #Stores current date, time, day, month and year
    public int date; public int time; public int d;
    public int month; public int year;
    /**
     * Fetches the current system date and stores
     * day, month and year in class variables.
     */
    public void dateCompute(){
        Calendar cs = Calendar.getInstance();
        d = cs.get(Calendar.DATE);
        date = cs.get(Calendar.DATE);   //Current date
        month = cs.get(Calendar.MONTH) +1; // Month (Calendar starts from 0)
        year = cs.get(Calendar.YEAR);  // Current year
    }
    /**
     * Retrieves the current hour in 24-hour format.
     * @return current system hour
     */
    public int timeCompute(){
        Calendar cs = Calendar.getInstance();
        time = cs.get(Calendar.HOUR_OF_DAY);
        return time;
    }
}