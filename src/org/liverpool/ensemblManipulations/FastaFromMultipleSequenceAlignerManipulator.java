package org.liverpool.ensemblManipulations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

/**
 * The program is to manipulate the FASTA produced by Multiple Sequence Aligners 
 * like clustalW etc. The aim is to first, select the accessions of interest, and
 * then, extract the chunks of sequences from the locations(columns in MSA) where
 * the alignment agreed for all the sequences.
 * 
 *  Example - In the alignment sample below, only the sub-sequences within "" will be selected
 *  for each accession, as those are the consensus locations across all the sequences.
 *  
 *  seq 1 = "ATG"GCTGCACCGCAACTTGGAAAATCTGTCTTC"TATGATCTGTTTAGC"
    seq 2 = "CTG"---GTTGGAACCAAAGAGAGGCCAACGTTT"TTTGAGATTTTTAAA"
    seq 3 = "ATG"------------------------------"TACGAGACATTCAAA"
    seq 4 = "ATG"CCTATTGGGTCCAAGGAGAGACCAACTTTT"TTTGAAATTTTTAAG" 
 * 
 * @author ritesh
 *
 */
public class FastaFromMultipleSequenceAlignerManipulator {

	public char pad_identifier = '-';
	
	public HashMap<String, ArrayList<Integer>> map_accession_padStartLocations = new HashMap<String, ArrayList<Integer>>();
	public HashMap<String, ArrayList<Integer>> map_accession_padEndLocations = new HashMap<String, ArrayList<Integer>>();
	
	public void findAlignmentLocations(String sequenceFastaFile, ArrayList<String>accessionsOfInterest){
		
		try{
			LinkedHashMap<String,DNASequence> fastaContent  = FastaReaderHelper.readFastaDNASequence(new File(sequenceFastaFile));

			Iterator<String> accessions = fastaContent.keySet().iterator();
			while(accessions.hasNext()){
				
				String accession = accessions.next();
				if(accessionsOfInterest.contains(accession)){
					String seq = fastaContent.get(accession).getSequenceAsString();
					
					// find locations of all blocks and put it in an ArrayList, will add -1 in end
					ArrayList<Integer> start_indices = new ArrayList<Integer>();
					ArrayList<Integer> end_indices = new ArrayList<Integer>();
					
					for(int i = 0 ; i < seq.length() ; i++){
						if(seq.charAt(i) == pad_identifier)
							continue;
						else{
							start_indices.add(i+1); // Counts from 1 in BioJava
							do{
								i++;
							}while((i < seq.length()) && (seq.charAt(i) != pad_identifier) );
							end_indices.add(i); // Counts from 1 in BioJava
						}
					}
					
					map_accession_padStartLocations.put(accession, start_indices);
					map_accession_padEndLocations.put(accession, end_indices);
				}
				
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	private HashMap<Integer,Integer> findCommonStartLocations(){
		HashMap<Integer,Integer> startLoc_count_map = new HashMap<Integer,Integer>();
		
		Iterator<String> accessions = map_accession_padStartLocations.keySet().iterator();
		while(accessions.hasNext()){
			String accession  = accessions.next();
			ArrayList<Integer> starts = map_accession_padStartLocations.get(accession);
			for(Integer start : starts){
				if(startLoc_count_map.containsKey(start)){
					Integer count = startLoc_count_map.get(start);
					startLoc_count_map.put(start, count + 1);
				}else startLoc_count_map.put(start, 1);
			}
		}
		
		return startLoc_count_map;
	}
	
	/**
	 * 
	 * @param sequenceFastaFile
	 * @param accessionsOfInterest
	 * @param count_threshold = At least how many accessions should contain same start locations
	 * @param outFasta
	 * @throws Exception
	 */
	public void processMSAFastaFile(String sequenceFastaFile, ArrayList<String>accessionsOfInterest,
			int count_threshold, String outFasta) throws Exception{
		
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFasta)));
		
		findAlignmentLocations(sequenceFastaFile, accessionsOfInterest);
		HashMap<Integer,Integer> common_starts = findCommonStartLocations();
		
		
		LinkedHashMap<String,DNASequence> fastaContent  = FastaReaderHelper.readFastaDNASequence(new File(sequenceFastaFile));
		
		Iterator<String> accessions = map_accession_padStartLocations.keySet().iterator();
		while(accessions.hasNext()){
			
			String accession = accessions.next();
			DNASequence dna = fastaContent.get(accession);
			
			ArrayList<Integer> starts = map_accession_padStartLocations.get(accession);
			
			String fastaToForm = new String();
			
			for(int i = 0 ; i < starts.size(); i++){
				int start = starts.get(i);
				if(common_starts.containsKey(start)){
					if(common_starts.get(start) >= count_threshold){
						System.out.println("Start : " + start + "\t count : " + common_starts.get(start));
						int end = map_accession_padEndLocations.get(accession).get(i);
						String dnaSubSequence = dna.getSubSequence(start, end).getSequenceAsString();
						fastaToForm = fastaToForm + dnaSubSequence;
					}
				}
			}
			
			String toWrite = ">" + accession + "\n" + fastaToForm + "\n";
			out.write(toWrite);
		}
		
		out.close();
		
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String [] args) throws Exception{
		
		String fastaFile = "tmp/ENSG00000139618-cds.fa";
		
		ArrayList<String> accessions = new ArrayList<String>();
		accessions.add("ENSP00000439902_90_Hsap_/1-10254"); // H Sap
		accessions.add("ENSPTRP00000009812_125_Ptro_/1-10254"); // Pan troglodytes - Chimpanzee
		accessions.add("ENSNLEP00000001277_115_Nleu_/1-10257"); // Gibbon (Nomascus leucogenys)
		accessions.add("ENSECAP00000013146_61_Ecab_/1-10257"); // Horse (Equus caballus)
		accessions.add("ENSTTRP00000010004_80_Ttru_/1-9921"); // Dolphin (Tursiops truncatus)
		accessions.add("ENSCAFP00000009557_135_Cfam_/1-10338"); // Dog (Canis lupus familiaris)
		accessions.add("ENSMUSP00000038576_134_Mmus_/1-9987"); // Mouse (Mus musculus)
		accessions.add("ENSMODP00000033276_46_Mdom_/1-10005"); // Opossum (Monodelphis domestica)
		accessions.add("ENSGALP00000027524_42_Ggal_/1-10191"); // Chicken (Gallus gallus)
		accessions.add("ENSDARP00000099674_110_Drer_/1-8622"); // "Zebrafish (Danio rerio)"
		
		
		int count_threshold = 5; 
		String outFasta = "tmp/MSA-thresh5.fa";
		
		FastaFromMultipleSequenceAlignerManipulator fm = new FastaFromMultipleSequenceAlignerManipulator();
		fm.processMSAFastaFile(fastaFile, accessions, count_threshold, outFasta);
		
	}
	
}
