import java.util.*;


public class Paging {
    private static final int PHYS_MEM_SIZE = 4;
    private static final int VIR_MEM_SIZE = 10;
    private static final int PAGE_REF_TOTAL = 100;
    private static HashMap<Integer, Boolean> pageTable;
    
    private static ArrayList <Page> physicalMem;
    
    
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
        int hitCount = 0;
        for (int refCount = 1; refCount < PAGE_REF_TOTAL; refCount++) {
            reference = nextReference(reference);
            
            if (workingSet.size() < PHYS_MEM_SIZE) {
                workingSet.offer(reference);
                
                // print
                append = "\tPAGE FAULT:  Page " + reference + " added.";
                print(new ArrayList<Integer>(workingSet), append);
            }
            else if (workingSet.contains(reference)) { //Gibran:  I am guessing the hit area is here
                // print
                print(new ArrayList<Integer>(workingSet), "");
                hitCount++;
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
        
        System.out.println("\nHit Ratio: " + hitCount + " percent\n");
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
        int hitCount = 0;
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
            else if (workingSet.keySet().contains(reference)) { //Gibran: I am guessing the hit area is here
                // print
                print(new ArrayList<Integer>(workingSet.keySet()), "");
                
                
                hitCount++;
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
        
        System.out.println("\nHit Ratio: " + hitCount + " percent\n");
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
    ///////////////////////////////////////////////
    
    
    //Gibran
    public void LFU(){
        
        System.out.println("\n\nRunning LFU Paging Algorithm.");
        System.out.println("------------------------------");
        physicalMem = new ArrayList <Page> ();
        pageTable = initializeTable();
        
        /* Create and populate disk*/
        ArrayList<Page> disk = new ArrayList<Page> ();
        
        //Constructing a page with its ID -'i', thus, initially page IDs are organized in ascending in the disk.
        for(int i = 0; i < VIR_MEM_SIZE; i++)
            disk.add(new Page (i));
        
        //For Mapping Page Number-Number of References  <Page, count>
        Map <Integer, Integer> referenceCountTracker = initializeCount();;
								
        int hitCount = 0; //Number of times the desired page is already in physical memory
        
        Random rand = new Random();
        int initalRandomNum = rand.nextInt(VIR_MEM_SIZE);
        int nextPageNum = initalRandomNum;
        
        int i = 0;
        while (i < PAGE_REF_TOTAL ) {
            nextPageNum = getRandomRefIndex(nextPageNum);
            
            int pageRefCount = referenceCountTracker.get(nextPageNum);
            referenceCountTracker.put(nextPageNum, pageRefCount++);
            
            /////////////////
            disk.get(nextPageNum).incrementCount();
            /////////////////
            
            if (isInMemory(nextPageNum)) {	//Already in physical memory
                
                hitCount++;
                print3();
                System.out.println();
                
            }
            else {  //it's not in physical memory
                
                if (physicalMem.size() < PHYS_MEM_SIZE) { //load if there is space in physical memory
                    pageTable.put(nextPageNum, true); //update table indicating this page is in physical memory
                    //physicalMem.add(p);    //finally, add page to physical memory
                    
                    physicalMem.add(disk.get(nextPageNum));
                    
                    print3();
                    System.out.println("     PAGE FAULT: Page "+ nextPageNum + " added.");
                    
                }
                else{ //evict a page
                    
                    int evictIndex = LFUindex (physicalMem);
                    
                    int pageTableIndex = physicalMem.get(evictIndex).getId();
                    physicalMem.remove(evictIndex);
                    
                    pageTable.put(pageTableIndex, false);
                    pageTable.put(nextPageNum, true);
                    physicalMem.add(disk.get(nextPageNum));
                    
                    print3();
                    System.out.print("      PAGE FAULT: Page "+ pageTableIndex + " evicted.");
                    System.out.println(" Replaced with " + nextPageNum);
                    //System.out.println("     PAGE FAULT: Page "+ nextPageNum + " added.");
                    
                }
            }
            i++;
        }
        
        System.out.println("\nHit Ratio: " + hitCount + " percent");
    }/* End of LFU() */
    
    public static void print3() {
        
        for (int i = 0; i < physicalMem.size(); i++)
            System.out.print(" " + physicalMem.get(i).pageNumber);
        
        
    }
    
    private static HashMap<Integer, Boolean> initializeTable() {
        
        HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>(VIR_MEM_SIZE);
        for (int i = 0; i < VIR_MEM_SIZE; i++) {
            map.put(i, false);
        }
        return map;
    }
    
    private static boolean isInMemory(int pageNum) {
        
        if (pageTable.get(pageNum) == true)
            return true;
        return false;
        
    }
    
    
    /* Gibran: For evicting the right page
     */
    private static int LFUindex(ArrayList <Page> pagesInPhyMem) {
        
        int minVal = pagesInPhyMem.get(0).getRefCount();
        int index = 0;
        for (int i = 1; i < pagesInPhyMem.size(); i++) {
            if (minVal > pagesInPhyMem.get(i).getRefCount()) { //each Page knows its own reference count
                minVal = pagesInPhyMem.get(i).getRefCount();
                //need to remove this index
                index = i;
            }
        }
        //returning index of Page in Physical Memory that needs to be evicted
        return index;
    }
    
    /* Gibran: initialize map count to all 0's
     */
    private static Map <Integer, Integer> initializeCount() {
        
        Map <Integer, Integer> map = new HashMap <Integer, Integer> (VIR_MEM_SIZE);
        
        for (int i =0; i < VIR_MEM_SIZE; i++)
            map.put(i, 0);
        
        return map;
    }
    
    
    /* Gibran: The DELTA i part from the slides
     */
    public static int getRandomRefIndex(int ithReference) {
        
        Random rand = new Random();
        int DELTAi = 0;
        //As per the slides,"first generate the random numer 'r'"
        int r = rand.nextInt(VIR_MEM_SIZE); //need plus one to get right limit (9 + 1)
        
        if (r >= 0 && r < 7 ){
            int [] rangeNumbers = {-1,0, 1};
            //Generate the second random number
            int randNum = rand.nextInt(3);
            DELTAi = rangeNumbers[randNum];
            
        }
        else{
            DELTAi = rand.nextInt((8-2) + 1) + 2;
        }
        
        //System.out.println("ithReference: " + ithReference + " DELTAi: " + DELTAi);
        int nextIvalue = (ithReference + DELTAi) % 10;
        
        if (nextIvalue == -1)
            nextIvalue = 9;
        
        return nextIvalue;
        
    } /*END of getRandomRefIndex()  */
    
    public void MFU(){
        
        System.out.println("\n\nRunning MFU Paging Algorithm.");
        System.out.println("------------------------------");
        physicalMem = new ArrayList <Page> ();
        pageTable = initializeTable();
        
        /* Create and populate disk*/
        ArrayList<Page> disk = new ArrayList<Page> ();
        
        //Constructing a page with its ID -'i', thus, initially page IDs are organized in ascending in the disk.
        for(int i = 0; i < VIR_MEM_SIZE; i++)
            disk.add(new Page (i));
        
        //For Mapping Page Number-Number of References  <Page, count>
        Map <Integer, Integer> referenceCountTracker = initializeCount();;
								
        int hitCount = 0; //Number of times the desired page is already in physical memory
        
        Random rand = new Random();
        int initalRandomNum = rand.nextInt(VIR_MEM_SIZE);
        int nextPageNum = initalRandomNum;
        
        int i = 0;
        while (i < PAGE_REF_TOTAL ) {
            nextPageNum = getRandomRefIndex(nextPageNum);
            
            int pageRefCount = referenceCountTracker.get(nextPageNum);
            referenceCountTracker.put(nextPageNum, pageRefCount++);
            
            /////////////////
            disk.get(nextPageNum).incrementCount();
            /////////////////
            
            if (isInMemory(nextPageNum)) {	//Already in physical memory
                
                hitCount++;
                print3();
                System.out.println();
                
            }
            else {  //it's not in physical memory
                
                if (physicalMem.size() < PHYS_MEM_SIZE) { //load if there is space in physical memory
                    pageTable.put(nextPageNum, true); //update table indicating this page is in physical memory
                    //physicalMem.add(p);    //finally, add page to physical memory
                    
                    physicalMem.add(disk.get(nextPageNum));
                    
                    print3();
                    System.out.println("     PAGE FAULT: Page "+ nextPageNum + " added.");
                    
                }
                else{ //evict a page
                    
                    int evictIndex = MFUindex (physicalMem);
                    
                    int pageTableIndex = physicalMem.get(evictIndex).getId();
                    physicalMem.remove(evictIndex);
                    
                    pageTable.put(pageTableIndex, false);
                    pageTable.put(nextPageNum, true);
                    physicalMem.add(disk.get(nextPageNum));
                    
                    print3();
                    System.out.print("      PAGE FAULT: Page "+ pageTableIndex + " evicted.");
                    System.out.println(" Replaced with " + nextPageNum);
                }
            }
            i++;
        }
        
        System.out.println("\nHit Ratio: " + hitCount + " percent");
        
    }/* End of MFU() */
    
    private static int MFUindex(ArrayList <Page> pagesInPhyMem) {
        
        int maxVal = pagesInPhyMem.get(0).getRefCount();
        int index = 0;
        for (int i = 1; i < pagesInPhyMem.size(); i++) {
            if (maxVal < pagesInPhyMem.get(i).getRefCount()) { //each Page knows its own reference count
                maxVal = pagesInPhyMem.get(i).getRefCount();
                //need to remove this index
                index = i;
            }
        }
        //returning index of Page in Physical Memory that needs to be evicted
        return index;
    }
    
    
    //Gibran:
    public void randomPick(){
        
        System.out.println("\n\nRunning Random Pick Paging Algorithm.");
        System.out.println("------------------------------");
        physicalMem = new ArrayList <Page> ();
        pageTable = initializeTable();
        
        /* Create and populate disk*/
        ArrayList<Page> disk = new ArrayList<Page> ();
        
        //Constructing a page with its ID -'i', thus, initially page IDs are organized in ascending in the disk.
        for(int i = 0; i < VIR_MEM_SIZE; i++)
            disk.add(new Page (i)); 
        
        //For Mapping Page Number-Number of References  <Page, count>
        Map <Integer, Integer> referenceCountTracker = initializeCount();;
								
        int hitCount = 0; //Number of times the desired page is already in physical memory
        
        Random rand = new Random();
        int initalRandomNum = rand.nextInt(VIR_MEM_SIZE);
        int nextPageNum = initalRandomNum;
        
        int i = 0;
        while (i < PAGE_REF_TOTAL ) {		
            nextPageNum = getRandomRefIndex(nextPageNum);
            
            int pageRefCount = referenceCountTracker.get(nextPageNum);
            referenceCountTracker.put(nextPageNum, pageRefCount++);   
            
            /////////////////
            disk.get(nextPageNum).incrementCount();
            /////////////////
            
            if (isInMemory(nextPageNum)) {	//Already in physical memory
                
                hitCount++;	
                print3();
                System.out.println();
                
            }
            else {  //it's not in physical memory
                
                if (physicalMem.size() < PHYS_MEM_SIZE) { //load if there is space in physical memory
                    pageTable.put(nextPageNum, true); //update table indicating this page is in physical memory
                    //physicalMem.add(p);    //finally, add page to physical memory
                    
                    physicalMem.add(disk.get(nextPageNum));
                    
                    print3();
                    System.out.println("     PAGE FAULT: Page "+ nextPageNum + " added.");
                    
                }
                else{ //evict a page
                    
                    //get a random index for evicting
                    int randomIndex = rand.nextInt(PHYS_MEM_SIZE);
                    
                    int pageTableIndex = physicalMem.get(randomIndex).getId();
                    physicalMem.remove(randomIndex);
                    
                    pageTable.put(pageTableIndex, false);
                    pageTable.put(nextPageNum, true);
                    physicalMem.add(disk.get(nextPageNum));
                    
                    print3();
                    System.out.print("      PAGE FAULT: Page "+ pageTableIndex + " evicted.");
                    System.out.println(" Replaced with " + nextPageNum);
                }				
            }
            i++;
        }
        
        System.out.println("\nHit Ratio: " + hitCount + " percent");
        
    } /* End of randomPick()*/
    
    
    private class Page{
        int pageNumber;
        int referenceCount;
        Page (int n){
            this.pageNumber = n;
            referenceCount = 0;
        }
        public int getId(){
            return pageNumber;
        }
        
        public int getRefCount(){
            return referenceCount;
        }
        
        public void incrementCount() {
            referenceCount++;
        }
        
    }
}
