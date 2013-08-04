package dna_file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DnaFileReader {
	
	BitFileReader reader ;
	protected int nReadSeq = 0 ;
	protected int nTotalSeq = 0 ;
	
	public DnaFileReader(File file) {
		
		try {
			reader = new BitFileReader(file) ;
			nTotalSeq = reader.readBits(32, false);
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
		
	
	public int[] readSequence() {
		int size, sizeMod4;
		int[] result = null;
		try {
			size = reader.readBits(32, false);
			sizeMod4 = (size >> 2) << 2; 
			result = new int[size];
			for(int i = 0 ; i < sizeMod4 ; i += 4 ){
				result[i] = reader.readByteToDnaArray(result, i);
			}
			///// !!!! \\\\\
			this.nReadSeq++;
		} catch (IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public int numberOfReadSeq(){
		return this.nReadSeq ;
	}
	
	public int totalNumberofSeq(){
		return this.nTotalSeq;
	}
	
	public void close(){
		reader.close() ;
	}
	
}
