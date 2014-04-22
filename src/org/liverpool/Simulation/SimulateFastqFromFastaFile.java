package org.liverpool.Simulation;
/**
 * Simulation 4: Creates a FASTQ file from all the sequences in a FASTA file
 * 
 * Usage :
 * -read_length 150 -offset 70 -sequenceFastaFile fastaFile.fa -outputFastqFile fastqFile.fq
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.liverpool.utils.GetCommandLineParameters;

public class SimulateFastqFromFastaFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length < 8){
			String cmd = "-read_length readLength -offset offset -sequenceFastaFile fastaFile -outputFastqFile fastqFile"; 
            System.out.println(cmd);
		}
        /*
		// note - 13/3/2014 - this setting will be used from now on 
		int desiredReadLength = 150;
		int OFFSET = 70; 
		String sequenceFastaFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/Cfam.txt";
		String outputFastqFile = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript/FASTQ-Sequences/length-150/Fastq-Cfam-150.txt";
		*/
        
		int desiredReadLength = Integer.parseInt(GetCommandLineParameters.getCmdParameter(args, "read_length", true));
		int OFFSET = Integer.parseInt(GetCommandLineParameters.getCmdParameter(args, "offset", true));
		String sequenceFastaFile = GetCommandLineParameters.getCmdParameter(args, "sequenceFastaFile", true);
		String outputFastqFile = GetCommandLineParameters.getCmdParameter(args, "outputFastqFile", true);
		
		
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
