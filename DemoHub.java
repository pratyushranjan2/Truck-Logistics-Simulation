package demo19065;

import base.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DemoHub extends Hub {

	private ArrayList<Truck> truckQ = new ArrayList<>();

	private ArrayList<Hub> path = new ArrayList<>();
	private ArrayList<Hub> visited = new ArrayList<>(); // routing helper fields
	private HashMap<Hub,Hub> phi = new HashMap<>();

	private int minDist;
	private ArrayList<Hub> traversed = new ArrayList<>(); // helper fields to find closest hub to a station
	private Hub closestHub;

	public DemoHub(Location loc) {
		super(loc);
	}

	@Override
	public synchronized boolean add(Truck truck) {
		if (currentCapacity() < super.getCapacity()) {
			// add the truck to the queue and return true(success)
			this.truckQ.add(truck);
			return true;
		}
		return false;
	}

	@Override
	protected synchronized void remove(Truck truck) {
		this.truckQ.remove(truck);
	}

	@Override
	public Highway getNextHighway(Hub last, Hub dest) {
		for (Highway highway: super.getHighways()) {
			if ((highway.getStart()==last && highway.getEnd()==dest)) {
				return highway;
			}
		}
		return null;
	}

	@Override 
	protected void processQ(int detlaT) {
		ArrayList<Truck> toBeRemoved = new ArrayList<>();

		for (Truck truck: truckQ) {
			//System.out.println("Breakpoint #1");
			refreshRoutingFields();
			//System.out.println("Breakpoint #2");

			Hub destination = findNearestHubForLoc(truck.getDest());
			//System.out.println("Breakpoint #3\nDestination Hub lLoc: "+destination.getLoc());
			route(this,destination);
			//System.out.println("Breakpoint #4");

			if (getPath().size()>1) {	// i.e, this is not the last hub
				//System.out.println("Breakpoint #5");
				Highway nextHighway = getNextHighway(this,getPath().get(1));
				//System.out.println("Breakpoint #6");

				boolean success = nextHighway.add(truck);
				//System.out.println("Breakpoint #7");
				if (success) {
					truck.enter(nextHighway);
					//System.out.println("Breakpoint #8");
					toBeRemoved.add(truck);
					//System.out.println("Breakpoint #8.1");
				}
			}
			else if (getPath().size() == 1) {	// if this is the last hub
				truck.setLoc(truck.getDest());
				//System.out.println("Breakpoint #9");
				toBeRemoved.add(truck);
			}
		}
		//System.out.println("Breakpoint #10");
		for (Truck truck: toBeRemoved) {
			remove(truck);
		}
		//System.out.println("Breakpoint #11");
		//System.out.println("Ending processing of queue in Hub");
	}

	private int currentCapacity() {
		return this.truckQ.size();
	}


	// Routing functions
	public void route(Hub source, Hub dest) {
		phi.put(source,null);
		dfs(source);
		trace(dest);
	}

	private ArrayList<Hub> getPath() {
		return this.path;
	}

	private void trace(Hub dest) {
		path.add(dest);
		Hub hub = phi.get(dest);
		while(hub!=null) {
			path.add(hub);
			hub = phi.get(hub);
		}
		Collections.reverse(path);
	}

	private void dfs(Hub hub) {
		visited.add(hub);

		for (Highway h: hub.getHighways()) {
			if (!visited.contains(h.getEnd())) {
				phi.put(h.getEnd(),hub);
				dfs(h.getEnd());
			}
		}
	}

	public synchronized Hub findNearestHubForLoc(Location loc) {
		Hub hub = this;
		minDist = hub.getLoc().distSqrd(loc);
		closestHub = hub;
		traversed = new ArrayList<>();

		traverse(hub,loc);
		return closestHub;
	}

	private void traverse(Hub currentHub, Location loc) {

		if (!traversed.contains(currentHub)) {
			traversed.add(currentHub);
			if (currentHub.getLoc().distSqrd(loc) < minDist) {
				minDist = currentHub.getLoc().distSqrd(loc);
				closestHub = currentHub;
			}
			ArrayList<Highway> hwys = currentHub.getHighways();
			for (Highway h: hwys) {
				traverse(h.getEnd(),loc);
			}
		}
	}

	private void refreshRoutingFields() {
		path.clear();
		visited.clear();
		phi.clear();
		traversed.clear();
	}
}