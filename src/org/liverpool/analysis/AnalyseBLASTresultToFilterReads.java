package org.liverpool.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import org.liverpool.utils.BlastStructure;
import org.liverpool.utils.ReadGeneUtilities;

/**
 * The program can be used to find the number of desired reads present in a BLAST
 * output file. It takes a list of the reads to search for, and looks for them in
 * a data-structure created by parsing a BLAST output file.
 * 
 * TSV BLAST result file : The file is supposed to be filtered with the 
 * appropriate scheme, as for example, the rows can be filtered according to the
 * e-value, score, or a certain matching length. The filtering can be performed on
 * the command line with awk/shell script etc.
 * 
 * The BLAST file will be parsed and a HashMap will be created, with the query accessions 
 * (read accessions in our case) as keys and the matching subject accessions as the values 
 * (in an ArrayList). 
 *  
 * @author ritesh
 *
 */
public class AnalyseBLASTresultToFilterReads {

	// Details of the BLAST output file used. The column numbers are standard from BLAST TAB output 
	final String delimiter = "\t";
	final int queryColumn = 0;
	final int subjectColumn = 1;
	int evalueColumn = 10;
	
	public HashMap<String, BlastStructure> blastResults = new HashMap<String, BlastStructure>();
	
	/**
	 * Read the BLAST file and create the HashMap blastResults
	 * @param blastTsvFile : The BLAST output file to be analysed
	 */
	public void  createBlastHashMap(String blastTsvFile){
		
		try{
			Scanner scanner = new Scanner(new File(blastTsvFile));
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();

				String [] records = line.split(this.delimiter);				
				// No empty value allowed
				if(records[this.queryColumn] == null || records[this.subjectColumn] == null
						|| records[this.evalueColumn] == null){
					System.out.println("Key/value value missing");
					System.exit(0);
				}
				
				String query = records[this.queryColumn]; 
				String subject = records[this.subjectColumn];
				double e_value = Double.parseDouble(records[this.evalueColumn]);
				
				BlastStructure b;
				if(blastResults.containsKey(query)){
					b = blastResults.get(query);
				}else{
					b = new BlastStructure();
				}
				b.fill(subject, e_value);
				blastResults.put(query, b);
				
			}
			
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param mouse_read_gene_map
	 * @param outFile 
	 * @return Total number of reads from mouse_read_gene_map present in the BLAST result
	 */
	public int findReadsInBlastResults(HashMap<String,String>  mouse_read_gene_map,String outFile){
		int totalReadsPresentInBlast = 0;
		
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile)));
		
			Iterator<String> reads = mouse_read_gene_map.keySet().iterator();
			while(reads.hasNext()){
				String read = reads.next();
			
				if(blastResults.containsKey(read)){
					totalReadsPresentInBlast++;
					String mousegene = mouse_read_gene_map.get(read);
					ArrayList<String> blastSubjects = blastResults.get(read).subjects;
					ArrayList<Double> blastEvalues = blastResults.get(read).e_values;
					
					for(int i = 0 ; i < blastSubjects.size(); i++)
						out.write(read +  "\t" + mousegene + "\t" + blastSubjects.get(i) + "\t" + blastEvalues.get(i) + "\n");
				}
			}
			
			out.close();
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		return totalReadsPresentInBlast;
	}
	
	/**
	 * Create Read-Gene Map for Mouse dataset 
	 * @return
	 */
	public HashMap<String,String>  createReadGeneMap_MouseData(){
		String read_exon_file  = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/read_1-exon-map-MM7.txt";
		String exon_gene_file  = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/Mouse-Chr7ProteinCoding-knownGenes-Cols-ExonID-exStart-exEnd-GeneID.txt";
		int key1_readcolumn    = 1; 
		int value1_exoncolumn  = 2;
		int key2_exoncolumn    = 1; 
		int value2_genecolumn  = 4;
		boolean paired = false;

		// create read-gene map
		HashMap<String,String>  read_gene_map = ReadGeneUtilities.prepare_readAndgene_map(read_exon_file, exon_gene_file, 
				key1_readcolumn, value1_exoncolumn, key2_exoncolumn, value2_genecolumn,paired);
	
		return read_gene_map;
	}
	

	/**
	 * 
	 * @param args
	 */
	public static void main(String [] args){
		
		String blastOutputFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Mappings/BLAST-results/filter_cutoff1-noevalthreshold_length60.txt";
		
		AnalyseBLASTresultToFilterReads ab = new AnalyseBLASTresultToFilterReads();
		ab.createBlastHashMap(blastOutputFile);		
		
		// The reads that we are interested in finding in BLAST o/p
		HashMap<String,String>  mouse_read_gene_map = ab.createReadGeneMap_MouseData();
		
		// File to write the information about the searched reads
		String outFile = "tmp/mouseReadsFromBlastResult-noevalthreshold_length60.txt";

		// Print total unique reads
		System.out.println("Total unique reads = " + ab.blastResults.size());
		System.out.println("Total exon reads found in BLAST = " + ab.findReadsInBlastResults(mouse_read_gene_map,outFile));
	}
}
