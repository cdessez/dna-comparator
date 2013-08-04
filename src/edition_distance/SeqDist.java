package edition_distance;

import java.io.Serializable;

public class SeqDist implements Serializable{
	
	private static final long serialVersionUID = -3507684117360452835L;
	
	public int[] seq = null;
	public int dist;
	
	public SeqDist(int[] seq, int dist){
		this.seq = seq;
		this.dist = dist;
	}
	
	public String toString(){
		String res = "Sequence : ";
		for(int i = 0; i < seq.length ; i++){
			res += (seq[i]==0 ? 'a' : (seq[i]==1 ? 't' : (seq[i]==2 ? 'g' : 'c')));
		}
		res += "\nDistance : " + dist;
		return res;
	}
}
