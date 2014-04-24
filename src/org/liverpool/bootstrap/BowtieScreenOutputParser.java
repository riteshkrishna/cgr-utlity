package org.liverpool.bootstrap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This code parses the output strings produced during the execution of ExecuteBowtie2.java.  
 * The output strings contain command string created by prepareBowtieRunScript.java
 * and also the screen output produced by execution of Bowtie-2. The idea is to parse
 * these strings and assemble the information in an easy to read tab format.
 *  
 * @author ritesh
 *
 */
public class BowtieScreenOutputParser {

	/**
	 * A typical example of the string to be parsed is the following -
	 """
	   ********************************************* 
		#!/bin/sh  
		BT2_HOME=/Users/ritesh/Software/bowtie2-2.1.0-binary
		# The Datasets 
		BOWTIE_DB=/Users/ritesh/Ritesh_CGR_Work/bowtie-database/Ensembl-Human-gene-cdna
		QUERY_FILE=/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/FASTQ-Sequences/length-150/Fastq-Cfam-150.fq.txt
		SAM_FILE=/Users/ritesh/tmp/out.sam
		STDOUT_CAPTURE=/Users/ritesh/tmp/Screenout.txt

		## Custom parameters 
		L=20
		N=0
		seed_overlap='S,1,0.10' 
		D=5
		R=3
		score='L,-0.6,-0.6'
		# Bowtie command 
		$BT2_HOME/bowtie2 -x $BOWTIE_DB -t -p 6 -D $D -R $R -L $L -N $N -i $seed_overlap --score-min $score -U $QUERY_FILE -S $SAM_FILE 2>&1 1>$STDOUT_CAPTURE | tee -a -i $STDOUT_CAPTURE
		Time loading reference: 00:00:00Time loading forward index: 00:00:04Time loading mirror index: 00:00:03Multiseed full-index search: 00:00:19199424 reads; of these:  199424 (100.00%) were unpaired; of these:    85390 (42.82%) aligned 0 times    15848 (7.95%) aligned exactly 1 time    98186 (49.23%) aligned >1 times57.18% overall alignment rateTime searching: 00:00:26Overall time: 00:00:26

	 """
	 */
	
	float totalAlignmentFound = 0.0f;
	String bowtie_command = new String();
	
	public String parseCommandAndScreenStrings(String inputStr){
		String info = new String();
		
		HashMap<String,String> parameterValues = new HashMap<String,String>();
		
		//System.out.println(inputStr);
		
		// Seed length
		String reg_L = "(L=)(\\d+)";
		Pattern p = Pattern.compile(reg_L);
		Matcher m = p.matcher(inputStr);
		while(m.find()){
			//System.out.println(m.group(2));
			String extract = m.group();
			info = extract;
			parameterValues.put("L", m.group(2));
		}
		
		// Seed match 
		String reg_N = "(N=)(\\d+)";
		m = Pattern.compile(reg_N).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("N", m.group(2));
		}
		
