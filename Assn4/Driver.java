import java.util.LinkedList;
import java.util.Queue;

public class Driver {
	
	private static final int MEM_SIZE = 100; //main memory size is 100 MB
	private static final int NUM_OF_PROCESSES = 150; //arbitrary value
	private static final int PID_START = 10; //ID of first process
	private static final int RUNTIME = 60; //60 seconds = 1 minute
	private static final int SIM_RUNS = 5; // number of simulation runs
	
	//Create and return queue of random processes
	public static Queue<Process> generateProcesses(){
		Queue<Process> processes = new LinkedList<Process>();
		for(int i = 0; i < NUM_OF_PROCESSES; i++)
			processes.add(new Process("P" + (PID_START + i)));
		return processes;
	}
	
	//Print table of processes
	/*
	public static void printProcesses(Queue<Process> processes){
		System.out.println("PID   |  Size  |  Duration  ");
		System.out.println("---------------------------");
		for(Process p : processes)
			System.out.printf("%s  |  %3d   |  %4d\n", p.getPID(), p.getSize(), p.getDuration());
		System.out.println("\n\n");
	}
	 */
	
	public static void main (String[] args) {
		
		int firstFitSum = 0, nextFitSum = 0, bestFitSum = 0, worstFitSum = 0, tempNum = 0;
		for(int i = 0; i < SIM_RUNS; i++){
			Queue<Process> processes = generateProcesses();
			Swapping swap = new Swapping(processes, MEM_SIZE, RUNTIME);
			
			tempNum = swap.firstFit();
			System.out.println("\nFirst Fit:\n# of processes swapped in: " + tempNum + "\n\n");
			firstFitSum += tempNum;
			
			tempNum = swap.nextFit();
			System.out.println("\nNext Fit:\n# of processes swapped in: " + tempNum + "\n\n");
			nextFitSum += tempNum;
			
			tempNum = swap.bestFit();
			System.out.println("\nBest Fit:\n# of processes swapped in: " + tempNum + "\n\n");
			bestFitSum += tempNum;
			
			tempNum = swap.worstFit();
			System.out.println("\nWorst Fit:\n# of processes swapped in: " + tempNum + "\n\n");
			worstFitSum += tempNum;
		}
		
		System.out.println("SWAPPING RESULTS\n--------------------");
		System.out.println("First Fit - AVG # of processes swapped in: " + firstFitSum / SIM_RUNS);
		System.out.println("Next Fit  - AVG # of processes swapped in: " + nextFitSum / SIM_RUNS);
		System.out.println("Best Fit  - AVG # of processes swapped in: " + bestFitSum / SIM_RUNS);
		System.out.println("Worst Fit - AVG # of processes swapped in: " + worstFitSum / SIM_RUNS);
		
		/*
		
		processes = generateProcesses();
		printProcesses(processes);
		
		Paging page = new Paging(processes, MEM_SIZE);
		page.FIFO();
		page.LFU();
		page.LRU();
		page.MFU();
		page.randomPick();*/
		
		
	}
	
	
}
