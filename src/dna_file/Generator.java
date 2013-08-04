package dna_file;
import java.io.File;
import java.util.Date;
import java.util.Random;



public class Generator {
	
	public static void main(String[] args){
		/*
		 * Arguments
		 */
		String[] names = {
				"1.dna",
				"2.dna",
				"3.dna"
		};
		int numberOfSequences = 32;
		int sequenceSize = 16*1024 ; // en nombre de nucléotides (2 bits par nucléotide)
		/////////////////////////////////////////////////////////////////////////////
		
		long initialTimestamp = new Date().getTime();
		generateFiles(names, numberOfSequences, sequenceSize);
		//readingTest("1.dna");
		long secs = (new Date().getTime() - initialTimestamp) / 1000;
		System.out.println("Temps d'éxecution : " + secs/3600 + "h " + (secs/60)%60 + "m " + secs%60 + "s");
	}
	
	public static void generateFiles(String[] fileNames, int nSeq, int seqSize){
		for(int i = 0 ; i < fileNames.length ; i++)
			generateFile(fileNames[i], nSeq, seqSize);
	}
	
	public static void generateFile(String fileName, int nSeq, int seqSize){
		File file = new File(fileName);
		DnaFileWriter writer = new DnaFileWriter(file, nSeq);
		for (int i = 0 ; i < nSeq ; i++){
			writer.writeSeq(getRandomSeq(seqSize));
			System.err.println(fileName + " : seq #" + i);
		}
		writer.endOfFile();
	}
	
	public static int[] getRandomSeq(int size){
		int[] res = new int[size];
		Random r = new Random();
		for(int i = 0 ; i<size ; i++){
			res[i] = r.nextInt(4);
		}
		return res;
	}
	
	public static void readingTest(String fileName){
		DnaFileReader reader = new DnaFileReader(new File("1.dna"));
		int size = reader.totalNumberofSeq();
		for(int i = 0 ; i < size ; i++){
			reader.readSequence();
		}
	}

}
