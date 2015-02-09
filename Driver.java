import java.util.*;
import java.util.Comparator;



public class Driver {
    
    static int numberOfProcesses = 75; // arbitrary ?
    static ArrayList <Process> setOfProcesses = new ArrayList <Process> (numberOfProcesses);
    static ArrayList <Process> readyQueue = new ArrayList <Process> ();
    
    
    public static void main (String args []) {
        
        populateProcesses();
        
        //sorts setOfProcesses
        sortProcesses();
        
        readyQueue = setOfProcesses;
        
        printStuff();
        
        QuantaTimeLine qt = new QuantaTimeLine();
        qt.FCFS(readyQueue);
    }
    
    public static void sortProcesses() {
        //Implement Comparator Interface so I can sort Processes by Arrival Time
        Collections.sort(setOfProcesses, new Comparator<Process> () {
            @Override public int compare(Process p1, Process p2) {
                return Float.compare(p1.getArrivalTime(), p2.getArrivalTime());
            }
        });
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
        
        int sumExpRunTime  = 0;
        int i = 0;
        System.out.println("Arrival T.\t\tExp. Rt.");
        while (i < setOfProcesses.size())
        {
            
            float a = setOfProcesses.get(i).getArrivalTime();
            float b = setOfProcesses.get(i).getExpRunTime();
            sumExpRunTime += b;
            System.out.println(i + ". " + a + "\t\t" + b);
            i++;
            
        }
        
        System.out.println("\n\nTotal Exp. Runtime: "+ sumExpRunTime);
    }
    
}
