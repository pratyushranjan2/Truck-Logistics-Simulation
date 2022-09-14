package demo19065;

import base.*;

import java.util.ArrayList;

public class DemoNetwork extends Network {
	private ArrayList<Hub> hubs = new ArrayList<>();
	private ArrayList<Truck> trucks = new ArrayList<>();
	private ArrayList<Highway> highways = new ArrayList<>();

	private int minDist;
	private ArrayList<Hub> traversed = new ArrayList<>(); // helper fields to find closest hub to a station
	private Hub closestHub;


	@Override
	public void add(Hub hub) {
		this.hubs.add(hub); //System.out.println("Added hub to demoNetwork");
	}

	@Override
	public void add(Highway hwy) {
		this.highways.add(hwy); //System.out.println("Added highway to demoNetwork");
	}

	@Override
	public void add(Truck truck) {
		this.trucks.add(truck); //System.out.println("Added truck to demoNetwork");
	}

	@Override
	public void start() {
		for (Hub hub: this.hubs) {
			if (!hub.isAlive()) {
				hub.start();
			}
		}
		for (Truck truck: this.trucks) {
			if (!truck.isAlive()) {
				truck.start();
			}
		}
	}

	@Override
	public void redisplay(Display disp) {
		for (Hub hub: this.hubs) {
			hub.draw(disp);
		}
		for (Truck truck: this.trucks) {
			truck.draw(disp);
		}
		for (Highway hwy: this.highways) {
			hwy.draw(disp);
		}
	}

	@Override
	public synchronized Hub findNearestHubForLoc(Location loc) {
		traversed.clear();
		Hub hub = this.hubs.get(0);
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
}