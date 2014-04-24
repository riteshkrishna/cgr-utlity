package org.liverpool.bootstrap;

/**
 * Execute Bowtie-2 in batch mode using a combination of parameters. The script further selects the parameters that
 * produce maximum percentage of total alignment and executes Bowtie-2 with those selected parameters to produce a
 * final SAM file for further processing. All the intermediate Bowtie-2 runs with different parameters are logged
 * in the -bowtie_batch_run_output output file.
 * 
 * Usage :
 * -bowtie_home ~/Software/bowtie2-2.1.0-binary -bowtie_db_path ~/bowtie-database/Ensembl-Human-gene-cdna -fastq_path tmp/Fastq-Cfam-150.fq.txt -sam_path tmp/out.sam -screenout_path tmp/Screenout.txt -bowtie_script_file tmp/BT2_script -bowtie_batch_run_output tmp/batch_run_output.txt -seed_step 2
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.liverpool.utils.GetCommandLineParameters;

public class ExecuteBowtie2 {
	
	// Create  HashMaps to remember 1) which script file produced what overall alignment
	// ... and which script file has what corresponding Bowtie-2 command
	HashMap<String, Float> map_scriptFile_alignmentPercentage;
	HashMap<String, String> map_scriptFile_bowtieCommand;
				
	public ExecuteBowtie2(){
		this.map_scriptFile_alignmentPercentage= new HashMap<String, Float>();
		this.map_scriptFile_bowtieCommand = new HashMap<String, String>();
	}
	
	/**
	 * 
	 * @param bowtie_home
	 * @param bowtie_db_path
	 * @param fastq_path
	 * @param sam_path
	 * @param screenout_path
	 * @param bowtie_script_file
	 * @param bowtie_batch_run_output
	 * @param seed_step
	 */
	public void performBatchRun(String bowtie_home,String bowtie_db_path,String fastq_path,String sam_path,
			String screenout_path,String bowtie_script_file,String bowtie_batch_run_output,int seed_step){
		
		try{
			
			BufferedWriter out_batch = new BufferedWriter(new FileWriter(new File(bowtie_batch_run_output)));
		
			GenerateBowtie2Parameters gp = new GenerateBowtie2Parameters(bowtie_home,bowtie_db_path,fastq_path,sam_path,screenout_path);
			ArrayList<String> all_commands = gp.generateParameters(seed_step);
	
			
			int counter = 1;
			for(String command : all_commands){
				
				String batchOutputFileContentForThisCommand = new String();
				
				String script_file = bowtie_script_file + "_" + counter + ".sh";
		
				File bowtie_script = new File(script_file);
				BufferedWriter out = new BufferedWriter(new FileWriter(bowtie_script));
				out.write(command);
				out.close();
				bowtie_script.setExecutable(true); // set the permission for execution
			
				String seperator = "\n\n ********************************************* \n"; 
				out_batch.write(seperator);
				out_batch.write(command);
				out_batch.flush();
				
				batchOutputFileContentForThisCommand = batchOutputFileContentForThisCommand + seperator + command;
				
				Process process = Runtime.getRuntime().exec(script_file);
			
				InputStream is  = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while((line = br.readLine()) != null){
						//System.out.println(line);
						out_batch.write(line);
						batchOutputFileContentForThisCommand = batchOutputFileContentForThisCommand + line;
				}
				process.waitFor();
				//System.out.println(process.exitValue());
            
				// Process batchOutputFileContentForThisCommand to fill the HashMaps
				BowtieScreenOutputParser bw = new BowtieScreenOutputParser();
				String info = bw.parseCommandAndScreenStrings(batchOutputFileContentForThisCommand);
				this.map_scriptFile_alignmentPercentage.put(script_file,bw.getTotalAlignment());
				this.map_scriptFile_bowtieCommand.put(script_file, bw.getBowtieCommand());
				// Screen output for debugging
				out_batch.write("\n" + info);
				System.out.println(info);
				
				counter++;
			}
		
			out_batch.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Scan the total alignment reported by all the Bowtie scripts, and return the best one
	 * @return The file name that produces the best alignment
	 */
	public String selectTheBestBowtieCommandAmongAllTheCommands(){
		
		float max_alignValue = 0.0f;
		String max_alignValueScript = new String();
		
		Iterator <String> allScripts = this.map_scriptFile_alignmentPercentage.keySet().iterator();
		while(allScripts.hasNext()){
			String scriptName = allScripts.next();
			float this_totalAlignment = this.map_scriptFile_alignmentPercentage.get(scriptName);
			if(max_alignValue < this_totalAlignment){
				max_alignValueScript = scriptName;
				max_alignValue = this_totalAlignment;
			}
		}
		
		System.out.println("Best script = " + max_alignValueScript);
		System.out.println("Best Alignment = " + this.map_scriptFile_alignmentPercentage.get(max_alignValueScript));
		System.out.println("Command = " + this.map_scriptFile_bowtieCommand.get(max_alignValueScript));
		
		return max_alignValueScript;
	}
	
	/**
	 * 
	 * @param script_file
	 * @return
	 */
	public int executeTheBestScript(String script_file){
		int exitValue  = 0;
		
		try{
			Process process = Runtime.getRuntime().exec(script_file);
		
			InputStream is  = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while((line = br.readLine()) != null){
					System.out.println(line);
			}
			process.waitFor();
			
			exitValue = process.exitValue();
			System.out.println(exitValue);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return exitValue;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ExecuteBowtie2 eb2 = new ExecuteBowtie2();
		
		try{
			
			if(args.length < 16){
				String cmd = "-bowtie_home bt_home -bowtie_db_path bowtie_db -fastq_path fastq -sam_path sam -screenout_path screenout" +
						" -bowtie_script_file bowtie_script -bowtie_batch_run_output bowtie_batch_run -seed_step seed_step"; 
	            System.out.println(cmd);
			}
	        
			String bowtie_home = GetCommandLineParameters.getCmdParameter(args, "bowtie_home", true);
			String bowtie_db_path = GetCommandLineParameters.getCmdParameter(args, "bowtie_db_path", true);
			String fastq_path = GetCommandLineParameters.getCmdParameter(args, "fastq_path", true);
			String sam_path = GetCommandLineParameters.getCmdParameter(args, "sam_path", true);
			String screenout_path = GetCommandLineParameters.getCmdParameter(args, "screenout_path", true);
			
			String bowtie_script_file = GetCommandLineParameters.getCmdParameter(args, "bowtie_script_file", true);
			String bowtie_batch_run_output = GetCommandLineParameters.getCmdParameter(args, "bowtie_batch_run_output", true);
			int seed_step = Integer.parseInt(GetCommandLineParameters.getCmdParameter(args, "seed_step", true));
			
			eb2.performBatchRun(bowtie_home, bowtie_db_path, fastq_path, sam_path,
					 screenout_path, bowtie_script_file, bowtie_batch_run_output, seed_step);	 

			String bestScript = eb2.selectTheBestBowtieCommandAmongAllTheCommands();
			
			// Write the information about the best selected script in the end of the file
			BufferedWriter out_batch = new BufferedWriter(new FileWriter(new File(bowtie_batch_run_output), true));
			out_batch.write("\n Best script = " + bestScript);
			out_batch.write("\n Best Alignment = " + eb2.map_scriptFile_alignmentPercentage.get(bestScript));
			out_batch.write("\n Command = " + eb2.map_scriptFile_bowtieCommand.get(bestScript));
			out_batch.close();
			
			// Execute the best script to produce a final SAM file
			eb2.executeTheBestScript(bestScript);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
