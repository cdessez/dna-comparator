package rmi_pooling;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import edition_distance.CudaAlgorithm;
import edition_distance.SeqDist;

public class DnaCudaProcessor extends DnaProcessor {

	private static final long serialVersionUID = -1971342151528769009L;

	public DnaCudaProcessor() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public SeqDist algorithm(DnaTask task) {
		SeqDist res = null;
		try {
			res = CudaAlgorithm.getClosestInFile(task.getSeq(), new File(task.getFileName()));
		} catch (IOException e) { e.printStackTrace(); }
		return res;
	}

}
