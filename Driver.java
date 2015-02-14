import java.util.*;



public class Driver {
    //Number of run simulations
    private final static int NUM_OF_PROCESSES = 60;
    static ArrayList <Process> readyQueue = new ArrayList <Process> (NUM_OF_PROCESSES);
    //static ArrayList <Process> readyQueue = new ArrayList <Process> ();
    
    public static void main (String args []) {
        
        populateProcesses();
        
        
        printStuff(readyQueue);
        
        
        //Example of simulated run and results
        QuantaTimeLine run1 = new QuantaTimeLine();
        run1.FCFS(readyQueue);
        printResults(run1.getFCFSResults());
        /* Not implemented yet
         run1.SJF(readyQueue);
         run1.SRT(readyQueue);
         run1.RR(readyQueue);
         run1.HPFNonPre(readyQueue);
         run1.HPFPre(readyQueue);
         */
        run1.SJF(readyQueue);
        printResults(run1.getSJFResults());
        /*
         printResults(run1.getSJFResults());
         printResults(run1.getSRTResults());
         printResults(run1.getRRResults());
         */
    }
    
    public static void populateProcesses(){
        int i = 0;
        while (i < NUM_OF_PROCESSES )
        {
            Process newProcess = new Process("P" + String.format("%02d", i));
            readyQueue.add(newProcess);
            i++;
        }
    }
    
    //DebuggingFunction: right now prints arrival time of processes
    public static void printStuff(ArrayList<Process> processes)  {
        
        int sumExpRunTime  = 0;
        int i = 0;
        System.out.println("Name\tArrival Tm.\tExp. Rntm.\tPriority");
        System.out.println("--------------------------------------------------");
        while (i < processes.size())
        {
            
            float arrival = processes.get(i).getArrivalTime();
            float runTime = processes.get(i).getExpRunTime();
            int priority = processes.get(i).getPriority();
            String name = processes.get(i).getProcessName();
            sumExpRunTime += runTime;
            System.out.format("%s\t%08.5f\t%08.5f\t%d\n", name, arrival, runTime, priority);
            i++;
            
        }
        
        System.out.println("\nTotal runtime: "+ sumExpRunTime + "\n\n");
    }
    
    //Print function to output simulation results and statistics
    public static void printResults(SimResults results){
        //print timeline
        System.out.println("\nTime line: ");
        results.printTimeline();
        System.out.println("\n");
        
        //print calculated stats
        System.out.println("Average turnaround time:\t" + results.getAverageTurnaround());
        System.out.println("Average waiting time:\t\t" + results.getAverageWaiting());
        System.out.println("Average response time:\t\t" + results.getAverageResponse());
        System.out.println("Throughput:\t\t\t" + results.getThroughput());
    }
    
}
