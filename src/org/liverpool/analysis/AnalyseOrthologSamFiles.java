package org.liverpool.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.liverpool.utils.ExtractMatchedReadsForGivenReferences;
import org.liverpool.utils.ExtractMatchedReferencesForGivenReads;

public class AnalyseOrthologSamFiles {
	
	String orthologGeneFile;
	String samFileMappedOnSpecies_1;
	String samFileMappedOnSpecies_2;
	
	public AnalyseOrthologSamFiles(String samFileMappedOnSpecies_1, String samFileMappedOnSpecies_2, 
			String orthologGeneFile){
		this.orthologGeneFile = orthologGeneFile;
		this.samFileMappedOnSpecies_1 = samFileMappedOnSpecies_1;
		this.samFileMappedOnSpecies_2 = samFileMappedOnSpecies_2;
		
	}
	
	public void performAnalysis(String outFile){
		try{
			
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile)));
			// create a reference (key) - reads (values) map for the accessions we are interested in
			ExtractMatchedReadsForGivenReferences emr = new ExtractMatchedReadsForGivenReferences(orthologGeneFile, samFileMappedOnSpecies_1);
			HashMap<String, ArrayList<String>> reference_read_map_sam1 = emr.getFiltered_reference_read_map();
		
			// Create a hashMap of reads (key) - references (values) map
			ExtractMatchedReferencesForGivenReads emg = new ExtractMatchedReferencesForGivenReads(samFileMappedOnSpecies_2);
			HashMap<String, ArrayList<String>> read_reference_map = emg.get_read_reference_map();
		
			// we want to search the reads present in reference_read_map_sam1 from the keys in  read_reference_map. This will tell us
			// that how many reads from the first genome (the read-gene correspondence is available in reference_read_map_sam1) were found
			// in the other SAM file (the other genome).
		
			Iterator<String> genesFromSam_1 = reference_read_map_sam1.keySet().iterator();
			while(genesFromSam_1.hasNext()){
				String gene_genome1 = genesFromSam_1.next();
				ArrayList<String> reads = reference_read_map_sam1.get(gene_genome1);
				for(String read : reads){
					if(read_reference_map.containsKey(read)){
						ArrayList<String> mappedGenes = read_reference_map.get(read);
						String geneString = new String(mappedGenes.get(0));
						for(int i = 1; i < mappedGenes.size(); i++){
							geneString = geneString + "," + mappedGenes.get(i);
						}
						String outString = read + "\t" + gene_genome1 + "\t" + geneString + "\n";
						out.write(outString);
						read_reference_map.remove(read);
					}
				}
			}
			
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String [] args){
		String samFileMappedOnSpecies_1 = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/listOfmappedReads-MMchr7-mapto-MouseGenesOnChr7.txt";
		String samFileMappedOnSpecies_2 = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/listOfmappedReads-MMchr7PP-mapto-HumanChrPP.txt"; 
		String orthologGeneFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/mousechr7-ortholog-wrt-human-sequences-list.txt";
		String outFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/output.txt";
		
		AnalyseOrthologSamFiles ao = new AnalyseOrthologSamFiles(samFileMappedOnSpecies_1, samFileMappedOnSpecies_2, orthologGeneFile);
		ao.performAnalysis(outFile);
	}
}
