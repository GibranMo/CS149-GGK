import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Process {
    
    private float arrivalTime; // 0-99 quanta
    private float expRunTime; // 0.1 - 10 quanta
    private int priority; // Lowest-1,2,3,4-Highest
    
    public Process(){
        
        //give a random priority
        Random rm = new Random();
        this.priority = rm.nextInt(4- 1 + 1) + 1;
        
        //give a random expected expected Run Time
        double lower = 0.10;
        double upper = 10.0;
        double result = Math.random() * (upper - lower) + lower;
        
        this.expRunTime = (float) result;
        
        //give a Random arrival Time
        double lowerArr = 0;
        double upperArr = 99;
        double resultArr = Math.random() * (upperArr - lowerArr) + lowerArr;
        
        this.arrivalTime = (float) resultArr;
        
    }
    
    public float getExpRunTime() {
        
        
        return expRunTime;
    }
    
    public float getArrivalTime(){
        
        return arrivalTime;
        
    }
    
    public int getPriority() {
        
        return priority;
    }
    
    
}

