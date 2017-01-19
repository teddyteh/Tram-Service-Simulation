package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import models.Message;

public interface TrackingService extends Remote {
	public abstract Message connect(Message message) throws RemoteException;

	public abstract Message retrieveNextStop(Message message) throws RemoteException;

	public abstract Message updateTramLocation(Message message) throws RemoteException;

	public abstract Message isRPCIdAvailable(Message message) throws RemoteException;
}
