package org.liverpool.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The program will take a TSV file and read two specific columns.The columns are provided by
 * the user. The program will then create a HashMp from those two columns.
 * 
 * @author ritesh
 *
 */
public class ReadTSVFileForHashMap {
	
	final String delimiter = "\t";
	
	String tsvFile;
	int keyColumn;
	int valueColumn;
	HashMap<String,String> map ;
	
	/**
	 * 
	 * @param tsvFile
	 * @param keyColumn - The column number (starting from 1) which will form the key
	 * @param valueColumn - The column number (starting from 1) which will be the value
	 */
	public ReadTSVFileForHashMap(String tsvFile, int keyColumn, int valueColumn) {
		this.tsvFile = new String(tsvFile);
		map = new HashMap<String,String>();
		
		this.keyColumn = keyColumn - 1;
		this.valueColumn = valueColumn - 1;
	}
	
	
	public HashMap<String,String>  createMap(){
		try{
			Scanner scanner = new Scanner(new File(this.tsvFile));
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				// Comments are made with a # in the beginning of the line
				// Skip comment or empty lines
				if(line.startsWith("#") || line.isEmpty())
					continue;
				
				String [] records = line.split(this.delimiter);
				
				// No empty value allowed
				if(records[this.keyColumn] == null || records[this.valueColumn] == null){
					System.out.println("Key/value value missing");
					System.exit(0);
				}
				
				this.map.put(records[this.keyColumn], records[this.valueColumn]);
			}
			
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return this.map;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String [] args){
		if(args.length < 6){
			String cmd = "-tsv tsv-file -key-column keyColumn -value-column valueColumn"; 
            System.out.println(cmd);
		}
		String tsvFile  = GetCommandLineParameters.getCmdParameter(args, "tsv", true);
        int keyColumn   = Integer.parseInt(GetCommandLineParameters.getCmdParameter(args, "key-column", true));
        int valueColumn = Integer.parseInt(GetCommandLineParameters.getCmdParameter(args, "value-column", true));
        
		ReadTSVFileForHashMap rsv = new ReadTSVFileForHashMap(tsvFile, keyColumn, valueColumn);
		HashMap<String,String> map = rsv.createMap();
		
		System.out.println("Map size = " + map.size());
	}
}
