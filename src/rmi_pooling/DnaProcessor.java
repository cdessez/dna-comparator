package rmi_pooling;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import edition_distance.SeqDist;

public abstract class DnaProcessor extends UnicastRemoteObject implements DnaProcessorInterface {

	private static final long serialVersionUID = -2313972064563858197L;
	
	public static final int rmiPort = 21600;
	
	private class CalculusThread implements Runnable {
		private DnaTask task;
		private String responseManagerName;
		private Integer token;
		
		public CalculusThread(DnaTask task, String resn, Integer token){
			this.task = task;
			this.responseManagerName = resn;
			this.token = token;
		}
		
		public void run() {
			SeqDist res = algorithm(this.task);
			TaskManagerInterface responseManager = null;
			try {
			     responseManager = (TaskManagerInterface) Naming.lookup ("//" + this.responseManagerName + ":"
			    		 + rmiPort + "/TaskManager");
			} catch (NotBoundException e){
			     System.out.println ("\"//" + this.responseManagerName + "/TaskManager\" does not exist");
			} catch (MalformedURLException e) {
			    System.out.println ("\"//" + this.responseManagerName + "/TaskManager\" not a valid URL ");
			} catch (RemoteException e) {
			    System.out.println ("Some remote exception in lookup"+e);
			}
			try {
				responseManager.giveResult(this.token, res);
				System.out.println("Best result sent to " + this.responseManagerName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public DnaProcessor() throws RemoteException {
		super();
		try {
		    Naming.rebind("rmi://localhost:" + rmiPort + "/DnaProcessor", this);
		} catch (RemoteException e){
		    System.out.println("Remote problem while binding DnaProcessor.");	    
		} catch (MalformedURLException e){
		    System.out.println("bad URL");
		}
		System.out.println("DnaProcessor is up on localhost on port " + rmiPort + ".");
	}
	
	public void launchTask(DnaTask task, String responseManagerName, Integer token) throws RemoteException {
		System.out.println("Task assigned for a " + task.getSeq().length + "-long sequence and the file \"" + task.getFileName()+ "\"");
		System.out.println("Give the result back to : " + responseManagerName);
		CalculusThread runnable = new CalculusThread(task, responseManagerName, token);
		Thread t = new Thread(runnable);
		t.start();
	}
	
	abstract public SeqDist algorithm(DnaTask task);
	
	
}
