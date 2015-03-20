import java.util.*;


public class Paging {
	private static final int PHYS_MEM_SIZE = 4;
	private static final int VIR_MEM_SIZE = 10;
	private static final int PAGE_REF_TOTAL = 100;
	private HashMap<Integer, Boolean> pageTable;
	
	public Paging() {
		pageTable = new HashMap<Integer, Boolean>(VIR_MEM_SIZE);
		for (int i = 0; i < VIR_MEM_SIZE; i++) {
			pageTable.put(i, false);
		}
	}
	
	public void runFIFO() {
		//use queue if size is 4 do swap
		Queue<Integer> workingSet = new LinkedList<Integer>();
		
		Random rand = new Random();
		int reference = rand.nextInt(VIR_MEM_SIZE);
		workingSet.offer(reference);
		
		// print
		System.out.println("Running FIFO Paging Algorithm.");
		String append = "\tPAGE FAULT:  Page " + reference + " added.";
		print(new ArrayList<Integer>(workingSet), append);
		
		for (int refCount = 1; refCount < PAGE_REF_TOTAL; refCount++) {
			reference = nextReference(reference);
			
			if (workingSet.size() < PHYS_MEM_SIZE) {
				workingSet.offer(reference);
				
				// print
				append = "\tPAGE FAULT:  Page " + reference + " added.";
				print(new ArrayList<Integer>(workingSet), append);
			}
			else if (workingSet.contains(reference)) {
				// print
				print(new ArrayList<Integer>(workingSet), "");
			}
			else {
				// swap
				int oldRef = workingSet.poll();
				workingSet.offer(reference);
				
				// print
				append = "\tPAGE FAULT:  Page " + oldRef + " evicted. Replaced with " + reference + ".";
				print(new ArrayList<Integer>(workingSet), append);
			}
		}
		
		System.out.println();
	}
	
	public void runLRU() {
		// map physmem to age and update age beginning of each iteration
		HashMap<Integer, Integer> workingSet = new HashMap<Integer, Integer>();
		
		Random rand = new Random();
		int reference = rand.nextInt(VIR_MEM_SIZE);
		workingSet.put(reference, 0);	// store reference as key and age as value
		
		// print
		System.out.println("Running LRU Paging Algorithm.");
		String append = "\tPAGE FAULT:  Page " + reference + " added.";
		print(new ArrayList<Integer>(workingSet.keySet()), append);
		
		for (int refCount = 1; refCount < PAGE_REF_TOTAL; refCount++) {
			reference = nextReference(reference);
			
			// update the ages of each entry
			for (int key : workingSet.keySet()) {
				int age = workingSet.get(key);
				workingSet.put(key, ++age);
			}
			
			if (workingSet.size() < PHYS_MEM_SIZE) {
				workingSet.put(reference, 0);	// store reference as key and age as value
				// print
				append = "\tPAGE FAULT:  Page " + reference + " added.";
				print(new ArrayList<Integer>(workingSet.keySet()), append);
			}
			else if (workingSet.keySet().contains(reference)) {
				// print
				print(new ArrayList<Integer>(workingSet.keySet()), "");
			}
			else {
				// find the entry with the highest age
				int highestAge = -1;
				int oldRef = -1;
				
				for (int key : workingSet.keySet()) {
					int age = workingSet.get(key);
					if (age > highestAge) {
						highestAge = age;
						oldRef = key;
					}
				}
				
				workingSet.remove(oldRef);
				workingSet.put(reference, 0);	// store reference as key and age as value
				
				// print
				append = "\tPAGE FAULT:  Page " + oldRef + " evicted. Replaced with " + reference + ".";
				print(new ArrayList<Integer>(workingSet.keySet()), append);
			}
		}
		
		System.out.println();
	}
	
	private int nextReference(int current) {
		Random rand = new Random();
		int delta, r = rand.nextInt(VIR_MEM_SIZE);
		
		if (r < 7)
			delta = rand.nextInt(3) - 1;
		else
			delta = rand.nextInt(VIR_MEM_SIZE - 3) + 2;
		
		if (delta < 0) delta += VIR_MEM_SIZE;  // to avoid negative values
		
		return (current + delta) % VIR_MEM_SIZE;
	}
	
	private void print(ArrayList<Integer> list, String appended) {
		StringBuilder sb = new StringBuilder();
		for (int ref : list) {
			sb.append(" " + ref);
		}
		
		sb.append(appended);
		System.out.println(sb.toString());
	}
}
