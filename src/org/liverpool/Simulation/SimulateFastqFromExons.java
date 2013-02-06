package org.liverpool.Simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.liverpool.utils.ReadTSVFileForHashMap;

/**
 * Simulation scheme 2 : Create a FASTA file from Exonic sequences. The file takes as input the co-ordinates
 * of exons or the regions on a genome. The co-ordinates are provided in a TSV format. The input also consists of a FASTA file
 * containing the sequence to which the co-ordinates correspond to. The FASTA could be a chromosome specific sequence 
 * or an entire genome (and accordingly the co-ordinates are provided as well). The program reads the FASTA file
 * and, for each exon, it extracts the subsequence representing that exon. The subsequence is further split in 'overlapping
 * smaller sequences' of the desired length. The overlapping behaviour can be overridden by setting the appropriate parameter as
 * 0 or negative. Each such smaller sequence is converted into a FASTA format and written to
 * a FASTA file. This will help us simulate the small reads from the 'entire exon regions'. 
 * 
 *    
 * @author ritesh
 *
 */

public class SimulateFastqFromExons {
	
	ArrayList<String> exonSequenceHeaders;
	
	SimulateFastqFromExons(){
		exonSequenceHeaders = new ArrayList<String> (); 
	}
	
	public void readCoOrdinateFile(String exonInformationFile, int keyColumn, int startExonColumn, int endExonColumn){
		ReadTSVFileForHashMap rsv_start = new ReadTSVFileForHashMap(exonInformationFile, keyColumn, startExonColumn);
		HashMap<String,String> exon_start = rsv_start.createMap();
		
		ReadTSVFileForHashMap rsv_end = new ReadTSVFileForHashMap(exonInformationFile, keyColumn, endExonColumn);
		HashMap<String,String> exon_end = rsv_end.createMap();
		
		Iterator<String> exons = exon_start.keySet().iterator();
		while(exons.hasNext()){
			String exon = exons.next();
			String start = exon_start.get(exon);
			String end = exon_end.get(exon);			
			String header = exon + "_" + start + "_" + end;
			exonSequenceHeaders.add(header);
		}		
	}
	
	/**
	 * 
	 * @param sequenceFastaFile - The FASTA file containing the sequence to convert into reads
	 * @param accessionOfInterest - The sequence accession in the FASTA file which will produce reads
	 * @param outputFastaFile - The output file containing the produced reads
	 * @param desiredReadLength - The desired read length. If the length is provided as <=0, no overlapping of
	 * reads take place, and the FASTA sequence is simply split according to the co-ordinates. If the read length
	 * is provided as +ve, it should be greater than the OFFSET parameter in the code. The OFFSET defines the length
	 * of overlap between reads.   
	 */
	public void readFastaAndExtractSubSequences(String sequenceFastaFile, String accessionOfInterest,
			String outputFastaFile, int desiredReadLength){
		
		int OFFSET = 30; 
		if(desiredReadLength <= OFFSET){
			System.out.println("The desired read length needs to be more than the offset. Present offset = " + OFFSET + 
					".Change the desired read length or the OFFSET in the code");
			System.exit(0);
		}
			
		
		try{

			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFastaFile)));

			LinkedHashMap<String,DNASequence> fastaContent  = FastaReaderHelper.readFastaDNASequence(new File(sequenceFastaFile));

			DNASequence dna = fastaContent.get(accessionOfInterest);
			
			// The third and the fourth line of FASTQ (fixed quality)
			String qualityLine = "+\n";
			for(int i = 0; i < desiredReadLength; i++)
				qualityLine = qualityLine + "I";
			
			for (String header : exonSequenceHeaders){
				// A typical exon header created above is like - exonID_110059629_110060128
				String [] headerParts = header.split("_");
				Integer start = Integer.parseInt(headerParts[1]);
				Integer end = Integer.parseInt(headerParts[2]);

				String dnaSubSequence = dna.getSubSequence(start, end).getSequenceAsString();
				
				if(desiredReadLength <= 0){
					String towrite = "@" +  header + "\n" + dnaSubSequence + "\n" + qualityLine + "\n";
					out.write(towrite);
				}else{
				
					if(dnaSubSequence.length() >= desiredReadLength){
						for(int i = 0, counter = 1 ; (i + desiredReadLength) <= dnaSubSequence.length(); counter++){
							String __header = header + "_" + counter;
							 String	__subSeq = dnaSubSequence.substring(i, i + desiredReadLength);
							
							String towrite = "@" +  __header + "\n" + __subSeq + "\n"  + qualityLine + "\n";
							out.write(towrite);
							
							i = i + desiredReadLength-OFFSET;
						}
					}
					
				}
			}

			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) throws Exception{
		
		String exonInformationFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Exon-Mapping-MM7/Mouse-Chr7ProteinCoding-knownGenes-Cols-ExonID-exStart-exEnd-GeneID.txt"; 
		int keyColumn = 1;
		int startExonColumn = 2; 
		int endExonColumn = 3;
		
		String fastaFile ="/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Database/Mus-musculus-Chr7.fa"; 
		String accessionOfInterest = "7";
		String outputFastaFile = "tmp/simFasta-exonic-split.fq";
		
		int desiredReadLength = 70;
		
		SimulateFastqFromExons sim = new SimulateFastqFromExons();
		sim.readCoOrdinateFile(exonInformationFile, keyColumn, startExonColumn,endExonColumn);
		sim.readFastaAndExtractSubSequences(fastaFile, accessionOfInterest, outputFastaFile,desiredReadLength);
		
	}

}
