import java.util.LinkedList;
import java.util.Queue;


public class MemManager {

	private static final int MEM_SIZE = 100; //main memory size, 100MB
	
	private SwappingAPI swapping; //swapping algorithms
	private PagingAPI paging; //paging algorithms
	private Queue<Process> readyQueue; //ready queue of processes
	private LinkedList<Integer> mainMem; //main memory
	
	public MemManager(Queue<Process> readyQueue){
		
		this.readyQueue = readyQueue;
		
		swapping = new SwappingAPI();
		paging = new PagingAPI();
		
		//Initialize main memory and set each unit to free
		mainMem = new LinkedList<Integer>();
		for(int i = 0; i < MEM_SIZE; i++)
			mainMem.add(0); //0 for free,  1 for allocated
		
	}
	
	public void run(){
		
	}
}
