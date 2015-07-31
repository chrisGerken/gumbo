package com.gerken.gumbo.graph;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a segment of the displayable path of an edge.
 * 
 * PathSegments generally represent the portion of an edge that travels 
 * between two adjacent node depths.  For example an edge whose endpoints
 * are at depths 2 and 5 will have 3 path segments: one each between depths 
 * 2 and 3, 3 and 4 and 4 and 5.
 * 
 * If the edge is a loopback edge (the "to" node has the same or lower depth 
 * than the "from" node) then the edge will have two curved path segments that will 
 * each start and end at the same depth.
 *  
 * @author chrisgerken
 *
 */
public class PathSegment {

	/**
	 * Indicates a typical segment from one depth to the next depth
	 */
	public static final int TYPE_NORMAL = 0;
	/**
	 * Indicates the last segment in a loopback path
	 */
	public static final int TYPE_LOOPBACK_TO = 1;
	/**
	 * Indicates the first segment in a loopback path
	 */
	public static final int TYPE_LOOPBACK_FROM = 2;
	
	private WayPoint	from;
	private WayPoint	to;
	private int			type;
	
	private boolean		directFrom = false;
	private boolean 	directTo = false;
	
	private double 		angleFrom = 0.0;
	private double 		angleTo = 0.0;
	
	private double		cutoverSubLevel;
	
	private ArrayList<WayPoint> innerPath;

	private double indirectDist = 20.0;

	/**
	 * Construct a new PathSegment between the two given waypoints
	 * and of the given type
	 */
	public PathSegment(WayPoint from, WayPoint to, int type) {
		this.from = from;
		this.to = to;
		this.type = type;
	}

	/**
	 * @return the segment's "from" waypoint
	 */
	public WayPoint getFrom() {
		return from;
	}

	/**
	 * @return the segment's "to" waypoint
	 */
	public WayPoint getTo() {
		return to;
	}

	/**
	 * @return the kind of segment
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return Whether this segment is the only segment from the "from" node.
	 */
	public boolean isDirectFrom() {
		return directFrom;
	}

	/**
	 * @param directFrom indicates whether this segment is the only segment from the "from" node.
	 */
	public void setDirectFrom(boolean directFrom) {
		this.directFrom = directFrom;
	}

	/**
	 * @return whether this segment is the only segment to the to node
	 */
	public boolean isDirectTo() {
		return directTo;
	}

	/**
	 * @param directTo whether this segment is the only segment to the to node
	 */
	public void setDirectTo(boolean directTo) {
		this.directTo = directTo;
	}

	/**
	 * @return the angle out of the from node for paths that are not directFrom
	 */
	public double getAngleFrom() {
		return angleFrom;
	}

	/**
	 * @param angleFrom the angle out of the from node for paths that are not directFrom
	 */
	public void setAngleFrom(double angleFrom) {
		this.angleFrom = angleFrom;
	}

	/**
	 * @return the angle into the to node for paths that are not directTo
	 */
	public double getAngleTo() {
		return angleTo;
	}

	/**
	 * @param angleTo the angle into the to node for paths that are not directTo
	 */
	public void setAngleTo(double angleTo) {
		this.angleTo = angleTo;
	}

	/**
	 * @return the intermediate depth of the portion of the path segment that runs 
	 * perpendicular to the depth dimension
	 */
	public double getCutoverSubLevel() {
		return cutoverSubLevel;
	}

	/**
	 * @param cutoverSubLevel the intermediate depth of the portion of the path segment that runs 
	 * perpendicular to the depth dimension
	 */
	public void setCutoverSubLevel(double cutoverSubLevel) {
		this.cutoverSubLevel = cutoverSubLevel;
	}

	/**
	 * @return the layout and related information for the receiver as a Json object
	 */
	public JSONObject asJson() {
		JSONObject jobj = new JSONObject();
		try {
			JSONArray jarr = new JSONArray();
			for (WayPoint wp: innerPath) {
				jarr.put(wp.getCoordinatesJson());
			}
			jobj.put("innerPath", jarr);
//			jobj.put("angleFrom", angleFrom);
//			jobj.put("angleTo", angleTo);
		} catch (Throwable e) {
			try { jobj.put("error", e.toString()); } catch (Throwable t) { }
		}
		
		return jobj;
	}

	/**
	 * Calculate the actual layout of the receiver given an array of the
	 * actual coordinates for each of the depths.
	 * @param x an array of coordinates, one for each logical depth
	 */
	public void layoutInnerPath(double[] x) {
		from = new WayPoint(x[from.getDepth()], from.getLeftToRight());
		to = new WayPoint(x[to.getDepth()], to.getLeftToRight());
		innerPath = new ArrayList<WayPoint>();
		if (type == TYPE_NORMAL) {
			innerPath.add(from);
			if (!directFrom) {
				double angle = from.angleTo(to) + angleFrom;
				WayPoint wp = from.movePolar(angle,indirectDist);
				innerPath.add(wp);
			}
			if (!directTo) {
				WayPoint wp = innerPath.get(innerPath.size()-1);
				double angle = wp.angleTo(to) + Math.PI - angleTo;
				wp = to.movePolar(angle,indirectDist);
				innerPath.add(wp);
			}
			innerPath.add(to);
		} else if (type == TYPE_LOOPBACK_TO) {
			
			// Layout the path for the final segment in the loopback edge.
			// This segment ends at the "to" node
			
			innerPath.add(from);
			
			// from "from" back for 1/2 cutover sublevel
			innerPath.add(new WayPoint(from.getDepth()-(cutoverSubLevel/2.0), from.getLeftToRight()));
			
			// to the midpoint between from and to, but back to cutover sublevel
			innerPath.add(new WayPoint(from.getDepth()-cutoverSubLevel, (from.getLeftToRight()+to.getLeftToRight()) / 2.0 ));
			
			// same height as "to" but back by cutover sublevel
			innerPath.add(new WayPoint(to.getDepth()-(cutoverSubLevel/2.0), to.getLeftToRight()));
			
			double y = from.getDepth(); 
			
			innerPath.add(to);
			
		} else if (type == TYPE_LOOPBACK_FROM) {
			
			// Layout the path for the first segment in the loopback edge.
			// This segment begins at the "from" node
			
			innerPath.add(from);
			
			// from "from" back for 1/2 cutover sublevel
			innerPath.add(new WayPoint(from.getDepth()+(cutoverSubLevel/2.0), from.getLeftToRight()));
			
			// to the midpoint between from and to, but back to cutover sublevel
			innerPath.add(new WayPoint(from.getDepth()+cutoverSubLevel, (from.getLeftToRight()+to.getLeftToRight()) / 2.0 ));
			
			// same height as "to" but back by cutover sublevel
			innerPath.add(new WayPoint(to.getDepth()+(cutoverSubLevel/2.0), to.getLeftToRight()));
			
			double y = from.getDepth(); 
			
			innerPath.add(to);
			
		} 
	}

}
