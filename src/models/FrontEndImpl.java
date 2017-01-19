package models;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import interfaces.FrontEnd;
import interfaces.TrackingService;

public class FrontEndImpl implements FrontEnd {
	public static final String host = "localhost";
	public static final int portNo = 4443;

	public FrontEndImpl() {
		try {
			FrontEnd instance = this;
			FrontEnd stub = (FrontEnd) UnicastRemoteObject.exportObject((FrontEnd) instance, 0);
			Registry registry = LocateRegistry.createRegistry(portNo);

			registry.bind("FrontEnd", stub);

			System.out.println("Front end has started at port " + portNo);
		} catch (RemoteException ex) {
			System.err.println("Couldn't contact rmiregistry.");
			ex.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RemoteException {
		new FrontEndImpl();
	}

	@Override
	public Message connect(Message message) throws RemoteException {
		System.out.println("Client connect");
		List<TrackingService> onlineServers = listTramService();
		Message reply = null;

		for (TrackingService onlineServer : onlineServers) {
			reply = onlineServer.connect(message);
		}

		return reply;
	}

	@Override
	public Message retrieveNextStop(Message message) throws RemoteException {
		System.out.println("Retrieving next stop");
		List<TrackingService> onlineServers = listTramService();
		Message reply = null;

		for (TrackingService onlineServer : onlineServers) {
			reply = onlineServer.retrieveNextStop(message);
			
		}

		return reply;
	}

	@Override
	public Message updateTramLocation(Message message) throws RemoteException {
		System.out.println("Updating tram location");
		List<TrackingService> onlineServers = listTramService();
		Message reply = null;

		for (TrackingService onlineServer : onlineServers) {
			reply = onlineServer.updateTramLocation(message);
		}

		return reply;
	}

	@Override
	public Message isRPCIdAvailable(Message message) throws RemoteException {
		System.out.println("Checking RPC id");
		List<TrackingService> onlineServers = listTramService();
		Message reply = null;

		for (TrackingService onlineServer : onlineServers) {
			reply = onlineServer.isRPCIdAvailable(message);
		}

		return reply;
	}

	public static List<TrackingService> listTramService() throws RemoteException {
		// Maintain a list of all running RMs
		List<TrackingService> onlineServers = new ArrayList<TrackingService>();
		String statusMessage = null;

		// Get RMI registries
		Registry registry1 = getRegistry(1);
		Registry registry2 = getRegistry(2);
		Registry registry3 = getRegistry(3);
		
		// Check which RMs are online
		TrackingService server1 = null, server2 = null, server3 = null;
		server1 = isRMUp(registry1);
		if (server1 != null) {
			onlineServers.add(server1);
		}
		server2 = isRMUp(registry2);
		if (server2 != null) {
			onlineServers.add(server2);
		}
		server3 = isRMUp(registry3);
		if (server3 != null) {
			onlineServers.add(server3);
		}
		
		// Print statuses of all RMs
		if (onlineServers.contains(server1)) {
			statusMessage = "Replica manager 1 on";
		} else {
			statusMessage = "Replica manager 1 off";
		}
		if (onlineServers.contains(server2)) {
			statusMessage += " | Replica manager 2 on";
		} else {
			statusMessage += " | Replica manager 2 off";
		}
		if (onlineServers.contains(server3)) {
			statusMessage += " | Replica manager 3 on";
		} else {
			statusMessage += " | Replica manager 3 off";
		}

		System.out.println(statusMessage);

		return onlineServers;
	}
	
	// Utility function to return the rightful RMI registry for a RM
	public static Registry getRegistry(int r) throws RemoteException {
		switch (r) {
		case 1:
			return LocateRegistry.getRegistry(TrackingServiceImpl.host, TrackingServiceImpl.portNo);
		case 2:
			return LocateRegistry.getRegistry(TrackingService2Impl.host, TrackingService2Impl.portNo);

		case 3:
			return LocateRegistry.getRegistry(TrackingService3Impl.host, TrackingService3Impl.portNo);
		}
		
		return null;
	}
	
	// Utility function to check if a given RM is running
	public static TrackingService isRMUp(Registry r) {
		TrackingService server = null;
		
		try {
			server = (TrackingService) r.lookup("TrackingService");
			return server;
		} catch (Exception e1) {
			return server;
		}

	}
}
