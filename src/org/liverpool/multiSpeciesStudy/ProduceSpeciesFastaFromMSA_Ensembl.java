package org.liverpool.multiSpeciesStudy;

import java.io.*;
import java.util.*;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

/**
 * This file takes as input a set of FASTA files containing the Multiple sequence alignment results from Ensembl.
 * Each FASTA file represents a gene and has a fixed number of species with respect to a reference species (Human mostly).
 * The sequences in the FASTA file are in pairs (due to pair-wise MSA between Human-and-other-species). Our task is to
 * read each file and assign the sequences belonging to a particular species to its respective species file. This way, we
 * can create a list of species specific files where each file contains sequences of the respective species. The FASTA 
 * accessions in the new file must contain the gene-name (original file name) as well, so we know that which sequence belongs
 * to which particular gene.   
 * 
 * Note - These file containing the reference species will contain on the last reference sequence from each fasta file, but
 * we don't need it, so we can simply ignore that file.
 *  
 * @author ritesh
 *
 */
public class ProduceSpeciesFastaFromMSA_Ensembl {

	public ArrayList<String> species;
	
	BufferedWriter [] out;
	
	// List of species present in the FASTA files
	public ProduceSpeciesFastaFromMSA_Ensembl(){
		species = new ArrayList<String>();
		species.add("Hsap"); // H Sap
		species.add("Ptro"); // Pan troglodytes - Chimpanzee
		species.add("Nleu"); // Gibbon (Nomascus leucogenys)
		species.add("Ecab"); // Horse (Equus caballus)
		species.add("Ttru"); // Dolphin (Tursiops truncatus)
		species.add("Cfam"); // Dog (Canis lupus familiaris)
		species.add("Mmus"); // Mouse (Mus musculus)
		species.add("Mdom"); // Opossum (Monodelphis domestica)
		species.add("Ggal"); // Chicken (Gallus gallus)
		species.add("Drer"); // "Zebrafish (Danio rerio)"
		
		out = new BufferedWriter[species.size()] ;
	}
	
	
	public void processEachMsaFile(File msaFile){
		
		try{
			// Example of an input file name : ENSG00000000005-cds.fa
			String [] msaNameBits = msaFile.getName().split("-cds"); 
			String geneName = msaNameBits[0];
					
			// Read the fasta file...
			LinkedHashMap<String,DNASequence> fastaContent  = FastaReaderHelper.readFastaDNASequence(msaFile);

			Iterator<String> accessions = fastaContent.keySet().iterator();
			while(accessions.hasNext()){
				
				String accession = accessions.next();
				String seq = fastaContent.get(accession).getSequenceAsString();
				
				//Example accession in the fasta : ENSP00000362122_90_Hsap_/1-951
				String regex = "_";
				String [] tokens = accession.split(regex);
				
				if(tokens.length >=2){
					String species_for_this_seq = tokens[2];
					
					int idx = -1;
					if(species.contains(species_for_this_seq)){
						for(int k = 0; k < species.size(); k++){
							if(species_for_this_seq.matches(species.get(k))){
								idx = k;
								break;
							}
						}
						String seq_info = ">" + geneName + "_" +  accession + "\n" + seq + "\n";
						out[idx].write(seq_info);
					}
				}
				
			}
			// parse accessions and write the seq in corresponding file
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param inputFolder
	 * @param outputFolder
	 */
	public void performFastaSeperation(String inputFolder,String outputFolder){
		try{
			// Create writers
			for(int i=0; i< out.length; i++){
				String outFasta = outputFolder + "/" + species.get(i) + ".txt";
				out[i] = new BufferedWriter(new FileWriter(new File(outFasta)));
			}
			
			// List out the files in the input folder
			File dir = new File(inputFolder);
            FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                            return name.endsWith(".fa");
                    }
            };
            File [] msaFiles = dir.listFiles(filter);
            
            // Process each file
            for(int i = 0; i < msaFiles.length; i++)
            	processEachMsaFile(msaFiles[i]);
            
            // Close all the writers
            for (int k = 0; k < out.length; k++)
            	out[k].close();
            
            
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String inputFolder = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/All-Human-Orth-Gene-HomologScript";
		String outputFolder = "/Users/ritesh/Ritesh_CGR_Work/Ortholog-data/Multi-Species-Approach/FromEnsemble/April/SpeciesWise-All-Human-Orth-Gene-HomologScript";
		ProduceSpeciesFastaFromMSA_Ensembl ps = new ProduceSpeciesFastaFromMSA_Ensembl();
		ps.performFastaSeperation(inputFolder,outputFolder);
	}

}
