package edition_distance;

import java.io.File;

import dna_file.DnaFileReader;

public class SeqAlgorithm {
	
	public static int sequentialDistance(int[] s, int[] t){
		int[] d = new int[s.length + 1];
		int m1 = 0, m2 = 0;
		for(int j = 0 ; j <= s.length ; j++)
			d[j] = j ; // à changer en d[j-1] + c si c != 1
		for(int i = 1 ; i <= t.length ; i++){
			m1 = d[0];
			d[0] = d[0] + 1;
			for(int j = 1 ; j <= s.length ; j++){
				m2 = d[j];
				d[j] =  min3(d[j-1] + 1, m1 + (s[j-1]==t[i-1] ? 1 : 0), m2 + 1); // à changer en min3(d[j] + c1, m1 + c2, m2 + c3) si c != 1
				m1 = m2;
			}
		}
		return d[s.length];
	}
	
	public static int min3(int a, int b, int c){
		int ab = a < b ? a : b ;
		return ab < c ? ab : c ;
	}
	
	public static SeqDist getClosestInFile(int[] s, File file){
		DnaFileReader reader = new DnaFileReader(file);
		int nSeq = reader.totalNumberofSeq();
		int[] bestSeq = null, curSeq = null;
		int bestDist = Integer.MAX_VALUE, curDist = Integer.MAX_VALUE;
		for(int i = 0 ; i < nSeq ; i++){
			curSeq = reader.readSequence();
			curDist = sequentialDistance(s, curSeq);
			System.out.println("Process distance #"+i+": "+curDist);
			if (curDist < bestDist){
				bestSeq = curSeq;
				bestDist = curDist;
			}
		}
		return new SeqDist(bestSeq, bestDist);
	}
}
