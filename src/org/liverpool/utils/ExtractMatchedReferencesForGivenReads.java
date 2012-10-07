package org.liverpool.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This program does the opposite of ExtractMatchedReadsForGivenReferences.java. It takes
 * a SAM file and prepares a Hash of reads as keys with matching reference accessions as values.
 * 
 * @author riteshk
 *
 */
public class ExtractMatchedReferencesForGivenReads {
	
	String samFile ;
	public HashMap<String, ArrayList<String>> read_references_map;
	
	public ExtractMatchedReferencesForGivenReads(String samFile) {
		this.samFile = new String(samFile);
		this.read_references_map = new HashMap<String, ArrayList<String>>();
	}
	
	void create_read_refernce_map(){
		
		ParseSAMFileForGenesAndReads pr = new ParseSAMFileForGenesAndReads(this.samFile);
		pr.processSAMFile();
		HashMap<String, ArrayList<String>> sam_reference_reads_map = pr.getReferenceReadHashMap();
		
		Iterator<String> references = sam_reference_reads_map.keySet().iterator();
		while(references.hasNext()){
			String reference = references.next();
			ArrayList<String> reads = sam_reference_reads_map.get(reference);
			for(String read : reads){
				ArrayList<String> referenceColl;
				if(this.read_references_map.containsKey(read))
					referenceColl = this.read_references_map.get(read);
				else
					referenceColl = new ArrayList<String>();
				
				if(!referenceColl.contains(reference)){
					referenceColl.add(reference);
					this.read_references_map.put(read, referenceColl);
				}
			}
		}
		
		System.out.println("Total reads identified = " + this.read_references_map.size());
	}
	
	/**
	 * The main method which does all the internal work and returns results
	 * @return
	 */
	public HashMap<String, ArrayList<String>> get_read_reference_map(){
		create_read_refernce_map();
		return this.read_references_map;
	}
	
}
