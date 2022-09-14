package demo19065;

import base.*;
import java.util.ArrayList;

public class DemoHighway extends Highway {

	private ArrayList<Truck> truckQ = new ArrayList<>();

	@Override
	public synchronized boolean hasCapacity() {
		return currentCapacity() < super.getCapacity();
	}

	@Override
	public synchronized boolean add(Truck truck) {
		if (hasCapacity()) {
			this.truckQ.add(truck);
			return true;
		}
		return false;
	}

	@Override
	public synchronized void remove(Truck truck) {
		this.truckQ.remove(truck);
	}

	private int currentCapacity() {
		return this.truckQ.size();
	}
}