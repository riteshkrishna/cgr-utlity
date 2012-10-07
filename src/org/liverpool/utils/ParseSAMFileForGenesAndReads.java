package org.liverpool.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * This program parses a SAM file and creates a hashMap of matched reads with the reference
 * accession. The matched reads are identified by their CIGAR field being "=".
 *  
 * @author riteshk
 *
 */
public class ParseSAMFileForGenesAndReads {
	
	final int QNAME_col = 0;
	final int RNAME_col = 2;
	final int CIGAR_col = 6;
	
	
	String samFile;
	
	HashMap<String, ArrayList<String>> reference_reads_map;
	/**
	 * 
	 * @param samFile The input SAM file
	 * @param outFile the output TSV file
	 */
	public ParseSAMFileForGenesAndReads(String samFile){
		this.samFile = new String(samFile);
		this.reference_reads_map = new HashMap<String,ArrayList<String>>();
	}

	public HashMap<String, ArrayList<String>> processSAMFile(){
		try{
			int readCount = 0;
			Scanner scanner = new Scanner(new File(this.samFile));
						
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				if(line.startsWith("@SQ"))
					continue;
				
				String [] records = line.split("\t",-1);
				if(records.length < 11)
					continue;
				
				if(records[CIGAR_col].equals("=")){
					String read_id = records[QNAME_col];
					String reference_id = records[RNAME_col];
					
					readCount++;
					
					ArrayList<String> readsPresent;
					if(reference_reads_map.containsKey(reference_id)){
						readsPresent = reference_reads_map.get(reference_id);
					}else{
						readsPresent = new ArrayList<String>();
					}
					
					// Keep only single copy of the read ids.
					if(!readsPresent.contains(read_id)){
						readsPresent.add(read_id);
						reference_reads_map.put(reference_id, readsPresent);
					}
				}
			}
			
			System.out.println("Total reads with match reported in the SAM file = " + readCount);
			scanner.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}

		return this.reference_reads_map;
		
	}
	
	/**
	 * A method to return the reference and read hash map created
	 * @return
	 */
	public HashMap<String, ArrayList<String>> getReferenceReadHashMap(){
		return this.reference_reads_map;
	}
	
	/*
	 * Write the hashMap to output file 
	 */
	public void writeHashToFile(String outFile){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile)));
			
			Iterator<String> references = this.reference_reads_map.keySet().iterator();
			while(references.hasNext()){
				String reference = references.next();
				ArrayList<String> reads = this.reference_reads_map.get(reference);
				
				String readString = new String(reads.get(0));
				for(int i = 1; i < reads.size(); i++){
					readString = readString + "," + reads.get(i);
				}
				String towrite = reference + "\t" + readString + "\n";
				out.write(towrite);
			}
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * The test function
	 * @param args
	 */
	public static void main(String [] args){
		String inputSam = "C:\\Ritesh-CGR-Work\\MM7-read\\listOfmappedReads-MMchr7-mapto-MouseGenesOnChr7.txt";
		String outputTsv = "C:\\Ritesh-CGR-Work\\MM7-read\\listOfmappedReads-And-Genes.txt";
		ParseSAMFileForGenesAndReads pr = new ParseSAMFileForGenesAndReads(inputSam);
		pr.processSAMFile();
		pr.writeHashToFile(outputTsv);
	}
}
