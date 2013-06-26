package org.liverpool.bootstrap;

import java.io.*;
import java.util.ArrayList;

public class ExecuteBowtie2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			String bowtie_home = "/Users/ritesh/Software/bowtie2-2.1.0-binary";
			String bowtie_db_path = "/Users/ritesh/Ritesh_CGR_Work/bowtie-database/Ensembl-Human-gene-cdna";
			String fastq_path = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/FASTQ-Sequences/Fastq-Cfam.txt";
			String sam_path = "/Users/ritesh/tmp/out.sam"; 
			String screenout_path = "/Users/ritesh/tmp/Screenout.txt";
			
			String bowtie_script_file = "/Users/ritesh/tmp/BT2_script"; 
			String bowtie_batch_run_output = "/Users/ritesh/tmp/batch_run_output.txt";
			
			int seed_step = 2; 
			
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
