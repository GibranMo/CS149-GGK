import java.util.Random;

public class Process {
    
    private float arrivalTime; // 0-99 quanta
    private float expRunTime; // 0.1 - 10 quanta
    private int priority; // Lowest-1,2,3,4-Highest
    private String processName;//process name
    
    private int pausedTime;//used for preemptive algorithms
    
    public Process(String processName){
        this.processName = processName;
    	
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
        double upperArr = 99;//99
        double resultArr = Math.random() * (upperArr - lowerArr) + lowerArr;
        
        this.arrivalTime = (float) resultArr;
    }
    
    //Deep copy constructor
    public Process(Process p){
    	this.arrivalTime = p.arrivalTime;
    	this.expRunTime = p.expRunTime;
    	this.priority = p.priority;
    	this.processName = p.processName;
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
    
    public String getProcessName(){
    	return processName;
    }
    
    public void setArrivalTime(float arrivalTime){
    	this.arrivalTime = arrivalTime;
    }
    
    public void setPriority(int priority){
    	this.priority = priority;
    }
    
    public void setExpRunTime(float expRunTime){
    	this.expRunTime = expRunTime;
    }
    
    /* THE FOLLOWING METHODS ARE USED FOR PREEMPTIVE ALGORITHMS */
   
    //decrement run time
    public void decrementExpRunTime(){
    	this.expRunTime--;
    }
    
    //job has been suspended, set pause time
    public void pause(int pausedTime){
    	this.pausedTime = pausedTime;
    }
    
    //job resume, reset pause time to 0
    public void resume(){
    	this.pausedTime = 0;
    }
    
    //return paused time
    public int getPausedTime(){
    	return pausedTime;
    }
    
    //check if job was previously suspended
    public boolean isPaused(){
    	if(pausedTime > 0)
    		return true;
    	return false;
    }
}

