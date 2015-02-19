package com.gerken.gumbo.graph;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class PathSegment {

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_LOOPBACK_TO = 1;
	public static final int TYPE_LOOPBACK_FROM = 2;
	
	private WayPoint	from;
	private WayPoint	to;
	private int			type;
	
	private boolean		directFrom = false;
	private boolean 	directTo = false;
	
	private double 		angleFrom = 0.0;
	private double 		angleTo = 0.0;
	
	private double			cutoverSubLevel;
	
	private ArrayList<WayPoint> innerPath;

	private double indirectDist = 20.0;

	public PathSegment(WayPoint from, WayPoint to, int type) {
		this.from = from;
		this.to = to;
		this.type = type;
	}

	public WayPoint getFrom() {
		return from;
	}

	public WayPoint getTo() {
		return to;
	}

	public int getType() {
		return type;
	}

	public boolean isDirectFrom() {
		return directFrom;
	}

	public void setDirectFrom(boolean directFrom) {
		this.directFrom = directFrom;
	}

	public boolean isDirectTo() {
		return directTo;
	}

	public void setDirectTo(boolean directTo) {
		this.directTo = directTo;
	}

	public double getAngleFrom() {
		return angleFrom;
	}

	public void setAngleFrom(double angleFrom) {
		this.angleFrom = angleFrom;
	}

	public double getAngleTo() {
		return angleTo;
	}

	public void setAngleTo(double angleTo) {
		this.angleTo = angleTo;
	}

	public double getCutoverSubLevel() {
		return cutoverSubLevel;
	}

	public void setCutoverSubLevel(double cutoverSubLevel) {
		this.cutoverSubLevel = cutoverSubLevel;
	}

	public JSONObject asJson() {
		JSONObject jobj = new JSONObject();
		try {
			JSONArray jarr = new JSONArray();
			for (WayPoint wp: innerPath) {
				jarr.put(wp.getCoordinatesJson());
			}
			jobj.put("innerPath", jarr);
		} catch (Throwable e) {
			try { jobj.put("error", e.toString()); } catch (Throwable t) { }
		}
		
		return jobj;
	}

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
			
		} else if (type == TYPE_LOOPBACK_FROM) {
			
		} 
	}

}
