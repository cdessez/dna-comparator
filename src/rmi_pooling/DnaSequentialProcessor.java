package rmi_pooling;

import java.io.File;
import java.rmi.RemoteException;

import edition_distance.SeqAlgorithm;
import edition_distance.SeqDist;

public class DnaSequentialProcessor extends DnaProcessor {

	private static final long serialVersionUID = -546627525655443704L;

	public DnaSequentialProcessor() throws RemoteException {
		super();
	}
	
	@Override
	public SeqDist algorithm(DnaTask task) {
		SeqDist res = SeqAlgorithm.getClosestInFile(task.getSeq(), new File(task.getFileName()));
		return res;
	}

}
