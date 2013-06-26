package org.liverpool.bootstrap;

import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.ws.handler.MessageContext.Scope;

public class GenerateBowtie2Parameters {

final int minimum_seed_length = 6;
	
	public String bowtie_home;
	public String bowtie_db_path;
	public String fastq_path;
	public String sam_path; 
	public String screenout_path;
	
	public int default_seed_length = 20;
	public int default_seed_match = 0;
	public String str_seed_overlap = "S,1,0.10"; // Fixed to very low already - No iteration for this at the moment
	public int int_d = 5; 
	public int int_r = 3; 
	public String str_score_fun = "L,-0.6,-0.6"; // default --score-min function in end-to-end
	
	// Allowed functions : L,S,G. Paramaters as F,A,B where expression is f(x) = A + B * F(x)
	// For end-to-end mapping, the L function should be used (see the writeup on 24-6-2013 for details)
	public double A = 0.6;
	public double B_min = 0.6;
	public double B_step = 0.6;
	public double B_max = 3; // should try with 1 or two as well to see the effect 
	
	/**
	 * @param bowtie_home
	 * @param bowtie_db_path
	 * @param fastq_path
	 * @param sam_path
	 * @param screenout_path
	 */
	public GenerateBowtie2Parameters(String bowtie_home,String bowtie_db_path, String fastq_path,String sam_path,String screenout_path){
		this.bowtie_home = bowtie_home;
		this.bowtie_db_path = bowtie_db_path;
		this.fastq_path = fastq_path;
		this.sam_path = sam_path; 
		this.screenout_path = screenout_path;
	}
	
	/**
	 * Call this function, in case we want to change the behaviour of the other Bowtie settings
	 * @param default_seed_length
	 * @param str_seed_overlap
	 * @param int_d
	 * @param int_r
	 * @param str_score_fun
	 * @param A
	 * @param B_min
	 * @param B_max
	 * @param B_step
	 */
	public void alterDefaultBowtieParameters(int default_seed_length,String str_seed_overlap,
			int int_d, int int_r, String str_score_fun,
			double A, double B_min, double B_max, double B_step){
		this.default_seed_length = default_seed_length;
		this.str_seed_overlap = str_seed_overlap;
		this.int_d = int_d;
		this.int_r = int_r;
		this.str_score_fun = str_score_fun;
		this.A = A;
		this.B_min = B_min;
		this.B_max = B_max;
		this.B_step = B_step;
	}
	
	public ArrayList<String> generateParameters(int seed_step){
		
		int generated_seed_length = default_seed_length;
		int generated_seed_match = default_seed_match;
		String generated_seed_overlap = str_seed_overlap; 
		int generated_d = int_d; 
		int generated_r = int_r; 
		String generated_score_fun = str_score_fun;
		
		ArrayList<String> seedAltered_bowtie_commands = new ArrayList<String>();
		
		// Different seed parameters with no seed mismatch allowed
		for(generated_seed_length = default_seed_length; generated_seed_length >= minimum_seed_length; generated_seed_length = generated_seed_length - seed_step){
			String bowtie_command = createBowtieScript(generated_seed_length, generated_seed_match, 
					generated_seed_overlap, generated_d, generated_r, generated_score_fun);
			seedAltered_bowtie_commands.add(bowtie_command);
		}
		
		// Different seed parameters with seed mismatch allowed
		generated_seed_match = 1;
		for(generated_seed_length = default_seed_length; generated_seed_length >= minimum_seed_length; generated_seed_length = generated_seed_length - seed_step){
			String bowtie_command = createBowtieScript(generated_seed_length, generated_seed_match, 
					generated_seed_overlap, generated_d, generated_r, generated_score_fun);
			seedAltered_bowtie_commands.add(bowtie_command);
		}
		
		// then iterate for the seed overlap parameters
		// Allowed functions : L,S,G. Paramaters as F,A,B where expression is f(x) = A + B * F(x)
		// For end-to-end mapping, the L function should be used (see the writeup on 24-6-2013 for details)
		//double A = 0.6;
		//double B_min = 0.6;
		//double B_step = 0.6;
		//double B_max = 3; // should try with 1 or two as well to see the effect 

		// Different seed parameters with no seed mismatch allowed + various scoring options
		generated_seed_match = 0;
		for(generated_seed_length = default_seed_length; generated_seed_length >= minimum_seed_length; generated_seed_length = generated_seed_length - seed_step){
			for(double score_parameter = B_min; score_parameter <= B_max; score_parameter = score_parameter + B_step){
				generated_score_fun ="L," + "-" + A + ",-" + score_parameter; // Like L,-A,-B
				String bowtie_command = createBowtieScript(generated_seed_length, generated_seed_match, 
						generated_seed_overlap, generated_d, generated_r, generated_score_fun);
				seedAltered_bowtie_commands.add(bowtie_command);
			}
		}
		
		// Different seed parameters with allowed seed mismatch allowed + various scoring options
		generated_seed_match = 1;
		for(generated_seed_length = default_seed_length; generated_seed_length >= minimum_seed_length; generated_seed_length = generated_seed_length - seed_step){
			for(double score_parameter = B_min; score_parameter <= B_max; score_parameter = score_parameter + B_step){
				generated_score_fun ="L," + "-" + A + ",-" + score_parameter; // Like L,-A,-B
				String bowtie_command = createBowtieScript(generated_seed_length, generated_seed_match, 
						generated_seed_overlap, generated_d, generated_r, generated_score_fun);
				seedAltered_bowtie_commands.add(bowtie_command);
			}
		}
		 
		return seedAltered_bowtie_commands;
	}
	
	/**
	 * 
	 * @param int_seed_length
	 * @param int_seed_match
	 * @param str_seed_overlap
	 * @param int_d
	 * @param int_r
	 * @param str_score_fun
	 * @return
	 */
	public String createBowtieScript(int int_seed_length, int int_seed_match, 
			String str_seed_overlap, int int_d, int int_r, String str_score_fun){
		
		PrepareBowtieParameterFile pb = new PrepareBowtieParameterFile();
		String command = pb.prepareBowtieRunScript(bowtie_home,bowtie_db_path, fastq_path, sam_path, 
				screenout_path, int_seed_length, int_seed_match, str_seed_overlap, int_d, int_r, str_score_fun);
		
		return command;
	}
	
	public static void main(String[] args) {
		
		Scanner keyIn = new Scanner(System.in);
		
		String bowtie_home = "xx";
		String bowtie_db_path = "xx";
		String fastq_path = "xx";
		String sam_path = "xx"; 
		String screenout_path = "xx";
		
		int seed_step = 2; 
		
		GenerateBowtie2Parameters gp = new GenerateBowtie2Parameters(bowtie_home,bowtie_db_path,fastq_path,sam_path,screenout_path);
		
		//call alterDefaultBowtieParameters(int default_seed_length,...) here if you needed to alter the behaviour
		
		ArrayList<String> all_commands = gp.generateParameters(seed_step);
		
		for(String command : all_commands){
			System.out.println(command);
			keyIn.nextLine();
		}
	}

}
