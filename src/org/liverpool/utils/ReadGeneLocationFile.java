package org.liverpool.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This program takes as input a TSV file with co-ords for each gene, where the fields are -
 * GeneID    start   end
 * 
 * It will parse through the file and create a HashMap with the contents. The input file can
 * be obtained from BioMart@Ensemble where genes of interest can be queried and the result can
 * be downloaded as a TSV file.
 * 
 * @author ritesh
 *
 */
public class ReadGeneLocationFile {
	
	String fileDelimiter = "\t";
	String inputFile;
	HashMap<String, int[] > geneLocationMap;
	
	public ReadGeneLocationFile(String inputFile){
		this.inputFile = inputFile;
		this.geneLocationMap = new HashMap<String, int[]>();
	}
	
	
	void createHashFromFile(){
		try{
			Scanner scanner = new Scanner(new File(this.inputFile));
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				if(line.isEmpty())
					continue;
				
				String [] records = line.split(fileDelimiter,-1);
				if(records.length < 3)
					continue;
				
				
					String gene = records[0];
					int start = Integer.parseInt(records[1]);
					int end = Integer.parseInt(records[2]);
					
					int [] location = new int[2];
					location[0] = start;
					location[1] = end;
					this.geneLocationMap.put(gene, location);
			}
			
			scanner.close();
		}catch(NumberFormatException n){
			System.out.println("Problem with Start/End in File for gene");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * The main function which does everything
	 * @return
	 */
	public HashMap<String, int[] > getGeneLocationMap(){
		createHashFromFile();
		return this.geneLocationMap;
	}
}


