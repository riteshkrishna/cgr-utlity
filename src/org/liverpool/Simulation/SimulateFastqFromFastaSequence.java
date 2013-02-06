package org.liverpool.Simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

/**
 * Simulation scheme 3 : Create reads from a FASTA file containing a single sequence 
 * @author ritesh
 *
 */
public class SimulateFastqFromFastaSequence {

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
			
			Integer start = 1;
			Integer end   = dna.getLength();
			
			String dnaSubSequence = dna.getSubSequence(start, end).getSequenceAsString();
			
			String header = accessionOfInterest;
			
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
				

			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	
	public static void main(String[] args) {
		String fastaFile ="/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/mapped_data/Simulated-Data/Rad1_featureStrand_Mouse.fasta.txt"; 
		String accessionOfInterest = "15_Rad1_dna:GRCm38:15:10486018:10499063:1";
		String outputFastaFile = "tmp/Mouse_Rad1-split.fq";
		
		int desiredReadLength = 70;
		
		SimulateFastqFromFastaSequence sim = new SimulateFastqFromFastaSequence();
		sim.readFastaAndExtractSubSequences(fastaFile, accessionOfInterest, outputFastaFile,desiredReadLength);
		
	}

}
