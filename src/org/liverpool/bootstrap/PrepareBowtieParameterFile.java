package org.liverpool.bootstrap;

public class PrepareBowtieParameterFile {

	/**
	 * @param bowtie_home = Bowtie home
	 * @param bowtie_db_path = Bowtie index
	 * @param fastq_path = query fastq file
	 * @param sam_path = SAM file o
	 * @param screenout_path
	 * @param int_seed_length = L parameter
	 * @param int_seed_match = M (0/1) parameter
	 * @param str_seed_overlap = Seed overlap function like 'S,1,0.1'
	 * @param int_d = D parameter
	 * @param int_r = R parameter
	 * @param str_score_fun = score function like '--score-min G,10,1'
	 * @return
	 */
	public String prepareBowtieRunScript(String bowtie_home,String bowtie_db_path, String fastq_path,
			String sam_path, String screenout_path,
			int int_seed_length, int int_seed_match, 
			String str_seed_overlap, int int_d, int int_r, String str_score_fun){
		
		String parameterFileContent = "#!/bin/sh  \n"+
				"BT2_HOME=" + bowtie_home + "\n" +
				"# The Datasets \n" +
				"BOWTIE_DB=" + bowtie_db_path + "\n" +
				"QUERY_FILE=" + fastq_path + "\n" +
				"SAM_FILE=" + sam_path + "\n" +
				"STDOUT_CAPTURE=" + screenout_path + "\n\n" + 
	 
				"## Custom parameters \n" + 
				"L=" + int_seed_length + "\n" +
				"N=" + int_seed_match + "\n" +
				"seed_overlap='" + str_seed_overlap + "' \n" + 
				"D=" + int_d + "\n" +
				"R=" + int_r + "\n" +
				"score='" + str_score_fun + "'\n" +

				"# Bowtie command \n" +
				"$BT2_HOME/bowtie2 -x $BOWTIE_DB -t -p 6 -D $D -R $R -L $L -N $N -i $seed_overlap --score-min $score -U $QUERY_FILE -S $SAM_FILE 2>&1 1>$STDOUT_CAPTURE | tee -a -i $STDOUT_CAPTURE" +
				"\n";
	
		return parameterFileContent;
	
	}
	
	public static void main(String[] args) {
		
		String bowtie_home = "xx";
		String bowtie_db_path = "xx";
		String fastq_path = "xx";
		String sam_path = "xx"; 
		String screenout_path = "xx";
		int int_seed_length = 20;
		int int_seed_match = 0;
		String str_seed_overlap = "xx"; 
		int int_d = 5; 
		int int_r = 3; 
		String str_score_fun = "xx";
		
		PrepareBowtieParameterFile pb = new PrepareBowtieParameterFile();
		String command = pb.prepareBowtieRunScript(bowtie_home,bowtie_db_path, fastq_path, sam_path, screenout_path, int_seed_length, int_seed_match, str_seed_overlap, int_d, int_r, str_score_fun);
		
		System.out.println(command);

	}

}
