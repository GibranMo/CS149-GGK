import java.util.Random;

public class Process {
	
	//Possible sizes and durations in MB and seconds (respectively)
	private int[] sizes = {2,3,4,5};//{5, 11, 17, 31};
	private int[] durations = {1, 2, 3, 4, 5};
	
	private String pID; //Process ID
	private int size; //Process size
	private int duration; //Process duration
	private int firstPartition; //starting partition
	private int lastPartition; //ending partition
	
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
	
	public void setFirstPartition(int firstPartition){
		this.firstPartition = firstPartition;
	}
	
	public void setLastPartition(int lastPartition){
		this.lastPartition = lastPartition;
	}
	
	public int getFirstPartition(){
		return firstPartition;
	}
	
	public int getLastPartition(){
		return lastPartition;
	}
	public Process getClone(){
		Process p = new Process(this.pID);
		p.duration = this.duration;
		p.size = this.size;
		p.firstPartition = this.firstPartition;
		p.lastPartition = this.lastPartition;
		return p;
	}
}
