package dna_file;

import java.io.File;


public class DnaFileWriter {
	
	BitFileWriter writer ;
	protected int nWrittenSeq = 0 ;
	protected int nTotalSeq = 0 ;
	
	public DnaFileWriter(File file, int nTotalSeq){
		this.nTotalSeq = nTotalSeq ;
		if (file.exists())
			file.delete() ;
		writer = new BitFileWriter(file) ;
		// Ecriture de l'entête du fichier
		writer.write(this.nTotalSeq, 32);
	}
	
	public void writeSeq(int[] seq){
		writer.write(seq.length, 32);
		for(int i = 0 ; i < seq.length ; i++)
			writer.write(seq[i], 2);
		this.nWrittenSeq++;
	}
	
	public int numberOfWrittenSeq(){
		return this.nWrittenSeq;
	}
	
	public void endOfFile(){
		writer.endOfFile() ;
	}
}
