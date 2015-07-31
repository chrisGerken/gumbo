package com.gerken.gumbo.graph;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a logical point in the layout of a node or edge 
 * and is used primarily to perform a rough layout of the nodes and edges.
 * 
 * The x-coordinate (depth) represents the depth of a node or 
 * intermediate point on a path in a tree representation of the graph such 
 * that root nodes have depth 1 and direct child points of root nodes have depth
 * 2, etc.
 * 
 * The y-coordinate (leftToRight) represents the actual y-coordinate of the
 * node and incorporates node display width and distance between paths.
 *  
 * @author chrisgerken
 *
 */
public class WayPoint {

	private int depth;
	private int leftToRight;
	
	/**
	 * Constructs a node at the given depth and order.
	 * @param depth the logical distance of the point from the root of the graph
	 * @param leftToRight the height of the node
	 */
	public WayPoint(int depth, int leftToRight) {
		this.depth = depth;
		this.leftToRight = leftToRight;
	}

	/**
	 * Constructs a node at the given depth and order.
	 * @param depth the logical distance of the point from the root of the graph
	 * @param leftToRight the height of the node
	 */
	public WayPoint(double depth, int leftToRight) {
		this.depth = (int) depth;
		this.leftToRight = leftToRight;
	}

	/**
	 * Constructs a node at the given depth and order.
	 * @param depth the logical distance of the point from the root of the graph
	 * @param leftToRight the height of the node
	 */
	public WayPoint(double depth, double leftToRight) {
		this.depth = (int) depth;
		this.leftToRight = (int) leftToRight;
	}

	/**
	 * Print a textual representation of the way point
	 */
	public String toString() {
		return "("+depth+","+leftToRight+")";
	}

	/**
	 * Construct a Json representation of the receiver
	 * @return the way point's depth and leftToRight values in Json format
	 * @throws JSONException
	 */
	public JSONObject getCoordinatesJson() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("depth",depth);
		jobj.put("order", leftToRight);
		return jobj;
	}

	/**
	 * 
	 * @return The logical depth of the way point
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * 
	 * @return the actual height of the way point
	 */
	public int getLeftToRight() {
		return leftToRight;
	}

	/**
	 * Calculate and return the way point's hashcode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + leftToRight;
		return result;
	}

	/**
	 * Answer whether this waypoint is in the same location as
	 * another waypoint
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WayPoint other = (WayPoint) obj;
		if (depth != other.depth)
			return false;
		if (leftToRight != other.leftToRight)
			return false;
		return true;
	}

	/**
	 * Calculate and return the angle from the receiver to a given waypoint
	 * @param to another waypoint
	 * @return the angle from the receiver to the specified waypoint
	 */
	public double angleTo(WayPoint to) {
		double x1 = this.depth;
		double x2 = to.depth;
		double y1 = this.leftToRight;
		double y2 = to.leftToRight;
		
		double slope = (y2-y1) / (x2-x1);
		double angle = 0.0;
		if (slope != 0.0) {
			angle = Math.atan(slope);
		}
		return angle;
	}

	/**
	 * @param angle an angle in radians
	 * @param dist a distance in pixels
	 * @return a new waypoint that is the specified distance and angle from the receiver
	 */
	public WayPoint movePolar(double angle, double dist) {
		double x = depth + (Math.cos(angle) * dist);
		double y = leftToRight + (Math.sin(angle) * dist);
		return new WayPoint(x,y);
	}
}
