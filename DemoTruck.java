package demo19065;

import base.*;

public class DemoTruck extends Truck {

	private Hub lastHub, sourceHub;
	private Highway currentHighway;
	private int timeElapsed = 0;
	private boolean onHighway = false;
	private DemoHub referenceHub; // A reference demo-hub to get nearest hub to the source station and destination station

	public String getTruckName() {
		return "Truck19065";
	}

	@Override
	public Hub getLastHub() {
		return this.lastHub;
	}

	@Override
	public void enter(Highway hwy) {
		onHighway = true; // method will be called from a hub in which it is present and about to leave onto a highway
		currentHighway = hwy;
	}

	@Override
	protected void update(int deltaT) {
		this.timeElapsed += deltaT; //System.out.println("Truck at rest. Elapsed time "+this.timeElapsed);
		if (this.sourceHub == null) {
			sourceHub = referenceHub.findNearestHubForLoc(super.getSource()); //System.out.println("Truck found the first hub at location "+sourceHub.getLoc());
		}

		//System.out.println("Time elapsed = "+this.timeElapsed+"   Start time = "+super.getStartTime());
		if (timeElapsed >= super.getStartTime()) {
			//System.out.println("Time elapsed is greater than starting time");
			// if at source
			//System.out.println("Comparing "+getLoc()+" and "+getSource()+" $$$$$$$$$$$$$$$@@@@@@@@@@@@@@");
			if ((super.getLoc().getX()==super.getSource().getX()) && (super.getLoc().getY()==super.getSource().getY())) {
				// move to the nearest hub if it has capacity
				// get an object of DemoHub, recursively visit all hubs from getHighways() and find the closest hub to the source station
				//System.out.println("Equality exists between "+getLoc()+" and "+getSource()+" ********************");
				enterHub(sourceHub);

			}
			// if at a hub
//			else if (this.currentHub != null) {
//				// if the truck is at a current hub then that hub will take care of sending it to the next highway
//				// this else if block is not needed then
//			}
			// if on a hignway
			else if (onHighway) {
				// update location, if location is approximately equal to the nextHub: update current hub and last hub,
				int x1 = currentHighway.getStart().getLoc().getX();
				int x2 = currentHighway.getEnd().getLoc().getX();
				int y1 = currentHighway.getStart().getLoc().getY();
				int y2 = currentHighway.getEnd().getLoc().getY();

				int distance = currentHighway.getMaxSpeed()*deltaT/1000; // distance covered in this time-step
				double distanceToNextHub = Math.sqrt(super.getLoc().distSqrd(currentHighway.getEnd().getLoc()));
				//System.out.println("Truck currentLoc: "+super.getLoc()+" NextHubLoc: "+currentHighway.getEnd().getLoc()+"\nTSD="+distance);

				if (distanceToNextHub <= distance) {
					enterHub(currentHighway.getEnd());
				}

				else {
					try {
						Location nextPoint = getNextPointOnHighway(x1,x2,y1,y2,distance,getLoc());
						super.setLoc(nextPoint); //System.out.println("Truck moved on highway to location "+getLoc());
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} 
		}
	}

	private Location getNextPointOnHighway(int x1, int x2, int y1, int y2, int dist, Location currentLoc) {
		double m = (y2-y1)/((x2-x1)*1.0);
		int x,y;
		int x0 = currentLoc.getX();
		int y0 = currentLoc.getY();

		if (x2 == x1) {  // in case of slope approaching infinity or vertical line
			if (y2 > y1) {
				y = (int)Math.ceil(y0+dist);
			}
			else {
				y = (int)Math.ceil(y0-dist);
			}
			x = x0;
		}
		else {
			if (x2 > x1) {
				x = (int)Math.ceil((x0 + dist*Math.sqrt(1.0/(1+m*m))));
				y = (int)Math.ceil((y0 + m*dist*Math.sqrt(1.0/(1+m*m))));
			}
			else {
				x = (int)Math.ceil((x0 - dist*Math.sqrt(1.0/(1+m*m))));
				y = (int)Math.ceil((y0 - m*dist*Math.sqrt(1.0/(1+m*m))));
			}

			if ((y-y1-m*x+m*x1 != 0) && m!=0) {  // handling deviation from the main line due to rounding off location co-ordinates into integer
				x = (int) Math.ceil(((y-y1+m*(x+x1))/(2*m)));
				y = (int) Math.ceil(((y+y1+m*(x-x1))/2.0));
			}
		}


		return new Location(x,y);
	}

	private void enterHub(Hub hub) {
		//System.out.println("Truck ready to move to the hub");
		boolean success = false;
		try {
			success = hub.add(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (success) {
			super.setLoc(hub.getLoc()); //System.out.println("Truck moved to hub with location "+hub.getLoc());
			this.lastHub = hub;
			onHighway = false;
			if (currentHighway != null) {
				currentHighway.remove(this);
				currentHighway = null;
			}
		}
	}

	public void setReferenceHub(DemoHub hub) {
		try {
			this.referenceHub = hub;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}