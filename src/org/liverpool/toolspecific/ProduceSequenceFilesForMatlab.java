package org.liverpool.toolspecific;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.liverpool.utils.GetCommandLineParameters;

/**
 * This code produces bedtools processed files that are required for the
 * matlab rountines for further processing. The program internally calls 
 * bedtools, so the path for bedtools need to be provided duing the call. 
 * 
 * Example run:
 * -bedtoolpath /Users/ritesh/Software/bedtools-2.17.0/bin/bedtools 
 * -referencefasta /Users/ritesh/tmp/Ensembl-Human-knownprotCoding-unspliced-genes.fa -inputsam /Users/ritesh/tmp/out.sam
 *  -outputbed /Users/ritesh/tmp/out.bed -outputsamsequence /Users/ritesh/tmp/samseq.fa 
 *  -outputrefsequence /Users/ritesh/tmp/refseq.fa
 *  
 * @author ritesh
 *
 */
public class ProduceSequenceFilesForMatlab {
	
	public void processViaBedtools(String bedtoolsPath,String referenceFasta,String inputSAMFile,
			String outputBEDFile,String outputSamReadSequenceFile,String outputRefReadSequenceFile){
		
		try{
			// produce a bed file corresponding to the information in the input SAM file
			// Also, produce a tab-fasta file for reads in the SAM file
			CreateBEDFileFromSAMFile cb = new CreateBEDFileFromSAMFile();
			cb.processSAMFile(inputSAMFile,outputBEDFile,outputSamReadSequenceFile);
		
			// Run bedtools and produce a tab-fasta with reads extracted from reference fasta file
			//$bedtools getfasta -fi reference.fa -bed test.bed -fo test.fa.out -s -name -tab
		
			if(!new File(bedtoolsPath).isFile()){
				System.out.println("Set the path for the correct bedtools executable...exiting now.");
				System.exit(0);
			}
			String badtoolsCommand  = bedtoolsPath +" getfasta -fi " +  referenceFasta + " -bed " + outputBEDFile + " -fo " + outputRefReadSequenceFile + " -s -name -tab";
			Process process = Runtime.getRuntime().exec(badtoolsCommand);
		
			InputStream is  = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while((line = br.readLine()) != null){
					System.out.println(line);
			}
			process.waitFor();
		
			int exitValue = process.exitValue();
			System.out.println(exitValue);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			
			if(args.length < 8){
				String cmd = "-bedtoolpath bedpath -referencefasta referencefile -inputsam samfile -outputbed bedfile -outputsamsequence samreads -outputrefsequence refreads"; 
	            System.out.println(cmd);
			}
	        
			ProduceSequenceFilesForMatlab psm = new ProduceSequenceFilesForMatlab();
			
			String bedtoolsPath = GetCommandLineParameters.getCmdParameter(args, "bedtoolpath", true);
			String referenceFasta = GetCommandLineParameters.getCmdParameter(args, "referencefasta", true);
			
			String inputSAMFile = GetCommandLineParameters.getCmdParameter(args, "inputsam", true);
			String outputBEDFile = GetCommandLineParameters.getCmdParameter(args, "outputbed", true);
			String outputSamReadSequenceFile = GetCommandLineParameters.getCmdParameter(args, "outputsamsequence", true);
			String outputRefReadSequenceFile = GetCommandLineParameters.getCmdParameter(args, "outputrefsequence", true);
			
			psm.processViaBedtools(bedtoolsPath, referenceFasta, inputSAMFile, outputBEDFile, outputSamReadSequenceFile, outputRefReadSequenceFile);
			
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		 
		
	}

}
