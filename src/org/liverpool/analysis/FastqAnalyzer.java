package org.liverpool.analysis;
/**
 * 
 * This program takes a FASTQ file and analyses the sequences. It counts the sequences which are composed
 * of only [ATGC] and calls them as valid sequences, all other sequences containing other characters are
 * classified as non-ATGC sequences.
 * 
 */
import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastqAnalyzer {
	
	String fastqFile;
	public FastqAnalyzer(String fastqFile){
		this.fastqFile = fastqFile;
	}
	
	
	public long  performCheckForATGC(){
		long validSeq = 0;
		long non_atgc_seq = 0;
		long totalSeqs = 0;
		try{
			Scanner scanner = new Scanner(new File(this.fastqFile));
			Pattern pattern = Pattern.compile("^[ATGC]*$");
			
			String header = new String();
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				if(line.startsWith("@")){
					header = line;
					continue;
				}
				if(line.startsWith("+") || line.startsWith("I"))
					continue;
				
				//line = line.replace("\n", "");
				//line = line.trim();
				
				Matcher matcher = pattern.matcher(line.toUpperCase());
				if(matcher.matches()){
					//System.out.println(line.length() + "\t" + line.toUpperCase());
					validSeq++;
				}else{
					non_atgc_seq++;
					//System.out.println(line.length() + "\t" + line.toUpperCase());
				}
				
				totalSeqs++;
				
				header = null;
				
			}
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Total Sequences = " + totalSeqs);
		System.out.println("Valid Sequences = " + validSeq);
		System.out.println("non-ATGC Sequences = " + non_atgc_seq);
		
		return validSeq;
	}
	
	public static void main(String [] args){
		String fastqFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Simulated-Data/MM7-read/MM7-read1.fq";
		FastqAnalyzer fq = new FastqAnalyzer(fastqFile);
		fq.performCheckForATGC();
	}
}
