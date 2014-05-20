package org.liverpool.OtherBioinfJobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

/**
 * The files in this folder are used for general bioinformatics jobs that may be unrelated 
 * to this project.
 * 
 * This program extracts protein sequences from a 6-frame translated FASTA file. The stop-codons
 * are marked with *. We read one sequence at a time, and breaks that sequence into multiple sequences
 * according to the occurrence of the start and end codons.
 *  
 * @author ritesh
 *
 */
public class ExtractProteinsFromFasta {


	public static void main(String[] args) throws Exception{
		
		String sequence_six_frame_FastaFile = "/Users/ritesh/Ritesh_Extra_Work/Wolbachia/RE__Wolbachia_proteogenomics/Wolbachia-whole-genome-AA.fa";
		String outProteinFastaWithMstart = "tmp/tmp.txt";
		String outProteinFastaWith_nonM_start = "tmp/tmp_nonM.txt";
		int minProteinLength = 8; // The minimum length required for the protein
		
		BufferedWriter out_m = new BufferedWriter(new FileWriter(new File(outProteinFastaWithMstart)));
		BufferedWriter out_non_m = new BufferedWriter(new FileWriter(new File(outProteinFastaWith_nonM_start)));
		
		LinkedHashMap<String,ProteinSequence> fastaContent  = FastaReaderHelper.readFastaProteinSequence(new File(sequence_six_frame_FastaFile));
		
		Iterator<String> accessions = fastaContent.keySet().iterator();
		while(accessions.hasNext()){
			String accession = accessions.next();
			ProteinSequence proteinSeq = fastaContent.get(accession);
			String protein = proteinSeq.toString();
			
			// parse this sequence into multiple sequences, * represents end-codon
			StringTokenizer st = new StringTokenizer(protein,"*");
			
			int count = 1;
			while(st.hasMoreElements()){
				String token = st.nextToken();
				
				System.out.println(token);
				
				// filter proteins that start with M
				int index_M = token.indexOf('M');
				if(index_M != -1){
					String m_start_protein = token.substring(index_M, token.length());	
					if(m_start_protein.length() >= minProteinLength)
						out_m.write(">" + accession + "_"+ count +"\n" + m_start_protein + "\n");
				}
				
				// Proteins with other start sites than M
				if(token.length() >= minProteinLength)
					out_non_m.write(">" + accession + "_"+ count +"\n" + token + "\n");
				
				count++;
			}

		}
		 out_m.close();
		 out_non_m.close();

	}

}
