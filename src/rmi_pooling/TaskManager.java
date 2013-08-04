package rmi_pooling;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import edition_distance.SeqDist;

public class TaskManager extends UnicastRemoteObject implements TaskManagerInterface {

	private static final long serialVersionUID = 3472322934829727244L;
	
	public static final int rmiPort = 21600;
	
	private String[] fileNames;
	private int[] seqToCompare;
	private String[] processorHosts;
	private SeqDist[] results;
	private String myHostName;
	
	private boolean allResponsesReceived = false;
	
	public TaskManager(String hostName) throws RemoteException {
		super();
		this.myHostName = hostName;
	}
	
	public void setFileNames(String[] fileNames){
		this.fileNames = fileNames;
		this.results = new SeqDist[fileNames.length];
		for (int i = 0 ; i < this.results.length ; i++)
			this.results[i] = null;
	}
	
	public void setSeq(int[] seq){
		this.seqToCompare = seq;
	}
	
	public void setProcessorHosts(String[] hosts){
		this.processorHosts = hosts;
	}
	
	public SeqDist process(){
		
		// bind to rmi
		try {
		    Naming.rebind("rmi://localhost:" + rmiPort + "/TaskManager", this);
		} catch (RemoteException e){
		    System.out.println("Remote problem while binding TaskManager.");	    
		} catch (MalformedURLException e){
		    System.out.println("bad URL");
		}
		System.out.println("TaskManager is up on localhost on port " + rmiPort + ".");
		
		// launches the calculus on all the remote DnaProcessor objects
		int cur_proc_id = 0;
		DnaProcessorInterface cur_proc = null;
		for (int i = 0 ; i < this.fileNames.length ; i++){
			try {
			     cur_proc = (DnaProcessorInterface) Naming.lookup ("//" + this.processorHosts[cur_proc_id] + ":"
			    		 + rmiPort + "/DnaProcessor");
			} catch (NotBoundException e){
			     System.out.println ("\"//" + this.processorHosts[cur_proc_id] + "/DnaProcessor\" does not exist");
			} catch (MalformedURLException e) {
			    System.out.println ("\"//" + this.processorHosts[cur_proc_id] + "/DnaProcessor\" not a valid URL ");
			} catch (RemoteException e) {
			    System.out.println ("Some remote exception in lookup"+e);
			}
			try {
				cur_proc.launchTask(new DnaTask(this.seqToCompare, this.fileNames[i]), this.myHostName, new Integer(i));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			System.out.println("Research in \"" + this.fileNames[i] + "\" given to " + this.processorHosts[cur_proc_id]);
			cur_proc_id = (cur_proc_id + 1) % this.processorHosts.length;
		}
		
		// waiting for the processors' results
		while (!this.allResponsesReceived)
			Thread.yield();
		
		// processes the best
		SeqDist best = this.results[0];
		for (int i = 1 ; i < this.results.length ; i++)
			if (best.dist > this.results[i].dist)
				best = this.results[i];
		
		return best;
	}
	
	public void giveResult(Integer token, SeqDist result) throws RemoteException {
		this.results[token.intValue()] = result;
		System.out.println("Response #" + token + " received from " + this.processorHosts[token % this.processorHosts.length]);
		if (this.allResultsReceived())
			this.allResponsesReceived = true;
	}
	
	private boolean allResultsReceived(){
		for(int i = 0 ; i < this.results.length ; i++)
			if (this.results[i] == null)
				return false;
		return true;
	}
	
}
