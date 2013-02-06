package org.liverpool.analysis;

import java.util.*;

import org.liverpool.utils.GetCommandLineParameters;
import org.liverpool.utils.ReadTSVFileForHashMap;

/**
 * Provide a list of genes of "interest" in a TSV file. The file can be read by ReadTSVForHashMap.java and a HashMap can
 * be created. 
 * Provide a list of reads-and-genes (read-gene HashMap can be obtained by Associate_reads_exons_genes.java).
 * 
 * Query the list of interesting genes in the read=gene list, to find out that how many reads belong to the interesting genes.
 * 
 * This can be useful when we are looking for reads belonging to orthologus genes only. Usually, when we generate reads, 
 * the read-gene association is fixed, but the set of interesting genes can vary according to time.
 * 
 * @author ritesh
 *
 */
public class AnalyseReadsForAGivenSetOfGenes {
	
	public HashMap<String, ArrayList<String>> gene_reads_map;
	
	public AnalyseReadsForAGivenSetOfGenes(){
		gene_reads_map = new HashMap<String, ArrayList<String>>();
	}
	
	/**
	 * 
	 * Create the read-gene map
	 * @param read_exon_file - file 1 with read and exon information
	 * @param exon_gene_file - file 2 with exon and gene information
	 * @param key1_readcolumn - read column in file 1
	 * @param value1_exoncolumn - exon column in file 1
	 * @param key2_exoncolumn - exon column in file 2
	 * @param value2_genecolumn - gene column in file 2
	 * @param paired - reads are paired or unpaired
	 * 
	 * Create the interesting gene map
	 * @param interestingGeneFile - file 3 with the list of interesting genes
	 * @param keyColumn           - interesting genes
	 * @param valueColumn		  - any other information (corresponding orthologs may be)
	 */
	public void findReadsForInterestingGenes(String read_exon_file,String exon_gene_file,
			int key1_readcolumn, int value1_exoncolumn,
			int key2_exoncolumn, int value2_genecolumn, 
			String interestingGeneFile,int keyColumn,int valueColumn, boolean paired){
		
		// Create read-gene map
		Associate_reads_exons_genes are = new Associate_reads_exons_genes(read_exon_file,exon_gene_file);
		HashMap<String,String> read_gene_map = are.associate_readsFromExons_to_genes(key1_readcolumn, value1_exoncolumn, 
				key2_exoncolumn, value2_genecolumn,paired);
		
        // Create a map for interesting genes
		ReadTSVFileForHashMap rsv = new ReadTSVFileForHashMap(interestingGeneFile, keyColumn, valueColumn);
		HashMap<String,String> interestingGeneMap = rsv.createMap();
		
		Iterator<String> interestingGenes = interestingGeneMap.keySet().iterator();
		while(interestingGenes.hasNext()){
			String interestingGene = interestingGenes.next();
			
			// consuming loop :(
			Iterator<String> reads = read_gene_map.keySet().iterator();
			while(reads.hasNext()){
				String read = reads.next();
				String corresponding_gene = read_gene_map.get(read);
				
				if(interestingGene.equals(corresponding_gene)){
					ArrayList<String> readsPresent;
					if(this.gene_reads_map.containsKey(interestingGene)){
						readsPresent = this.gene_reads_map.get(interestingGene);						
					}else{
						readsPresent = new ArrayList<String>();
					}
					readsPresent.add(read);
					this.gene_reads_map.put(interestingGene, readsPresent);
				}
			}
		}// end of while(interestingGenes.hasNext())
		
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String [] args) throws Exception{
		
		// Read-exon-gene related information
		String read_exon_file  = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/read_1-exon-map-MM7.txt";
		String exon_gene_file  = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/Mouse-Chr7ProteinCoding-knownGenes-Cols-ExonID-exStart-exEnd-GeneID.txt";
		int key1_readcolumn    = 1; 
		int value1_exoncolumn  = 2;
		int key2_exoncolumn    = 1; 
		int value2_genecolumn  = 4;
		
		// Interesting genes
		String interestingGeneFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Mouse-data/MM7-orthologs-on-Human-List-Ensemble.txt";
		int keyColumn = 1;
		int valueColumn = 5;
		boolean paired = false;
		
		AnalyseReadsForAGivenSetOfGenes agg = new AnalyseReadsForAGivenSetOfGenes();
		agg.findReadsForInterestingGenes(read_exon_file, exon_gene_file, key1_readcolumn, value1_exoncolumn, 
				key2_exoncolumn, value2_genecolumn, 
				interestingGeneFile, keyColumn, valueColumn,paired);
		
		System.out.println(" Total genes in the Map = " + agg.gene_reads_map.size());
		
		int count = 0;
		Iterator<String> genes = agg.gene_reads_map.keySet().iterator();
		while(genes.hasNext()){
			count = count + agg.gene_reads_map.get(genes.next()).size();
		}
		System.out.println(" Total reads present in the Map = " + count);
		
	}
	
}