		// Dynamic programming
		String reg_D = "(D=)(\\d+)";
		m = Pattern.compile(reg_D).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("D", m.group(2));
		}
		
		// Dynamic programming
		String reg_R = "(R=)(\\d+)";
		m = Pattern.compile(reg_R).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("R", m.group(2));
		}
		
		// The seed overlap function
		String reg_seedoverlap = "(seed_overlap=)(\\S+)";
		m = Pattern.compile(reg_seedoverlap).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("seed_overlap", m.group(2));
		}
		
		// The score function
		String reg_score = "(score=)(\\S+)";
		m = Pattern.compile(reg_score).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("score", m.group(2));
		}
		
		// Overall alignment 57.18% overall alignment
		String reg_alignment = "([0-9]*\\.?[0-9]*)(% overall alignment)";
		m = Pattern.compile(reg_alignment).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("TotalAlignment", m.group(1));
		}
				
		//BT2_HOME=/Users/ritesh/Software/bowtie2-2.1.0-binary
		String reg_bt = "(BT2_HOME=)(\\S+)";
		m = Pattern.compile(reg_bt).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("BT2_HOME", m.group(2));
		}		
		
		//BOWTIE_DB=/Users/ritesh/Ritesh_CGR_Work/bowtie-database/Ensembl-Human-gene-cdna
		String reg_btdb = "(BOWTIE_DB=)(\\S+)";
		m = Pattern.compile(reg_btdb).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("BOWTIE_DB", m.group(2));
		}
		//QUERY_FILE=/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/FASTQ-Sequences/length-150/Fastq-Cfam-150.fq.txt
		String reg_query = "(QUERY_FILE=)(\\S+)";
		m = Pattern.compile(reg_query).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("QUERY_FILE", m.group(2));
		}
		//SAM_FILE=/Users/ritesh/tmp/out.sam
		String reg_sam = "(SAM_FILE=)(\\S+)";
		m = Pattern.compile(reg_sam).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("SAM_FILE", m.group(2));
		}
		
		//STDOUT_CAPTURE=/Users/ritesh/tmp/Screenout.txt
		String reg_screen = "(STDOUT_CAPTURE=)(\\S+)";
		m = Pattern.compile(reg_screen).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			parameterValues.put("STDOUT_CAPTURE", m.group(2));
		}
		
		// The full Bowtie-2 command
		String reg_command = "(\\$BT2_HOME)(.+)";
		m = Pattern.compile(reg_command).matcher(inputStr);
		while(m.find()){
			String extract = m.group();
			info = info + "\t" + extract;
			this.bowtie_command = extract;
		}

		// Create the corresponding Bowtie-2 command from these parameters
		Iterator<String> keys = parameterValues.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			String value = parameterValues.get(key);
			info = info.replace("$"+key, value);
		}
		
		// Create the corresponding Bowtie-2 command for the class variable
		keys = parameterValues.keySet().iterator();
		while(keys.hasNext()){
				String key = keys.next();
				String value = parameterValues.get(key);
				this.bowtie_command = this.bowtie_command.replace("$"+key, value);
		}
		
		// Add the total alignment in the beginning of the line
		info = parameterValues.get("TotalAlignment") + "\t" + info;
		
		this.totalAlignmentFound = Float.parseFloat(parameterValues.get("TotalAlignment")); 
				
		return info;
	}
	
	/**
	 * Total Alignment found
	 * @return
	 */
	public float getTotalAlignment(){
		return this.totalAlignmentFound;
	} 
	
	/**
	 * The full executable bowtie-2 command 
	 * @return
	 */
	public String getBowtieCommand(){
		return this.bowtie_command;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String exampleCommand = " " +
				" *********************************************\n" +  
				"#!/bin/sh\n"  +
				"BT2_HOME=/tmp/Software/bowtie2-2.1.0-binary\n" +
				"# The Datasets\n" + 
				"BOWTIE_DB=/tmp/bowtie-database/Ensembl-Human-gene-cdna\n" +
				"QUERY_FILE=/tmp/Fastq-Cfam-150.fq.txt\n" +
				"SAM_FILE=/tmp/out.sam\n" +
				"STDOUT_CAPTURE=/tmp/Screenout.txt\n" +
				"\n" +
				"## Custom parameters\n" + 
				"L=20\n" +
				"N=0\n" +
				"seed_overlap='S,1,0.10'\n" + 
				"D=5\n" +
				"R=3\n" +
				"score='L,-0.6,-0.6'\n" +
				"# Bowtie command\n" + 
				"$BT2_HOME/bowtie2 -x $BOWTIE_DB -t -p 6 -D $D -R $R -L $L -N $N -i $seed_overlap --score-min $score -U $QUERY_FILE -S $SAM_FILE 2>&1 1>$STDOUT_CAPTURE | tee -a -i $STDOUT_CAPTURE\n" +
				"Time loading reference: 00:00:00Time loading forward index: 00:00:04Time loading mirror index: 00:00:03Multiseed full-index search: 00:00:19199424 reads; of these:  199424 (100.00%) were unpaired; of these:    85390 (42.82%) aligned 0 times    15848 (7.95%) aligned exactly 1 time    98186 (49.23%) aligned >1 times57.18% overall alignment rateTime searching: 00:00:26Overall time: 00:00:26\n" +
				" ";
		
		BowtieScreenOutputParser bw = new BowtieScreenOutputParser();
		String info = bw.parseCommandAndScreenStrings(exampleCommand);
		
		System.out.println(info); // Print the tab seperated string with complete information
		
		System.out.println(bw.getTotalAlignment()); // Print the total alignment found
		
		System.out.println(bw.getBowtieCommand()); // Print the executable Bowtie-2 command
	}

}
