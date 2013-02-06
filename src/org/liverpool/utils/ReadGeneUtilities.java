package org.liverpool.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.liverpool.analysis.Associate_reads_exons_genes;

public class ReadGeneUtilities {
	
	/**
	 * Create the read-gene map
	 * @param read_exon_file - file 1 with read and exon information
	 * @param exon_gene_file - file 2 with exon and gene information
	 * @param key1_readcolumn - read column in file 1
	 * @param value1_exoncolumn - exon column in file 1
	 * @param key2_exoncolumn - exon column in file 2
	 * @param value2_genecolumn - gene column in file 2
	 * @param paired - reads are paired or unpaired
	 * @return
	 */
	public static HashMap<String,String> prepare_readAndgene_map(String read_exon_file,String exon_gene_file,
									int key1_readcolumn, int value1_exoncolumn,
									int key2_exoncolumn, int value2_genecolumn, boolean paired){
		
		Associate_reads_exons_genes are = new Associate_reads_exons_genes(read_exon_file,exon_gene_file);
		HashMap<String,String> read_gene_map = are.associate_readsFromExons_to_genes(key1_readcolumn, value1_exoncolumn, 
				key2_exoncolumn, value2_genecolumn, paired);
		
		return read_gene_map;
	}
	
	/**
	 * Create a hashMap of reads (key) - references (values) map
	 * @param samFile
	 * @return
	 */
	public static HashMap<String, ArrayList<String>>  get_read_reference_mapFromSAMFile(String samFile){
		
		ExtractMatchedReferencesForGivenReads emg = new ExtractMatchedReferencesForGivenReads(samFile);
		HashMap<String, ArrayList<String>> read_reference_map = emg.get_read_reference_map();
		return read_reference_map;		
	}
	

}
