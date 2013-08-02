package org.liverpool.toolspecific;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We want to create a BED file from the information stored in a SAM file. Our purpose is
 * to retrieve the snippets of the reference sequences where the reads have been mapped.
 * Every mapped read has a RNAME (ref accession), POS (start) and CIGAR. A properly parsed CIGAR can indicate that
 * how much of the read was mapped, this quantity can be added to the corresponding POS to
 * determine the end-location of the snippet (call it end). This way, we have three essential
 * columns needed for a BED format - (ref accession, start, end). The FLAG = 16 in the second 
 * column of the SAM file indicates that the read is mapped on negative strand, this gives
 * us five columns for the BED format (ref accession, start, end, score (0), orientation).
 * 
 *  Once the BED file is created, we can use BEDtools to extract the sequences from the reference
 *  FASTA file. 
 *   
 *  NOTE : This program considers only the CIGARs with M, D and I operations, it will ignore reads
 *  with N, S, H and P operations.
 *   
 * @author ritesh
 *
 */
public class CreateBEDFileFromSAMFile {
	
	private static final Pattern VALID_CIGAR_PATTERN = Pattern.compile("[0-9]+|[MDINSHP]+");
	
	final int QNAME_col = 0;
	final int FLAG_col = 1;
	final int RNAME_col = 2;
	final int POS_col = 3;
	final int CIGAR_col = 6;
	final int SEQ_col = 9;
	
	
	public void processSAMFile(String inputSAMFile, String outputBEDFile){
		try{
			
			BufferedWriter out_bed = new BufferedWriter(new FileWriter(new File(outputBEDFile)));
			
			Scanner scanner = new Scanner(new File(inputSAMFile));
						
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				if(line.startsWith("@SQ"))
					continue;
				
				String [] records = line.split("\t",-1);
				if(records.length < 11)
					continue;
				
				// Extract info for all the reads with a present mapping
				if(!records[RNAME_col].equals("*")){
					
					String read_id 		= records[QNAME_col];
					String flag_id 		= records[FLAG_col];
					String reference_id = records[RNAME_col];
					String str_pos 		= records[POS_col];
					String cigar 		= records[CIGAR_col];
					String sequence   	= records[SEQ_col];
					
					int pos = Integer.parseInt(str_pos);
					int read_length = sequence.length();
					int end = parseCIGARtoFindEndCoordianate(pos,read_length,cigar);
					
					int flag = Integer.parseInt(flag_id);
					String strand = "+";
					if (flag == 16){
						strand = "-";
					}
					if (end != 0){
						// start = (pos-1) as SAM starts from 1; end = end (as BED doesn't include the last position)
						String bed_line = reference_id + "\t" + (pos-1) + "\t" + end + "\t" + read_id + "\t" + 0 + "\t" + strand + "\n";
						out_bed.write(bed_line);
					}
				}
				
			} // end of while
			
			out_bed.close();
			scanner.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Compute the end co-ordinate of of the reference string using the information
	 * in the cigar string and the pos location.
	 * 
	 * Ex: For CIGAR of 2M2I2M2D1M1I1M1I, and read length = 10, and pos = 1
	 * Ref  - 1 2 _ _ 3 4 5 6 7 _ 8 _
	 * Read - 1 2 3 4 5 6 _ _ 7 8 9 10
	 * 
	 * The (start,end) for ref would be (1,8)
	 * 
	 * Formula : end = (pos + read_length - insertions + deletions - 1)
	 * 
	 * @param pos
	 * @param read_length
	 * @param cigar
	 * @return
	 */
	public int parseCIGARtoFindEndCoordianate(int pos, int read_length, String cigar){
		int endPosition = 0;
		
		int total_M = 0;
		int total_I = 0;
		int total_D = 0;
		
		Iterator<String> chunks = parseCIGAR(cigar).iterator();
		String prev_chunk = new String();
		if(chunks.hasNext())
			prev_chunk = chunks.next();
		
		while(chunks.hasNext()){
			String chunk = chunks.next();
			if(chunk == "M")
				total_M = total_M + Integer.parseInt(prev_chunk);
			if(chunk == "D")
				total_D = total_D + Integer.parseInt(prev_chunk);
			if(chunk == "I")
				total_I = total_I + Integer.parseInt(prev_chunk);
			else return 0; // Return 0 when you see anything other than M, D or I in CIGAR
			
			prev_chunk = chunk;
			
		}
		
		endPosition = pos + read_length + total_D - total_I - 1;
		return endPosition;
	}
	
	

	private List<String> parseCIGAR(String toParse) {
		
	    List<String> chunks = new LinkedList<String>();
	    Matcher matcher = VALID_CIGAR_PATTERN.matcher(toParse);
	    while (matcher.find()) {
	        chunks.add( matcher.group() );
	    }
	    return chunks;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		String note = "This program considers only the CIGARs with M, D and I operations, it will ignore reads" +
						" with N, S, H and P operations. ";
		 System.out.println(note);
		 
		 String inputSAMFile = "/Users/ritesh/tmp/test.sam";
		 String outputBEDFile = "/Users/ritesh/tmp/test-new.bed";
		 
		 CreateBEDFileFromSAMFile cb = new CreateBEDFileFromSAMFile();
		 cb.processSAMFile(inputSAMFile,outputBEDFile);

	}

}
