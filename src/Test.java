import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;

import rmi_pooling.DnaSequentialProcessor;
import rmi_pooling.TaskManager;
import dna_file.Generator;
import edition_distance.SeqAlgorithm;


public class Test {
	
	
	public static void main(String[] args){
		long initialTimestamp = new Date().getTime();
		
		if (args.length == 1){
			if (args[0].equals("seq_processor"))
				try {
					new DnaSequentialProcessor();
				} catch (RemoteException e) { e.printStackTrace(); }
		} else {
			//testSequentialDistance("1.dna", 16*1024);
			testRmiSequentialDistance();
			
			long secs = (new Date().getTime() - initialTimestamp) / 1000;
			System.out.println("\nTemps d'éxecution total : " + secs/3600 + "h " + (secs/60)%60 + "m " + secs%60 + "s");
		}
	}
	
	public static void testSequentialDistance(String fileName, int sequenceSize){
		int[] seq = Generator.getRandomSeq(sequenceSize);
		File file = new File(fileName);
		System.out.println(SeqAlgorithm.getClosestInFile(seq, file));
	}
	
	public static void testRmiSequentialDistance(){
		int[] seq = Generator.getRandomSeq(16*1024);
		String[] fileNames = {
			"1.dna",
			"2.dna",
			"3.dna"
		};
		String[] hosts = {
			"rover.polytechnique.fr",
			"royce.polytechnique.fr",
			"skoda.polytechnique.fr"
		};
		TaskManager tm;
		try {
			tm = new TaskManager("rolls.polytechnique.fr");
			tm.setSeq(seq);
			tm.setFileNames(fileNames);
			tm.setProcessorHosts(hosts);
			System.out.println("Best distance: " + tm.process().dist);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
}
