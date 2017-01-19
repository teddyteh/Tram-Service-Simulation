package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {
	private int routeId;
	private List<Integer> stops;
	private Map<String, String> trams;

	public Route(int routeId) {
		// Set route id
		this.setRoute(routeId);

		// Add stops to routes
		this.stops = new ArrayList<Integer>();
		switch (routeId) {
		case 1:
			stops.add(1);
			stops.add(2);
			stops.add(3);
			stops.add(4);
			stops.add(5);

			break;
		case 96:
			stops.add(23);
			stops.add(24);
			stops.add(2);
			stops.add(34);
			stops.add(22);

			break;
		case 101:
			stops.add(123);
			stops.add(11);
			stops.add(22);
			stops.add(34);
			stops.add(5);
			stops.add(4);
			stops.add(7);

			break;
		case 109:
			stops.add(88);
			stops.add(87);
			stops.add(85);
			stops.add(80);
			stops.add(9);
			stops.add(7);
			stops.add(2);
			stops.add(1);

			break;
		case 112:
			stops.add(110);
			stops.add(123);
			stops.add(11);
			stops.add(22);
			stops.add(34);
			stops.add(33);
			stops.add(29);
			stops.add(4);

			break;
		}

		trams = new HashMap<String, String>();
	}

	public int getRoute() {
		return routeId;
	}

	public void setRoute(int id) {
		this.routeId = id;
	}

	public List<Integer> getStops() {
		return stops;
	}

	public Map<String, String> getTrams() {
		return trams;
	}

	public void addTram(String tramId, String currentStop) {
		if (trams.size() < 5) {
			trams.put(tramId, currentStop);
		}
	}

	public int getNextStop(String currentStop, String previousStop) {
		// Get index of the current stop in the stops array
		int current = stops.indexOf(Integer.parseInt(currentStop));
		int previous = stops.indexOf(Integer.parseInt(previousStop));
		int nextStop = 0;

		// Right stop bigger than left
		if (current > previous) {
			// Current stop is the last stop in the route
			if (current == stops.size() - 1) {
				nextStop = stops.get(previous);
			} else {
				nextStop = stops.get(current + 1);
			}
		} else if (current < previous) {
			// Current stop is the first stop in the route
			if (current == 0) {
				nextStop = stops.get(previous);
			} else {
				nextStop = stops.get(current - 1);
			}
		}

		return nextStop;
	}
}
