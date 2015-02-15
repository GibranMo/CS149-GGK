import java.util.*;



public class Driver {
    //Number of run simulations
	private final static int NUM_OF_SIMULATION = 5;
    private final static int NUM_OF_PROCESSES = 75;//75;
    private static ArrayList <Process> readyQueue;
    
    public static void main (String args []) {
    	
    	testRun();
    	
    	/*
    	//Used to calculate sum of overall stats
    	OverallResults FCFSOverallResults = new Driver.OverallResults();
    	OverallResults SJFOverallResults = new Driver.OverallResults();
    	OverallResults SRTOverallResults = new Driver.OverallResults();
    	OverallResults RROverallResults = new Driver.OverallResults();
    	OverallResults HPFPreOverallResults = new Driver.OverallResults();
    	OverallResults HPFNonPreOverallResults = new Driver.OverallResults();
    	
    	//Run simulations
    	for(int i = 1; i <= NUM_OF_SIMULATION; i++){
    		//Generate processes and print
            populateProcesses();
            System.out.println("Random Generated Processes:\n");
            printStuff(readyQueue);
            
            System.out.println("SIMULATION " + i + "\n____________\n");
            QuantaTimeLine run = new QuantaTimeLine();
            
            //Run all algorithms
            run.FCFS(readyQueue);
            run.SJF(readyQueue);
            run.SRT(readyQueue);
            run.RR(readyQueue);
            run.HPFPre(readyQueue);
            run.HPFNonPre(readyQueue);
            
            //Output results for all algorithms
            System.out.println("FCFS Results:");
            printResults(run.getFCFSResults());
            System.out.println("SJF Results:");
            printResults(run.getSJFResults());
            System.out.println("SRT Results:");
            printResults(run.getSRTResults());
            System.out.println("RR Results:");
            printResults(run.getRRResults());
            System.out.println("HPF Preemptive Results:");
            printResults(run.getHPFPreResults());
            System.out.println("HPF Non-preemptive Results:");
            printResults(run.getHPFNonPreResults());
            
            //Save results for all algorithms to calculate overall stats
            FCFSOverallResults = setOverallStats(FCFSOverallResults, run.getFCFSResults());
            SJFOverallResults = setOverallStats(SJFOverallResults, run.getSJFResults());
            SRTOverallResults = setOverallStats(SRTOverallResults, run.getSRTResults());
            RROverallResults = setOverallStats(RROverallResults, run.getRRResults());
            HPFPreOverallResults = setOverallStats(HPFPreOverallResults, run.getHPFPreResults());
            HPFNonPreOverallResults = setOverallStats(HPFNonPreOverallResults, run.getHPFNonPreResults());
    	}
    	
    	//Print overall average stats
    	System.out.println("\n\nOVERALL SIMULATION RESULTS\n___________________________\n");
    	System.out.println("FCFS AVG Turnaround Time:\t\t" + FCFSOverallResults.getAvgTurnaroundTime());
    	System.out.println("FCFS AVG Waiting Time:\t\t\t" + FCFSOverallResults.getAvgWaitingTime());
    	System.out.println("FCFS AVG Response Time:\t\t\t" + FCFSOverallResults.getAvgResponsetime());
    	System.out.println("FCFS AVG Throughput:\t\t\t" + FCFSOverallResults.getAvgThroughput());
    	System.out.println("\nSJF AVG Turnaround Time:\t\t" + SJFOverallResults.getAvgTurnaroundTime());
    	System.out.println("SJF AVG Waiting Time:\t\t\t" + SJFOverallResults.getAvgWaitingTime());
    	System.out.println("SJF AVG Response Time:\t\t\t" + SJFOverallResults.getAvgResponsetime());
    	System.out.println("SJF AVG Throughput:\t\t\t" + SJFOverallResults.getAvgThroughput());
    	System.out.println("\nSRT AVG Turnaround Time:\t\t" + SRTOverallResults.getAvgTurnaroundTime());
    	System.out.println("SRT AVG Waiting Time:\t\t\t" + SRTOverallResults.getAvgWaitingTime());
    	System.out.println("SRT AVG Response Time:\t\t\t" + SRTOverallResults.getAvgResponsetime());
    	System.out.println("SRT AVG Throughput:\t\t\t" + SRTOverallResults.getAvgThroughput());
    	System.out.println("\nRR AVG Turnaround Time:\t\t\t" + RROverallResults.getAvgTurnaroundTime());
    	System.out.println("RR AVG Waiting Time:\t\t\t" + RROverallResults.getAvgWaitingTime());
    	System.out.println("RR AVG Response Time:\t\t\t" + RROverallResults.getAvgResponsetime());
    	System.out.println("RR AVG Throughput:\t\t\t" + RROverallResults.getAvgThroughput());
    	System.out.println("\nHPF Preemptive AVG Turnaround Time:\t" + HPFPreOverallResults.getAvgTurnaroundTime());
    	System.out.println("HPF Preemptive AVG Waiting Time:\t" + HPFPreOverallResults.getAvgWaitingTime());
    	System.out.println("HPF Preemptive AVG Response Time:\t" + HPFPreOverallResults.getAvgResponsetime());
    	System.out.println("HPF Preemptive AVG Throughput:\t\t" + HPFPreOverallResults.getAvgThroughput());
    	System.out.println("\nHPF Non-Preemptive AVG Turnaround Time: " + HPFNonPreOverallResults.getAvgTurnaroundTime());
    	System.out.println("HPF Non-Preemptive AVG Waiting Time:\t" + HPFNonPreOverallResults.getAvgWaitingTime());
    	System.out.println("HPF Non-Preemptive AVG Response Time:\t" + HPFNonPreOverallResults.getAvgResponsetime());
    	System.out.println("HPF Non-Preemptive AVG Throughput:\t" + HPFNonPreOverallResults.getAvgThroughput());*/
    	
    }
    
