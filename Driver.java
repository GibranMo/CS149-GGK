import java.util.*;
import java.util.Comparator;



public class Driver {
    
    static int numberOfProcesses = 30; // arbitrary ?
    public static ArrayList <Process> setOfProcesses = new ArrayList <Process> (numberOfProcesses);
    
    
    public static void main (String args []) {
        
        
        populateProcesses();
        
        
        //Implement Comparator Interface so I can sort Processes by Arrival Time
        Collections.sort(setOfProcesses, new Comparator<Process> () {
            @Override public int compare(Process p1, Process p2) {
                return Float.compare(p1.getArrivalTime(), p2.getArrivalTime());
            }
        });
        
        printStuff();
        
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
    
    //DebuggingFunction: right now prints arrival time of processes
    public static void printStuff() {
        
        int i = 0;
        while (i < setOfProcesses.size())
        {
            
            float a = setOfProcesses.get(i).getArrivalTime();
            
            System.out.println(i + ". " + a);
            i++;
            
        }
    }
    
}
