import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Quanta {
	
	/*We decided, 1 quanta = 1000 millisecond = 1 second
	 * This way, if we generate a process with a random value of 0.1 quanta,
	 * it will not be too small for the scheduling and timers of the computer.
	 * With this set value, the smallest possible quanta is 100 milliseconds or .1 seconds,
	 * which is manageable. 
	*/
	//private float timeSlice = 1000; //milliseconds
	//private float testTimeSlice = 5;
	
	private Process quantasProcess;
	
	public Quanta(Process p) {
		
		quantasProcess = p;
	}
	
	//This just really means run the process assigned to this quanta.
	public void runQuanta(){
		
		quantasProcess.runProcess();
		
	}
	

}
