package org.liverpool.manager;

import java.util.HashMap;
import org.liverpool.bootstrap.ExecuteBowtie2;
import org.liverpool.toolspecific.ProduceSequenceFilesForMatlab;

public class RunManager {

	public static String userFeedback="java -jar jar-location/longshadow.jar ";
	
    public static String runBowtie_Usage = "runBowtie -bowtie_home bt_home -bowtie_db_path bowtie_db -fastq_path fastq -sam_path sam -screenout_path screenout" +
			" -bowtie_script_file bowtie_script -bowtie_batch_run_output bowtie_batch_run -seed_step seed_step";
    
    public static String runBedTools_Usage = "runBedTools -bedtoolpath bedpath -referencefasta referencefile -inputsam samfile -outputbed bedfile -outputsamsequence samreads -outputrefsequence refreads";
    
    
    private HashMap<String, String> allFunctions = new HashMap<String, String>();
    
    public RunManager(){
    	this.allFunctions.put("runBowtie","ExecuteBowtie2");
    	this.allFunctions.put("runBedTools","ProduceSequenceFilesForMatlab");
    }
    
    /**
     * The main class to call other relevant classes
     * @param args
     */
	public static void main(String[] args) {
		
		try{
			RunManager rm = new RunManager();
			
			if((args.length < 1) || (!rm.allFunctions.containsKey(args[0]))){
				System.out.println("Allowed operations  :" + userFeedback + rm.allFunctions.keySet().toString());
				System.exit(0);
			}
			
			if(args[0].trim().equals("runBowtie")){
				if(args.length != 17){
					
					userFeedback+=runBowtie_Usage;
					System.out.println("Expected Usage for option - runBowtie");
					System.out.println(userFeedback);
					
				}else{
					String [] prog_args = new String[16];
					for (int i=1; i < args.length; i++)
						prog_args[i-1] = args[i];
					
					ExecuteBowtie2.main(prog_args);
				}
			}else if(args[0].trim().equals("runBedTools")){
				if(args.length != 13){
					
					userFeedback+=runBedTools_Usage;
					System.out.println("Expected Usage for option - runBedTools_Usage");
					System.out.println(userFeedback);
					
				}else{
					String [] prog_args = new String[16];
					for (int i=1; i < args.length; i++)
						prog_args[i-1] = args[i];
					
					ProduceSequenceFilesForMatlab.main(prog_args);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
