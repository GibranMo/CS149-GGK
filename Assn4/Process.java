import java.util.Random;

public class Process {
	
	//Possible sizes and durations in MB and seconds (respectively)
	private int[] sizes = {5, 11, 17, 31};
	private int[] durations = {1, 2, 3, 4, 5};
	
	private String pID; //Process ID
	private int size; //Process size
	private int duration; //Process duration
	
	public Process(String pID){
		this.pID = pID;

        Random rand = new Random();
        
        //Random size
        int index = rand.nextInt(4);
        this.size = sizes[index];
        
        //Random duration
        index = rand.nextInt(5);
        this.duration = durations[index];
	}
	
	//return process id
	public String getPID(){
		return pID;
	}
	
	//return process size
	public int getSize(){
		return size;
	}
	
	//return process duration
	public int getDuration(){
		return duration;
	}
	
	public void decrementDuration(){
		this.duration--;
	}
	
}
