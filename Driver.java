import java.util.*;


public class Driver {
	
	static int numberOfProcesses = 30; // arbitrary ?
	public static ArrayList <Process> setOfProcesses = new ArrayList <Process> (numberOfProcesses);
	
	
	public static void main (String args []) {
		
		
		populateProcesses();
		
		int i = 0;
		while (i < setOfProcesses.size())
		{
			
			float a = setOfProcesses.get(i).getArrivalTime();
			
			System.out.println(i + ". " + a);
			i++;
			
		}
			
		
		
	}
	
	public static void populateProcesses(){
		
		int i = 0;
		while (i < numberOfProcesses )
		{
			Process newProcess = new Process();	
			setOfProcesses.add(newProcess);
			i++;
		}
		
	}
	

}
