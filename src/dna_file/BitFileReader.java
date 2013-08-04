package dna_file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class BitFileReader {
	
	DataInputStream dis;
	LinkedList<Integer> buff = new LinkedList<Integer>() ;
	
	public BitFileReader(File file){
		
		try {
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	protected void fillBuffer(){
		int b = 0;
		try {
			b = dis.readByte() ;
		} catch (IOException e) { e.printStackTrace(); }
		for(int i=7 ; i>=0 ; i--){
			this.buff.add((b>>i)&1) ;
		}
	}
	
	public byte readBit(){
		if (buff.isEmpty())
			fillBuffer();
		return buff.poll().byteValue() ;
	}
	
	public byte readByte(){
		fillBuffer();
		byte res = 0 ;
		for(int i=0 ; i<8 ; i++){
			res |= buff.poll().byteValue() ;
			res = (byte) (res << 1) ;
		}
		return res ;
	}
	
	public int readBits(int n, boolean isSigned) throws IOException {
		if (n > Integer.SIZE+1 || n<1)
			throw new IOException();
		for(int m = buff.size() ; m<n ;){
			fillBuffer();
			m+=8;
		}
		int res = (buff.poll()==1 ? (isSigned ? ~0 : 1) : 0) ;
		n--;
		for(; n>0 ; n--){
			res = res << 1 ;
			res |= buff.poll()&1 ;
		}
		return res ;
	}
	
	public int readBits(int n) throws IOException {
		return readBits(n, true);
	}
	
	public int readByteToDnaArray(int[] seqs, int offset){
		int b=0;
		try {
			b = dis.readByte();
		} catch (IOException e) { e.printStackTrace(); }
		seqs[offset++] = b & 0x3 ; b = b >> 2 ;
		seqs[offset++] = b & 0x3 ; b = b >> 2 ;
		seqs[offset++] = b & 0x3 ; b = b >> 2 ;
		seqs[offset] = b & 0x3 ;
		return 4;
	}
	
	public void close(){
		try {
			dis.close() ;
		} catch (IOException e) { e.printStackTrace(); }
	}
}
