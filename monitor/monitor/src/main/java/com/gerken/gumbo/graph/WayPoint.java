package com.gerken.gumbo.graph;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WayPoint {

	private int depth;
	private int leftToRight;
	
	public WayPoint(int depth, int leftToRight) {
		this.depth = depth;
		this.leftToRight = leftToRight;
	}

	public WayPoint(double depth, int leftToRight) {
		this.depth = (int) depth;
		this.leftToRight = leftToRight;
	}

	public WayPoint(double x, double y) {
		this.depth = (int) x;
		this.leftToRight = (int) y;
	}

	public String toString() {
		return "("+depth+","+leftToRight+")";
	}

	public JSONObject getCoordinatesJson() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("depth",depth);
		jobj.put("order", leftToRight);
		return jobj;
	}

	public int getDepth() {
		return depth;
	}

	public int getLeftToRight() {
		return leftToRight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + leftToRight;
		return result;
	}

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

	public WayPoint movePolar(double angle, double dist) {
		double x = depth + (Math.cos(angle) * dist);
		double y = leftToRight + (Math.sin(angle) * dist);
		return new WayPoint(x,y);
	}
}
