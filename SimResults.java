import java.util.LinkedList;
import java.util.Queue;

//Stores the results from each simulation run
//Calculate various stats for each run
public class SimResults {
	
	/* TURNAROUND time: amount of elapsed time from when a process
	 * enters the ready queue to when it completes execution */
	private double sumOfTurnaround;
	
	/* WAITING time: amount of time a process waits in the ready queue */
	private double sumOfWaiting;
	
	/* RESPONSE time: amount of elapsed time from when a request
	 * was submitted until the first response is produced */
	private double sumOfResponse;//response time
	
	/* THROUGHPUT: number of processes that complete their execution
	 * per time unit. */
	private int numOfCompletedProcesses;
	
	//ordered timeline for processes
	private Queue<String> timeline;
	
	public SimResults(){
		timeline = new LinkedList<String>();
	}
	
	public void setSumOfTurnaround(double sumOfTurnaround){
		this.sumOfTurnaround = sumOfTurnaround;
	}
	
	public void setSumOfWaiting(double sumOfWaiting){
		this.sumOfWaiting = sumOfWaiting;
	}
	
	public void setSumOfResponse(double sumOfResponse){
		this.sumOfResponse = sumOfResponse;
	}
	
	public void setNumOfCompletedProcesses(int numOfCompletedProcesses){
		this.numOfCompletedProcesses = numOfCompletedProcesses;
	}
	
	//Return average turnaround time
	public double getAverageTurnaround(){
		return sumOfTurnaround / numOfCompletedProcesses;
	}
	
	//return average waiting time
	public double getAverageWaiting(){
		return sumOfWaiting / numOfCompletedProcesses;
	}
	
	//return average reponse time
	public double getAverageResponse(){
		return sumOfResponse / numOfCompletedProcesses;
	}
	
	public int getThroughput(){
		return numOfCompletedProcesses;
	}
	
	//Return timeline queue
	public Queue<String> getTimeline(){
		return timeline;
	}
	
	//Add a process to the timeline
	public void addToTimeline(String p){
		timeline.add(p);
	}
	
	//Print timeline for the processes in order
	public void printTimeline(){
		int quanta = 1;
		for(String p : timeline){
			System.out.println("Quanta " + quanta + " : " + p);
			quanta++;
		}
	}
	
}
