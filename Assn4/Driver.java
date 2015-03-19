import java.util.LinkedList;
import java.util.Queue;

public class Driver {
	
	private static final int MEM_SIZE = 100; //main memory size is 100 MB
	private static final int NUM_OF_PROCESSES = 50; //arbitrary value
	private static final int PID_START = 10; //ID of first process
	private static final int RUNTIME = 60; //60 seconds = 1 minute
	
	//Create and return queue of random processes
	public static Queue<Process> generateProcesses(){
		Queue<Process> processes = new LinkedList<Process>();
		for(int i = 0; i < NUM_OF_PROCESSES; i++)
			processes.add(new Process("P" + (PID_START + i)));
		return processes;
	}
	
	//Print table of processes
	public static void printProcesses(Queue<Process> processes){
		System.out.println("PID   |  Size  |  Duration  ");
		System.out.println("---------------------------");
		for(Process p : processes)
			System.out.printf("%s  |  %3d   |  %4d\n", p.getPID(), p.getSize(), p.getDuration());
		System.out.println("\n\n");
	}

	public static void main (String[] args) {
		
		Queue<Process> processes = generateProcesses();
		printProcesses(processes);
		
		Swapping swap = new Swapping(processes, MEM_SIZE, RUNTIME);
		swap.firstFit();
		/*swap.nextFit();
		swap.bestFit();
		swap.worstFit();
		
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
