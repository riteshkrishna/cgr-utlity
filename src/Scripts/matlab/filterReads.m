%% Code to filter a list of reads from a FASTQ file
% listOfReadsToFilter is a TAB file with more than one columns, where the
% first column is the list of reads which need to be filtered. The
% fastqFile is the source FASTQ file which will be read to extract the 
% desired reads. The filtered reads will be written to outputFile

function filterReads(fastqFile, listOfReadsToFilter,outputFile)

% Read the list of reads to filter
fileID = fopen(listOfReadsToFilter);
desiredReads = textscan(fileID, '%s %s');
fclose(fileID);

% Replace all @ symbol in the beginning of each accession
desiredReads = regexprep(desiredReads{1}, '@', '', 'ignorecase');

% Read the FASTQ file
reads = fastqread(fastqFile);
AllHeaders = {reads(:).Header};

fout = fopen(outputFile,'w');

for i=1:size(desiredReads)
    read = desiredReads{i};
    
    [truefalse,index] = ismember(read,AllHeaders);
    if truefalse == true
        header = reads(index).Header;
        sequence = reads(index).Sequence;
        quality = reads(index).Quality;
        
        outString = sprintf('>@%s\n%s\n+\n%s\n',header,sequence,quality);
        fprintf(fout,'%s',outString);
       
    end
end

fclose(fout);
end