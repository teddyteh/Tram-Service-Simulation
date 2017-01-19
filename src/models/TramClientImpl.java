package models;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interfaces.FrontEnd;

public class TramClientImpl {
	private static Random random = new Random();

	// Local client unique identifiers
	private static int tramCount = 0;
	private static List<Long> transactionIds = new ArrayList<Long>();
	private static long requestId = 0;

	// Client info
	private String tramId, routeId, currentStop, previousStop;
	private int count;

	// Default constructor
	public TramClientImpl() throws RemoteException {
		addTram();
	}

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();

		// Run x trams in 5 threads
		for (int i = 0; i < 5; i++) {
			executor.execute(new AddTramTask());
		}
	}

	// Add a tram
	public void addTram() throws RemoteException {
		try {
			// Connect to the front end server
			Registry registry = LocateRegistry.getRegistry(FrontEndImpl.host, FrontEndImpl.portNo);
			FrontEnd fe = (FrontEnd) registry.lookup("FrontEnd");

			// Reconnect until tram id and route id are valid, server will
			// return a current stop and previous stop when successful
			while (!connect(fe)) {
				connect(fe);
			}

			// Go down the route every random x seconds
			while (true) {
				// Request to server to invoke retrieveNextStop()
				if (retrieveNextStop(fe)) {
					// Request to server to invoke updateTramLocation()
					if (updateTramLocation(fe)) {
						// Sleep randomly for 10-20 seconds before traveling to
						// the
						// next
						// stop
						Random r = new Random();
						int timeToSleep = r.nextInt(20 - 10) + 10;
						System.out.println("[" + this.count + "] Tram #" + tramId + " in route #" + routeId
								+ " is  waiting for " + timeToSleep + " seconds before travelling to the next stop"
								+ "\n########## ##### ##### ##### ##### ##### ##########");
						Thread.sleep(timeToSleep * 1000);
					} else {
						System.out.println("An error has occured while updating tram location on the server.");
					}
				} else {
					System.out.println("An error has occured while updating tram location on the server.");
				}
			}
		} catch (RemoteException e) {
			System.out.println("Couldn't contact rmiregistry.");
		} catch (NotBoundException e) {

		} catch (Exception e) {
			System.out.println("A critical error has occured.");
		}
	}

	public boolean connect(FrontEnd fe) throws RemoteException {
		RPCMessage connectRequest = new RPCMessage();
		connectRequest.setType(RPCMessage.REQUEST);
		connectRequest.setTransactionId(genTransactionId());
		connectRequest.setRequestId(++requestId);
		connectRequest.setRPCId(genRPCId(fe));
		connectRequest.setProcedure((short) 5);
		String connectRequestData[] = { genTramId(), genRouteId() };
		connectRequest.setData(connectRequestData);
		Message connectRequestMsg = new Message();
		connectRequestMsg.marshal(connectRequest);

		// Response from server when retrieveNextStop() is invoked
		Message connectResponseMsg = fe.connect(connectRequestMsg);
		RPCMessage connectResponse = connectResponseMsg.unMarshall();
		String[] connectResponseData = connectResponse.getData();

		// Validate response from server
		if (connectResponse.getType() == RPCMessage.REPLY
				&& connectResponse.getTransactionId() == connectRequest.getTransactionId()
				&& connectResponse.getRPCId() == connectRequest.getRPCId()
				&& connectResponse.getRequestId() == connectRequest.getRequestId()
				&& connectResponse.getStatus() == 0) {
			if (!connectResponseData[0].equals("-1")) {
				this.currentStop = connectResponseData[0];
				this.previousStop = connectResponseData[1];
				TramClientImpl.tramCount++;
				this.count = TramClientImpl.tramCount;

				System.out.println("[" + this.count + "] Tram #" + tramId + " in  route #" + routeId + " is at stop #"
						+ currentStop + "\n########## ##### ##### ##### ##### ##### ##########");

				return true;
			}
		}

		return false;
	}

	public boolean retrieveNextStop(FrontEnd fe) throws RemoteException {
		RPCMessage nextStopRequest = new RPCMessage();
		nextStopRequest.setType(RPCMessage.REQUEST);
		nextStopRequest.setTransactionId(genTransactionId());
		nextStopRequest.setRequestId(++requestId);
		nextStopRequest.setRPCId(genRPCId(fe));
		nextStopRequest.setProcedure((short) 1);
		String nextStopData[] = { routeId, currentStop, previousStop };
		nextStopRequest.setData(nextStopData);
		Message nextStopRequestMsg = new Message();
		nextStopRequestMsg.marshal(nextStopRequest);

		// Response from server when retrieveNextStop() is invoked
		Message nextStopResponseMsg = fe.retrieveNextStop(nextStopRequestMsg);
		RPCMessage nextStopResponse = nextStopResponseMsg.unMarshall();
		String[] nextStopResponseData = nextStopResponse.getData();

		// Validate retrieve next stop response
		if (nextStopResponse.getType() == RPCMessage.REPLY
				&& nextStopResponse.getTransactionId() == nextStopRequest.getTransactionId()
				&& nextStopResponse.getRPCId() == nextStopRequest.getRPCId()
				&& nextStopResponse.getRequestId() == nextStopRequest.getRequestId()
				&& nextStopResponse.getStatus() == 0) {
			if (nextStopResponseData[0].equals("-1")) {
				System.out.println(
						"Next stop number could not be retrieved. One of the parameters: route id, current stop number, previous stop number is invalid. Retrying...");
				return false;
			} else {
				System.out.println("[" + this.count + "] Tram #" + tramId + " in route #" + routeId
						+ " is now travelling to stop #" + nextStopResponseData[0] + "..."
						+ "\n########## ##### ##### ##### ##### ##### ##########");

				// Update current stop with the next stop
				this.previousStop = currentStop;
				this.currentStop = nextStopResponseData[0];

				// Print current time and next stop
				System.out.println("[" + this.count + "] The time is " + new java.util.Date() + "\nTram #" + tramId
						+ " in route #" + routeId + " is now at stop #" + this.currentStop
						+ "\n########## ##### ##### ##### ##### ##### ##########");

				return true;
			}
		}

		return false;
	}

	public boolean updateTramLocation(FrontEnd fe) throws RemoteException {
		RPCMessage updateLocationRequest = new RPCMessage();
		updateLocationRequest.setType(RPCMessage.REQUEST);
		updateLocationRequest.setTransactionId(genTransactionId());
		updateLocationRequest.setRPCId(genRPCId(fe));
		updateLocationRequest.setRequestId(++requestId);
		updateLocationRequest.setProcedure((short) 3);
		String updateLocationData[] = { routeId, tramId, currentStop };
		updateLocationRequest.setData(updateLocationData);
		updateLocationRequest.setStatus((short) 0);

		Message updateLocationMsg = new Message();
		updateLocationMsg.marshal(updateLocationRequest);

		Message updateLocationResponseMsg = fe.updateTramLocation(updateLocationMsg);
		RPCMessage updateLocationResponse = updateLocationResponseMsg.unMarshall();

		// Validate update location message
		if (updateLocationResponse.getType() == RPCMessage.REPLY
				&& updateLocationResponse.getTransactionId() == updateLocationRequest.getTransactionId()
				&& updateLocationResponse.getRPCId() == updateLocationRequest.getRPCId()
				&& updateLocationResponse.getRequestId() == updateLocationRequest.getRequestId()
				&& updateLocationResponse.getStatus() == 0) {

			return true;
		}

		return false;
	}

	public long genRPCId(FrontEnd fe) throws RemoteException {
		long value = random.nextLong();

		while (true) {
			RPCMessage checkRPCRequest = new RPCMessage();
			checkRPCRequest.setType(RPCMessage.REQUEST);
			checkRPCRequest.setTransactionId(genTransactionId());
			checkRPCRequest.setRPCId((long) -1);
			checkRPCRequest.setRequestId(++requestId);
			checkRPCRequest.setProcedure((short) 6);
			checkRPCRequest.setCsvData(Long.toString(value));
			checkRPCRequest.setStatus((short) 0);

			Message checkRPCMsg = new Message();
			checkRPCMsg.marshal(checkRPCRequest);

			Message checkRPCResponseMsg = fe.isRPCIdAvailable(checkRPCMsg);
			RPCMessage checkRPCResponse = checkRPCResponseMsg.unMarshall();
			String[] checkRPCResponseData = checkRPCResponse.getData();

			// Validate update location message
			if (checkRPCResponse.getType() == RPCMessage.REPLY
					&& checkRPCResponse.getTransactionId() == checkRPCRequest.getTransactionId()
					&& checkRPCResponse.getRPCId() == checkRPCRequest.getRPCId()
					&& checkRPCResponse.getRequestId() == checkRPCRequest.getRequestId()
					&& checkRPCResponse.getStatus() == 0 && checkRPCResponseData[0].equals("1")) {

				return value;
			}
		}
	}

	public String genTramId() throws RemoteException {
		Random random = new Random();

		int value = random.nextInt(Integer.MAX_VALUE) + 1;

		this.tramId = Integer.toString(value);

		return Integer.toString(value);
	}

	public String genRouteId() throws RemoteException {
		List<String> routes = new ArrayList<String>();
		routes.add("1");
		routes.add("96");
		routes.add("101");
		routes.add("109");
		routes.add("112");

		int value = random.nextInt(4 - 0 + 1) + 0;

		this.routeId = routes.get(value);

		return routes.get(value);
	}

	public long genTransactionId() throws RemoteException {
		long value = random.nextLong();

		while (true) {
			if (!transactionIds.contains(value)) {
				transactionIds.add(value);

				return value;
			}
		}
	}
}