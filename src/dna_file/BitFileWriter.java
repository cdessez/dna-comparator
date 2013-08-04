package dna_file;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class BitFileWriter {
	
	DataOutputStream dos ;
	LinkedList<Integer> buff = new LinkedList<Integer>() ;
	
	public BitFileWriter(File file){
		try {
			if (file.exists())
				file.delete() ;
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void write(int data, int size){
		for(int i=size-1 ; i>=0 ; i--){
			this.buff.add((data>>i)&1) ;
		}
		writeInFile() ;
	}
	
	protected void writeInFile(){
		while (buff.size()>=8){
			int b = 0 ;
			for(int i=0 ; i<8 ; i++){
				b = b<<1 ;
				b |= buff.poll()&1 ;
			}
			try {
				dos.writeByte(b) ;
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public void endOfFile(){
		if (!buff.isEmpty()){
			for(int i=8-buff.size() ; i>0 ; i--)
				buff.add(0) ;
			writeInFile() ;
		}
		try {
			dos.close() ;
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
}
