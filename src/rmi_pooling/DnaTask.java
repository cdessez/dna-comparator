package rmi_pooling;

import java.io.Serializable;

public class DnaTask implements Serializable {

	private static final long serialVersionUID = 8901217237910379718L;
	
	private int[] seqToCompare;
	private String fileName;
	
	public DnaTask(int[] seqToCompare, String fileName){
		this.seqToCompare = seqToCompare;
		this.fileName = fileName;
	}
	
	public int[] getSeq(){
		return this.seqToCompare;
	}
	
	public String getFileName(){
		return this.fileName;
	}
	
	public String toString(){
		String res = "Sequence : ";
		for(int i = 0; i < this.seqToCompare.length ; i++){
			res += (this.seqToCompare[i]==0 ? 'a' : (this.seqToCompare[i]==1 ? 't' : (this.seqToCompare[i]==2 ? 'g' : 'c')));
		}
		res += "\nFile Name : " + this.fileName;
		return res;
	}
	
}
