package org.liverpool.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.liverpool.utils.ExtractMatchedReferencesForGivenReads;
import org.liverpool.utils.ReadGeneUtilities;
import org.liverpool.utils.ReadTSVFileForHashMap;

/**
 * Take the mouse read data in read-exon TSV file. Have another TSV file for associating 
 * exons with the mouse genes. This way we will have read-gene accociation for mouse data.
 * This will ensure that we are only searching for those reads which originate from mouse exons.   
 * 
 * We will search for these reads in a Human SAM file and search for the reads which were mapped.
 * 
 * TODO : Furthermore, we will take a list of orthologus genes from Mouse and Human in a TSV file and
 * create a list of reads - mapped on human gene - corresponding mouse ortholog gene
 *   
 * @author ritesh
 *
 */
public class MatchMouseReadsAgainstHumanSAMFile {
	
	

	
	public HashMap<String,String> readOrthologFile(String orthologFile, int species1_column,int species2_column){
		
		ReadTSVFileForHashMap rsv = new ReadTSVFileForHashMap(orthologFile, species1_column, species2_column);
		HashMap<String,String> map = rsv.createMap();
		return map;
	}
	
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String [] args){
		try{
			
			// Read-exon-gene related information
			String read_exon_file  = "tmp/read_1-exon-map-MM7.txt";
			String exon_gene_file  = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/Mouse-Chr7ProteinCoding-knownGenes-Cols-ExonID-exStart-exEnd-GeneID.txt";
			int key1_readcolumn    = 1; 
			int value1_exoncolumn  = 2;
			int key2_exoncolumn    = 1; 
			int value2_genecolumn  = 4;
		
			// SAM file with mapping
			//String samFile         = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Mappings/MappingExonReadsOnly/MM7-read1-PP-OnEnsemblehuman-veryfastlocal.sam";
			String samFile         = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Mappings/MappingExonReadsOnly/JavaSimulatedReads/wholeExonRegionChr7-fastlocal-PP.sam";
			boolean paired = false;
			
			// List of orthologous genes
			String orthologFile    = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Mouse-data/MM7-orthologs-on-Human-List-Ensemble.txt";
			int species1_column    = 1;
			int species2_column    = 5;
			 
			
			String outFile = "tmp/wholeExonRegionChr7-fastlocal-PP-wrt_Human.txt";

			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile)));
		
			MatchMouseReadsAgainstHumanSAMFile mp = new MatchMouseReadsAgainstHumanSAMFile();
		
			// create read-gene map
			HashMap<String,String>  read_gene_map = ReadGeneUtilities.prepare_readAndgene_map(read_exon_file, exon_gene_file, 
					key1_readcolumn, value1_exoncolumn, key2_exoncolumn, value2_genecolumn,paired);
		
			// create read-reference map
			HashMap<String, ArrayList<String>> read_reference_map = ReadGeneUtilities.get_read_reference_mapFromSAMFile(samFile);
		
			// create ortholog map
			HashMap<String,String> ortholog_map = mp.readOrthologFile(orthologFile, species1_column,species2_column);
		
			/* If we want to know the relationship: read -> source gene -> mapped to gene
			Iterator<String> reads = read_gene_map.keySet().iterator();
			while(reads.hasNext()){
				String readID = reads.next();
				String origin_gene = read_gene_map.get(readID);
			
				if(read_reference_map.containsKey(readID)){
					ArrayList<String> mappedOnGenes = read_reference_map.get(readID);
				
					for(String mappedGene : mappedOnGenes){	
						String output = readID + "\t" + origin_gene + "\t" + mappedGene + "\n";
						out.write(output);
					}
				}
			}
			*/
			Iterator<String> reads = read_gene_map.keySet().iterator();
			while(reads.hasNext()){
				String readID = reads.next();
				String origin_gene = read_gene_map.get(readID);
			
				// First, the read needs to come from an ortholog gene
				if(ortholog_map.containsKey(origin_gene)){
					String human_ortholog_gene = ortholog_map.get(origin_gene);
					// Second, if the read is mapped at all in the SAM file
					if(read_reference_map.containsKey(readID)){
						ArrayList<String> mappedOnGenes = read_reference_map.get(readID);
						for(String mappedGene : mappedOnGenes){
							int correctMap = 0;
							if(mappedGene.contains(human_ortholog_gene)){
								correctMap = 1;
							}
							String output = readID + "\t" + origin_gene + "\t"  + human_ortholog_gene + "\t" + mappedGene + "\t" + correctMap + "\n";
							out.write(output);
						}
					}
				}
			}
			
			out.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
