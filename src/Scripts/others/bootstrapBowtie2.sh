# The Datasets
BOWTIE_DB=~/Bowtie-2-Indices/Ensembl-Human-gene-cdna
QUERY_FILE=Fastq-Cfam.fq
SAM_FILE=cfam-local-cust-3.sam
STDOUT_CAPTURE=screenout.txt
 
## Custom parameters
L=10 # Seed length 
N=1; # Allow mismatch in seed (0 default, 1 means one mismatch)
seed_overlap='S,1,0.1'
D=20 ; # Dynamic programming effort
R=3; # Re-seeding attempt
score='--score-min G,10,1'; # 10 reflecting the seed length

# Bowtie command
bowtie2 -x $BOWTIE_DB -t -p 6 -D $D -R $R -L $L -N $N -i $seed_overlap $score -U $QUERY_FILE -S $SAM_FILE 2>&1 1>$STDOUT_CAPTURE | tee -a $STDOUT_CAPTURE 
