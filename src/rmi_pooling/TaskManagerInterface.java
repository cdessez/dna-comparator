package rmi_pooling;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edition_distance.SeqDist;

public interface TaskManagerInterface extends Remote {
	
	public void giveResult(Integer token, SeqDist result) throws RemoteException ;
	
}