    //Helper function to sum up the stats for each run
    public static Driver.OverallResults setOverallStats(OverallResults overallResults, SimResults result){
    	overallResults.turnaroundTime += result.getAverageTurnaround();
    	overallResults.responseTime += result.getAverageResponse();
    	overallResults.waitingTime += result.getAverageWaiting();
    	overallResults.throughput += result.getThroughput();
    	return overallResults;
    }
    
    public static void populateProcesses(){
        int i = 0;
        readyQueue = new ArrayList<Process>();
        while (i < NUM_OF_PROCESSES )
        {
            Process newProcess = new Process("P" + String.format("%02d", i));
            readyQueue.add(newProcess);
            i++;
        }
    }
    
    //DebuggingFunction: right now prints arrival time of processes
    //KEITH: I converted this method to a generic print method for any data structure
    public static void printStuff(Iterable<Process> processes)  {
        
        int sumExpRunTime  = 0;
        System.out.println("Name\tArrival Tm.\tExp. Rntm.\tPriority");
        System.out.println("--------------------------------------------------");
        for(Process p : processes)
        {
            float arrival = p.getArrivalTime();
            float runTime = p.getExpRunTime();
            int priority = p.getPriority();
            String name = p.getProcessName();
            sumExpRunTime += Math.ceil(runTime);
            System.out.format("%s\t%08.5f\t%08.5f\t%d\n", name, arrival, runTime, priority);
        }
        
        System.out.println("\nTotal runtime: "+ sumExpRunTime + "\n\n");
    }
    
    //Print function to output simulation results and statistics
    public static void printResults(SimResults results){
        //print timeline
        System.out.println("Time line: ");
        results.printTimeline();
        System.out.println();
        
        //print calculated stats
        System.out.println("Average turnaround time:\t" + results.getAverageTurnaround());
        System.out.println("Average waiting time:\t\t" + results.getAverageWaiting());
        System.out.println("Average response time:\t\t" + results.getAverageResponse());
        System.out.println("Throughput:\t\t\t" + results.getThroughput() + "\n\n");
    }
    
    //Nested class to save stats for overall stats calculations
    public static class OverallResults {
    	public double turnaroundTime;
    	public double waitingTime = 0.0;
    	public double responseTime = 0.0;
    	public int throughput = 0;
    	
    	public double getAvgTurnaroundTime(){
    		return turnaroundTime / (double)NUM_OF_SIMULATION;
    	}
    	
    	public double getAvgWaitingTime(){
    		return waitingTime / (double)NUM_OF_SIMULATION;
    	}
    	
    	public double getAvgResponsetime(){
    		return responseTime / (double)NUM_OF_SIMULATION;
    	}
    	
    	public double getAvgThroughput(){
    		return (double)throughput / (double)NUM_OF_SIMULATION;
    	}
    }
    
    public static ArrayList<Process> getTestProcesses(){
    	ArrayList<Process> processes = new ArrayList<Process>();
    	
    	Process p = new Process("P1");
    	
    	p.setArrivalTime(0);
    	p.setExpRunTime(10);
    	p.setPriority(3);
    	processes.add(p);
    	
    	p = new Process("P2");
    	p.setArrivalTime(0);
    	p.setExpRunTime(1);
    	p.setPriority(1);
    	processes.add(p);
    	
    	p = new Process("P3");
    	p.setArrivalTime(0);
    	p.setExpRunTime(2);
    	p.setPriority(3);
    	processes.add(p);
    	
    	p = new Process("P4");
    	p.setArrivalTime(0);
    	p.setExpRunTime(1);
    	p.setPriority(4);
    	processes.add(p);
    	
    	p = new Process("P5");
    	p.setArrivalTime(0);
    	p.setExpRunTime(5);
    	p.setPriority(2);
    	processes.add(p);
    	
    	return processes;
    }
    
    public static void testRun(){
		//Generate processes and print
        populateProcesses();
        //readyQueue = getTestProcesses(); 
        
        ArrayList <Process> queue = readyQueue;
        //First simulation run
        QuantaTimeLine run1 = new QuantaTimeLine();
        System.out.println("SRT");
        run1.SRT(queue);
        printResults(run1.getSRTResults());
        //System.out.println("RR");
        //run1.RR(queue);
        //printResults(run1.getRRResults());
    }
    
}
