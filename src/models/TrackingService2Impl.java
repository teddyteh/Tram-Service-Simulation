package models;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import interfaces.TrackingService;

public class TrackingService2Impl implements TrackingService {
	public static final String host = "localhost";
	public static final int portNo = 4445;

	private static TrackingService instance;
	private static Map<String, Route> routes = new HashMap<String, Route>();
	private static List<String> trams = new ArrayList<String>();
	private static List<Long> rpcIds = new ArrayList<Long>();

	// Singleton instance
	public static TrackingService getInstance() throws RemoteException {
		if (instance == null) {
			instance = new TrackingService2Impl();

			routes.put("1", new Route(1));
			routes.put("96", new Route(96));
			routes.put("101", new Route(101));
			routes.put("109", new Route(109));
			routes.put("112", new Route(112));
		}

		return instance;
	}

	// Default constructor
	public TrackingService2Impl() throws RemoteException {

	}

	// Main method
	public static void main(String[] args) {
		try {
			// Only one instance of the server is allowed
			TrackingService server = getInstance();

			TrackingService stub = (TrackingService) UnicastRemoteObject.exportObject((TrackingService) server, 0);
			Registry registry = LocateRegistry.createRegistry(portNo);

			registry.bind("TrackingService", stub);

			System.out.println("Tracking service has started at port " + portNo);
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

	// Accepts a tram id and route id. Returns a current stop and previous stop
	// if both are valid.
	public Message connect(Message message) throws RemoteException {
		// Create a new RPCMessage
		RPCMessage serverReply = new RPCMessage();
		// Create new Message
		Message replyMsg = new Message();

		// Unmarshall message received from client
		RPCMessage clientRequest = message.unMarshall();
		// data is in this format: tram id, route id
		String[] data = clientRequest.getData();

		serverReply.setStatus((short) -1);

		// Ensure that the request is handled by the connect method
		if (clientRequest.getType() == RPCMessage.REQUEST && clientRequest.getProcedure() == 5) {
			// Set reply values
			serverReply.setType(RPCMessage.REPLY);
			serverReply.setTransactionId(clientRequest.getTransactionId());
			serverReply.setRPCId(clientRequest.getRPCId());
			serverReply.setRequestId(clientRequest.getRequestId());
			serverReply.setProcedure((short) 4);
			serverReply.setStatus((short) 0);
			serverReply.setCsvData("-1");
		}

		// Ensure that tram id is unique, route does exist and route does not
		// already have more than 5 trams
		if (!trams.contains(data[0]) && routes.containsKey(data[1]) && routes.get(data[1]).getTrams().size() <= 5) {
			// Reply with a current stop and previous stop
			Route route = routes.get(data[1]);

			// Current stop will be either the first stop in the route or the
			// last
			int r = new Random().nextInt(1 - 0 + 1) + 0;
			String currentStop = null, previousStop;
			// 0 for right most stop and 1 for left most stop
			if (r == 0) {
				currentStop = Integer.toString(route.getStops().get(0));
				previousStop = Integer.toString(route.getStops().get(1));
				String[] serverReplyData = { currentStop, previousStop };
				serverReply.setData(serverReplyData);
			} else if (r == 1) {
				currentStop = Integer.toString(route.getStops().get(route.getStops().size() - 1));
				previousStop = Integer.toString(route.getStops().get(route.getStops().size() - 2));
				String[] serverReplyData = { currentStop, previousStop };
				serverReply.setData(serverReplyData);
			}

			// Connection successful
			trams.add(data[0]);
			route.addTram(data[0], currentStop);

			System.out.println("Tram #" + data[0] + " has been added to route #" + data[1] + " and is placed at stop #"
					+ currentStop);
		}

		// Store RPCMessage in Message
		replyMsg.marshal(serverReply);

		return replyMsg;
	}

	@Override
	public Message retrieveNextStop(Message message) throws RemoteException {
		// Create a new RPCMessage
		RPCMessage serverReply = new RPCMessage();
		// Create new Message
		Message replyMsg = new Message();

		// Unmarshall message received from client
		RPCMessage clientRequest = message.unMarshall();
		// data is in this format: route id, current stop number, previous stop
		// number
		String[] data = clientRequest.getData();

		serverReply.setStatus((short) -1);

		// Ensure that the request is handled by the retrieveNextStop method
		if (clientRequest.getType() == RPCMessage.REQUEST && clientRequest.getProcedure() == 1) {
			// Validations
			if (!routes.containsKey(data[0]) || !routes.get(data[0]).getStops().contains(Integer.parseInt(data[1]))
					|| !routes.get(data[0]).getStops().contains(Integer.parseInt(data[1]))) {
				// Set reply values
				serverReply.setType(RPCMessage.REPLY);
				serverReply.setTransactionId(clientRequest.getTransactionId());
				serverReply.setRPCId(clientRequest.getRPCId());
				serverReply.setRequestId(clientRequest.getRequestId());
				serverReply.setProcedure((short) 2);
				serverReply.setStatus((short) 0);
				serverReply.setCsvData("-1");
			} else {
				// Get route id from client message
				Route stop = routes.get(data[0]);

				// Set reply values
				serverReply.setType(RPCMessage.REPLY);
				serverReply.setTransactionId(clientRequest.getTransactionId());
				serverReply.setRPCId(clientRequest.getRPCId());
				serverReply.setRequestId(clientRequest.getRequestId());
				serverReply.setProcedure((short) 2);
				serverReply.setStatus((short) 0);
				// Calculate next stop
				int nextStopInt = stop.getNextStop(data[1], data[2]);
				String nextStop[] = { Integer.toString(nextStopInt) };
				serverReply.setData(nextStop);
			}
		}

		// Store RPCMessage in Message
		replyMsg.marshal(serverReply);

		return replyMsg;
	}

	@Override
	public Message updateTramLocation(Message message) throws RemoteException {
		// Create a new RPCMessage
		RPCMessage serverReply = new RPCMessage();
		// Create new Message
		Message replyMsg = new Message();

		// Unmarshall message received from client
		RPCMessage clientRequest = message.unMarshall();
		// data is in this format: route id, tram id, stop id
		String[] data = clientRequest.getData();

		serverReply.setStatus((short) -1);

		// Ensure that the request is handled by the appropriate method
		if (clientRequest.getType() == RPCMessage.REQUEST && clientRequest.getProcedure() == 3) {
			// Get route information
			Route route = routes.get(data[0]);

			// Get all trams in the route
			Map<String, String> trams = (HashMap<String, String>) route.getTrams();
			// Update tram location, add tram to the map if it doesn't exist
			trams.put(data[1], data[2]);

			// Set reply values
			serverReply.setType(RPCMessage.REPLY);
			serverReply.setTransactionId(clientRequest.getTransactionId());
			serverReply.setRPCId(clientRequest.getRPCId());
			serverReply.setRequestId(clientRequest.getRequestId());
			serverReply.setProcedure((short) 4);
			serverReply.setStatus((short) 0);
			serverReply.setCsvData(null);

			System.out.println("Tram #" + data[1] + " in route #" + data[0] + " is now at stop #" + data[2]);
		}

		// Store RPCMessage in Message
		replyMsg.marshal(serverReply);

		return replyMsg;
	}

	// Check if a given RPC id is unique throughout the system
	public Message isRPCIdAvailable(Message message) throws RemoteException {
		// Create a new RPCMessage
		RPCMessage serverReply = new RPCMessage();
		// Create new Message
		Message replyMsg = new Message();

		// Unmarshall message received from client
		RPCMessage clientRequest = message.unMarshall();
		// data is in this format: RPC id
		String[] data = clientRequest.getData();
		long id = Long.parseLong(data[0]);

		// Set reply values
		serverReply.setType(RPCMessage.REPLY);
		serverReply.setTransactionId(clientRequest.getTransactionId());
		serverReply.setRPCId(clientRequest.getRPCId());
		serverReply.setRequestId(clientRequest.getRequestId());
		serverReply.setProcedure((short) 6);
		serverReply.setStatus((short) 0);

		// Check if RPC id already exists
		if (!rpcIds.contains(id)) {
			// Id is available, add to our list of RPC id's
			rpcIds.add(id);
			serverReply.setCsvData("1");
		} else {
			serverReply.setCsvData(null);
		}

		// Store RPCMessage in Message
		replyMsg.marshal(serverReply);

		return replyMsg;
	}
}