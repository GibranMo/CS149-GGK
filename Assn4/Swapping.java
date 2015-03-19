import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Swapping {
	
	private int memSize; //main memory size
	private int runtime; //simulation run time
	private Queue<Process> readyQueue; //ready queue of processes

	public Swapping(Queue<Process> readyQueue, int memSize, int runtime){
		this.readyQueue = readyQueue;
		this.memSize = memSize;
		this.runtime = runtime;
	}
	
	//TODO: Keith
	//Finds the first hole big enough
	public void firstFit(){
		Queue<Process> tempReadyQueue = readyQueue;
		LinkedList<Partition> mainMem = createMemManager();
		Map<String, Process> inMem = new HashMap<String, Process>(); //memory tracker
		System.out.println("Time | Swap | PID | Size | Duration | Memory Map\n-------------------------------------------------");
		
		int currTime = 0;
		while(currTime < runtime && tempReadyQueue.size() > 0){
			
			//Decrease process running time and swap out any finished process
			for(int i = 0; i < mainMem.size(); i++){
				Partition partition = mainMem.get(i);
				if(!partition.isEmpty){
					Process p = inMem.get(partition.pid);
					if(p.getDuration() > 0){
						p.decrementDuration();
						if(p.getDuration() == 0){
							do{
								if(mainMem.get(i).pid.equals(p.getPID())){
									mainMem.get(i).clear();
								}else{
									break;
								}
								i++;
							}while(i < mainMem.size());
							print("out", currTime, p, mainMem);
							inMem.remove(p); //remove from memory tracker
							i--;
						}
					}
				}
				//System.out.println("count: " + i);
			}
			
			//Check for available partition hole
			int availableSize = 0;
			int pSize = tempReadyQueue.peek().getSize();
			boolean found = false;
			for(int i = 0; i < mainMem.size(); i++){
				
				int j = i;
				//If partition is empty
				if(mainMem.get(i).isEmpty){
					
					//Check consecutive partitions for a large enough hole
					for(j = i; j < mainMem.size(); j++){
						//Check if consecutive partitions are empty
						if(mainMem.get(j).isEmpty){
							availableSize++;
							//Set found to true if available size is found
							if(availableSize == pSize){
								found = true;
								break;
							}
						}
						else{
							//break since the hole is not big enough
							break;
						}
					}
				}
				
				//If hole is found, swap process into memory
				if(found){
					Process p = tempReadyQueue.poll();
					while(i <= j){
						mainMem.get(i).pid = p.getPID();
						mainMem.get(i).isEmpty = false;
						inMem.put(p.getPID(), p); // add to in memory tracker
						i++;
					}
					print("in", currTime, p, mainMem);
					break;
				}else{
					//skip to next possible hole
					i = j;
				}
			}
			
			//increment running time
			//print("", currTime, null, mainMem);
			currTime++;
			
		}
	}
	
	//TODO: Keith
	//Searches for the next hole, continuing from the previous search point
	public void nextFit(){
		
	}
	
	//TODO: Keith
	//Search for the smallest hole that is big enough
	public void bestFit(){
		
	}
	
	//TODO: Graeme
	public void worstFit(){
		
	}
	
	public void print(String event, int time, Process p, LinkedList<Partition> mainMem){
		//Get memory map
		StringBuilder memMap = new StringBuilder();
		for(Partition partition : mainMem){
			memMap.append(partition.pid + " ");
		}
		
		//Output process being swapped in or out
		//Output current memory map
		if(event.equals("in")){
			System.out.printf(" %02d  | in   | %s | %02d   | %d        | %s\n", time, p.getPID(), p.getSize(), p.getDuration(), memMap.toString());
		}else if(event.equals("out")){
			System.out.printf(" %02d  | out  | %s |      |          | %s\n", time, p.getPID(), memMap.toString());
		}else{
			System.out.printf(" %02d  |      |     |      |          | %s\n", time, memMap.toString());
		}
	}
	
	public LinkedList<Partition> createMemManager(){
		//Initialize main memory and set each unit to free
		LinkedList<Partition>mainMem = new LinkedList<Partition>();
		for(int i = 0; i < memSize; i++)
			mainMem.add(new Partition()); //0 for free,  1 for allocated
		return mainMem;
	}
	
	private class Partition{
		public boolean isEmpty;
		public String pid;
		public Partition(){
			isEmpty = true;
			pid = ".";
		}
		public void clear(){
			isEmpty = true;
			pid = ".";
		}
	}
}


