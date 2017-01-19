package models;

import java.rmi.RemoteException;

public class AddTramTask implements Runnable {
	@Override
	public void run() {
		try {
			new TramClientImpl();
		} catch (RemoteException e) {
			System.out.println("An error has occured in the thread.");
		}
	}
}
