package org.liverpool.Simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.liverpool.utils.ReadTSVFileForHashMap;

/**
 * Simulation scheme 1 : Create a FASTQ file from a Single Chromosome FASTA file. The FASTQ sequences
 * are generated from a list of FASTQ headers provided in a separate file. The headers come from the
 * FASTQ file produced by wgsim program. The wgsim generated headers contain the start and end co-ordinates
 * of the sequence in the Chromosome FASTA file. Here, we create FASTQ sequences from the FASTA file using the
 * same co-ordinates, and we can compare these sequences with the ones produced by wgsim to compare how they
 * differ. Using this scheme, we can produce the purest, unaltered FASTQ sequences.
 *    
 * @author ritesh
 *
 */
public class SimulateFastq {
	
	// Wgsim headers and corresponding gene name
	HashMap<String,String> wgsimHeaders;
	
	SimulateFastq(){
		wgsimHeaders = new HashMap<String,String>(); 
	}
	
	/**
	 * Read the wgsim headers and the corresponding gene from a TSV file
	 * @param wgsimHeaderFile
	 * @param keyColumn
	 * @param valueColumn
	 */
	public void readTheWgsimHeaderList(String wgsimHeaderFile,int keyColumn,int valueColumn){
		ReadTSVFileForHashMap rsv = new ReadTSVFileForHashMap(wgsimHeaderFile, keyColumn, valueColumn);
		wgsimHeaders = rsv.createMap();
	}
	
	public void readFastaAndExtractSubSequences(String fastaFile, String accessionOfInterest,
												String outputFastaFile){
		try{
			
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFastaFile)));
			
			LinkedHashMap<String,DNASequence> fastaContent  = FastaReaderHelper.readFastaDNASequence(new File(fastaFile));
			
			DNASequence dna = fastaContent.get(accessionOfInterest);
			
			Iterator<String> fastqHeaders = wgsimHeaders.keySet().iterator();
			while(fastqHeaders.hasNext()){
				String header = fastqHeaders.next();
				// A typical wgsim header - @7_110059629_110060128_0:0:0_0:0:0_3b1d9/1
				String [] headerParts = header.split("_");
				Integer start = Integer.parseInt(headerParts[1]);
				Integer end = Integer.parseInt(headerParts[2]);
				
				String dnaSubSequence = dna.getSubSequence(start, end).getSequenceAsString();
				
				String towrite = ">" +  header + "\n" + dnaSubSequence + "\n";
				out.write(towrite);
			}
			
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	public static void main(String [] args) throws Exception{
		SimulateFastq sim = new SimulateFastq();
		
		String wgsimHeaderFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/read_1-exon-map-MM7.txt";
		int keyColumn = 1;
		int valueColumn = 2;
		sim.readTheWgsimHeaderList(wgsimHeaderFile, keyColumn, valueColumn);
		
		String fastaFile ="/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Database/Mus-musculus-Chr7.fa"; 
		String accessionOfInterest = "7";
		String outputFastaFile = "tmp/simFasta.fa";
		sim.readFastaAndExtractSubSequences(fastaFile, accessionOfInterest, outputFastaFile);
		
	}
}
