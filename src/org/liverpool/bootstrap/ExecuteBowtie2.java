package org.liverpool.bootstrap;

import java.io.*;
import java.util.ArrayList;

import org.liverpool.utils.GetCommandLineParameters;

public class ExecuteBowtie2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			
			/* 
			String bowtie_home = "/Users/ritesh/Software/bowtie2-2.1.0-binary";
			String bowtie_db_path = "/Users/ritesh/Ritesh_CGR_Work/bowtie-database/Ensembl-Human-gene-cdna";
			String fastq_path = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/FASTQ-Sequences/Fastq-Cfam.txt";
			String sam_path = "/Users/ritesh/tmp/out.sam"; 
			String screenout_path = "/Users/ritesh/tmp/Screenout.txt";
			
			String bowtie_script_file = "/Users/ritesh/tmp/BT2_script"; 
			String bowtie_batch_run_output = "/Users/ritesh/tmp/batch_run_output.txt";
			int seed_step = 2;
			*/
			 
			
			BufferedWriter out_batch = new BufferedWriter(new FileWriter(new File(bowtie_batch_run_output)));
			
			GenerateBowtie2Parameters gp = new GenerateBowtie2Parameters(bowtie_home,bowtie_db_path,fastq_path,sam_path,screenout_path);
			ArrayList<String> all_commands = gp.generateParameters(seed_step);
		
			int counter = 1;
			for(String command : all_commands){
				String script_file = bowtie_script_file + "_" + counter + ".sh";
			
				File bowtie_script = new File(script_file);
				BufferedWriter out = new BufferedWriter(new FileWriter(bowtie_script));
				out.write(command);
				out.close();
				bowtie_script.setExecutable(true); // set the permission for execution
				
				out_batch.write("\n\n ********************************************* \n");
				out_batch.write(command);
				out_batch.flush();
				
				// execute script_file and capture the screenout
				//String commandString = "./ " + script_file;
				//ProcessBuilder builder = new ProcessBuilder("sh","-c",commandString);
				//ProcessBuilder builder = new ProcessBuilder(script_file);
				//Process process = builder.start();
				Process process = Runtime.getRuntime().exec(script_file);
				
                InputStream is  = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while((line = br.readLine()) != null){
                        System.out.println(line);
                        out_batch.write(line);
                }
                process.waitFor();
                System.out.println(process.exitValue());
                
				counter++;
			}
			
			out_batch.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
