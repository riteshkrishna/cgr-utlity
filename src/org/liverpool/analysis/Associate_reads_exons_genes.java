package org.liverpool.analysis;
/**
 * This program takes two TSV files - 
 * 1 -  the file with columns having read IDs and Exon Ids.
 * 2.- the file with columns having exon id and gene ids.
 * We will provide the column numbers to identify the columns. 
 * 
 * The program will query both the files and return a HashMap with reads assigned to genes.
 * 
 * Alternatively, we can use a single file will all the information and provide appropriate
 * columns for processing.
 */
import java.util.*;

import org.liverpool.utils.ReadTSVFileForHashMap;

public class Associate_reads_exons_genes {
	
	String read_exon_file;
	String exon_gene_file;
	
	HashMap<String,String> read_gene_map;
	
	public Associate_reads_exons_genes(String read_exon_file,String exon_gene_file){
		this.read_exon_file = read_exon_file;
		this.exon_gene_file = exon_gene_file;
		read_gene_map = new HashMap<String,String>();
	}
	
	/**
	 * We have two TSV files - 1) with reads and exons, and 2) with exons and genes
	 * @param key1_readcolumn - read column in file 1
	 * @param value1_exoncolumn - exon column in file 1
	 * @param key2_exoncolumn - exon column in file 2
	 * @param value2_genecolumn - gene column in file 2
	 * @param boolean paired - reads are paired or un-paired
	 * 
	 * @return read-gene map by querying both the TSV files
	 */
	public HashMap<String,String> associate_readsFromExons_to_genes(int key1_readcolumn, int value1_exoncolumn,
			int key2_exoncolumn, int value2_genecolumn, boolean paired){
		
		ReadTSVFileForHashMap rsv_1 = new ReadTSVFileForHashMap(this.read_exon_file, key1_readcolumn, value1_exoncolumn);
		HashMap<String,String> read_exon_map = rsv_1.createMap();
		
		ReadTSVFileForHashMap rsv_2 = new ReadTSVFileForHashMap(this.exon_gene_file, key2_exoncolumn, value2_genecolumn);
		HashMap<String,String> exon_gene_map = rsv_2.createMap();
		
		Iterator<String> reads  = read_exon_map.keySet().iterator();
		while(reads.hasNext()){
			String read = reads.next();
			String exon = read_exon_map.get(read);
			
			if(exon_gene_map.containsKey(exon)){
				String gene = exon_gene_map.get(exon);
				
				// Remove / from the read accession for paired reads, but leave it for single ones
				// For both the types, remove the @
				if(paired == true)
					read = read.substring(read.indexOf("@") + 1,read.indexOf("/"));
				else
					read = read.substring(read.indexOf("@") + 1,read.length());
				
				this.read_gene_map.put(read, gene);
			}
		}
		return this.read_gene_map;
	}
}
