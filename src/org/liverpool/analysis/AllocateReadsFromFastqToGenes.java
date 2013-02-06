package org.liverpool.analysis;
/**
 * The program parses a FASTQ file and reads the accessions. The FASTQ 
 * is coming from wgsim. The accessions contain identifier_start_end_etc. The start
 * and end are the locations on chromosome where the sequences were picked from.
 * 
 * The program takes additional information about the locations of genes/exons from the
 * ReadGeneLocationFile class, which reads a TSV file with list of genes and their 
 * start and end locations.
 * 
 * The program can return a HashMp of reads and their corresponding gene/exon
 * @author ritesh
 *
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import org.liverpool.utils.GetCommandLineParameters;
import org.liverpool.utils.ReadGeneLocationFile;
import org.liverpool.utils.SortHashMapAccordingToField;

public class AllocateReadsFromFastqToGenes {
	
	String fastqFile;
	HashMap<String, String> read_gene_map;
	ArrayList<String> non_gene_reads;
	
	public AllocateReadsFromFastqToGenes(String fastqFile){
		this.fastqFile = fastqFile;
		this.read_gene_map = new HashMap<String, String>();
		non_gene_reads = new ArrayList<String>();
	}
	
	/**
	 * 
	 * @param geneLocationFile
	 */
	public void processFastqUsingGeneLocationFile(String geneLocationFile){
		// Read the co-ord ranges for all the genes
		ReadGeneLocationFile rg = new ReadGeneLocationFile(geneLocationFile);
		HashMap<String, long[] > geneLocations = rg.getGeneLocationMap();
		
		// Create data-structure for searching the relevant genes
		SortHashMapAccordingToField sm = new SortHashMapAccordingToField(geneLocations);
		sm.fillStart_name_map();
		
		try{
			Scanner scanner = new Scanner(new File(this.fastqFile));
			
			BufferedWriter out_debug = new BufferedWriter(new FileWriter(new File("tmp/readStartEndpositions.txt")));
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(line.startsWith("@")){
					String [] headerparts = line.split("_", -1);
					if(headerparts.length < 3)
						continue;
					
					long read_start = Long.parseLong(headerparts[1]);
					long read_end = Long.parseLong(headerparts[2]);
					
					String geneName = sm.findGeneNameForLocation(read_start,read_end);
				
					if(geneName == null)
						this.non_gene_reads.add(line);
					else this.read_gene_map.put(line, geneName);
					
					String readinfo = line + "\t" + read_start + "\t" + read_end + "\n";
					out_debug.write(readinfo);
				}
			}
			
			out_debug.close();
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the HashMap with (read -> gene) relation
	 * @return
	 */
	public HashMap<String, String>  get_read_gene_map(){
		return this.read_gene_map;
	}
	
	/**
	 * Get a list of non-coding reads
	 * @return
	 */
	public ArrayList<String> get_non_gene_reads(){
		return this.non_gene_reads;
	}
	
	/**
	 * Write the reads belonging to genes in a file
	 * 
	 * @param outputFile The file with read accessions in the first column and the corresponding gene
	 * in the second column.
	 */
	public void writeReadsToFile(String outputFile){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFile)));
			
			Iterator<String> reads = this.read_gene_map.keySet().iterator();
			while(reads.hasNext()){
				String read = reads.next();
				String gene = this.read_gene_map.get(read);
				String towrite = read + "\t" + gene + "\n";
				out.write(towrite);
			}
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	} 
	
	/**
	 * The test function..
	 * @param args
	 * 
	 * Example run 1:
	 * -gene-cords Mouse-Chr7genes-withLocations.txt -fastq MM7-read/MM7-read1.fq -outfile mapped.txt
	 */
	public static void main(String [] args){
	
         if(args.length < 4 ){
        	   String cmd = "-fastq fastaq-file -gene-cords geneLocationFile [-outfile read-gene.txt]"; 
               System.out.println(cmd);
         }else{
               String fastqFile    		 = GetCommandLineParameters.getCmdParameter(args, "fastq", true);
               String geneLocationFile   = GetCommandLineParameters.getCmdParameter(args, "gene-cords", true);
               String outputFile   		 = GetCommandLineParameters.getCmdParameter(args, "outfile", false);
                    
               AllocateReadsFromFastqToGenes ag = new AllocateReadsFromFastqToGenes(fastqFile);
               ag.processFastqUsingGeneLocationFile(geneLocationFile);
		
               System.out.println("The total number of reads belonging to gene  =" + ag.get_read_gene_map().size());
               System.out.println("The total number of reads not belonging to gene  =" + ag.get_non_gene_reads().size());
               System.out.println("The total number of reads not belonging to gene  =" + ag.get_non_gene_reads().get(0));
               if(outputFile != null)
            	   ag.writeReadsToFile(outputFile);
         }
	}
	
}
