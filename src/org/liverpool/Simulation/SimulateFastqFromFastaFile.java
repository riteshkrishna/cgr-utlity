package org.liverpool.Simulation;
/**
 * Simulation 4: Creates a FASTQ file from all the sequences in a FASTA file
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

public class SimulateFastqFromFastaFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int desiredReadLength = 70;
		int OFFSET = 30; 
		String sequenceFastaFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/Ttru.txt";
		String outputFastqFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/Fastq-Ttru.txt";
		
		
		if(desiredReadLength <= OFFSET){
			System.out.println("The desired read length needs to be more than the offset. Present offset = " + OFFSET + 
					".Change the desired read length or the OFFSET in the code");
			System.exit(0);
		}
			
		
		try{

			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFastqFile)));

			LinkedHashMap<String,DNASequence> fastaContent  = FastaReaderHelper.readFastaDNASequence(new File(sequenceFastaFile));

			Iterator<String> accessions = fastaContent.keySet().iterator();
			while(accessions.hasNext()){
				String accession = accessions.next();
				DNASequence dna = fastaContent.get(accession);
				
				// The third and the fourth line of FASTQ (fixed quality)
				String qualityLine = "+\n";
				for(int i = 0; i < desiredReadLength; i++)
					qualityLine = qualityLine + "I";
				
				Integer start = 1;
				Integer end   = dna.getLength();
				
				String dnaSubSequence = dna.getSubSequence(start, end).getSequenceAsString();
				
				// Remove the '-' characters from the sequence which appear from MSA
				dnaSubSequence = dnaSubSequence.replaceAll("-", "");
				
				String header = accession;
				
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

}
