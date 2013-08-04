package rmi_pooling;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DnaProcessorInterface extends Remote {
	
	public void launchTask(DnaTask task, String responseManagerName, Integer token) throws RemoteException;
	
	
}
