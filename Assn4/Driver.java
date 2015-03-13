import java.util.LinkedList;
import java.util.Queue;

public class Driver {
	
	private static final int NUM_OF_PROCESSES = 75; //arbitrary value
	private static final int PID_START = 100; //ID of first process
	
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
	}

	public static void main (String[] args) {
		Queue<Process> processes = generateProcesses();
		printProcesses(processes);
		
		MemManager memManager = new MemManager(processes);
		memManager.run();
	}
	
	
}
