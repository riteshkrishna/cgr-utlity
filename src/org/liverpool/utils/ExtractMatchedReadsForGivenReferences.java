package org.liverpool.utils;

import java.util.*;
import java.io.*;

/**
 * This program takes as input a set of reference accessions and extracts the corresponding 
 * matched reads from the hashMap created by ParseSAMFileForGenesAndReads.java
 * 
 * Expects a file with reference accessions at each line of a single column file. It will read the
 * file and create a Hash of accessions to search for.
 * @author riteshk
 *
 */
public class ExtractMatchedReadsForGivenReferences {
	
	public HashSet<String> referenceAccesions;
	public HashMap<String, ArrayList<String>> filtered_reference_reads_map;
	
	String accessionFile;
	String samFile;
	
	public ExtractMatchedReadsForGivenReferences(String accessionFile, String samFile){
		this.accessionFile = new String(accessionFile);
		this.samFile = new String(samFile);
		this.referenceAccesions = new HashSet<String>();
		this.filtered_reference_reads_map = new HashMap<String, ArrayList<String>>(); 
		
	}
	
	/**
	 * Reads a TSV file and sets the accessions in a HashSet
	 * @param accessionFile
	 */
	void readReferenceAccessionsFromFile(String accessionFile){
		try{
			Scanner scanner = new Scanner(new File(accessionFile));
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();				
				if(line != null)
					this.referenceAccesions.add(line.trim());
			}
			
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Read the SAM file and extract the reads for desired references
	 * @param samFile
	 */
	void extractAskedReferencesFromGenesAndReadsFromSAMFile(String samFile){
		
		ParseSAMFileForGenesAndReads pr = new ParseSAMFileForGenesAndReads(samFile);
		pr.processSAMFile();
		HashMap<String, ArrayList<String>> sam_reference_reads_map = pr.getReferenceReadHashMap();
		
		Iterator<String> accessionsToFind = this.referenceAccesions.iterator();
		while(accessionsToFind.hasNext()){
			String accession = accessionsToFind.next();
			if(sam_reference_reads_map.containsKey(accession)){
				ArrayList<String> reads = sam_reference_reads_map.get(accession);
				this.filtered_reference_reads_map.put(accession, reads);
			}
		}
		
		if(this.filtered_reference_reads_map.size() == 0)
			System.out.println("Nothing found for the given list of references");
		else 
			System.out.println("Reads found for total "+ this.filtered_reference_reads_map.size() + " references");
	}
	
	
	/**
	 * The main function which does everything and returns a map with filtered references and reads 
	 * @return
	 */
	public HashMap<String, ArrayList<String>> getFiltered_reference_read_map(){
		readReferenceAccessionsFromFile(accessionFile);
		extractAskedReferencesFromGenesAndReadsFromSAMFile(samFile);
		return this.filtered_reference_reads_map;
	}
	
	
}
