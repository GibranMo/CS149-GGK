import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Quanta {
    
    private Process quantasProcess = null;
    
    public Quanta() {}
    
    public void assignProcess(Process p){
        
        quantasProcess = p;
    }
    
    //This just really means run the process assigned to this quanta.
    public void useUpQuanta(){
        
        //quantasProcess.runProcess();// Takes up the same amount of chunk/time every time
        
        float start = System.currentTimeMillis();
        float end = start + 3000; //
        
        while (System.currentTimeMillis() < end)
        {
            
        }	
        
    }
    
}
