package com.gerken.gumbo.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a directed graph that is a Storm topology of nodes (spouts and bolts)
 * and edges (streams between components).
 * 
 * @author chrisgerken
 *
 */
public class Graph {

	private HashMap<String, HashSet<String>> components = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> streams = new HashMap<String, HashSet<String>>();
	
	private HashMap<String, GraphNode> nodes = new HashMap<String, GraphNode>();
	private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
	
	private JSONObject json = null;
	
	// Layout parameters
	
	private double indirectSpan =  0.2;
	private double perLevel     = 100.0;
    private double vmargin 		= 10;
    private double cutoverWidth = 10.0;

	/**
	 * Constructs a graph given the nodes (components and the streams emitted by each) and 
	 * the edges (the streams and the destination components for each).
	 * 
	 * @param components 
	 * @param streams
	 */
	public Graph(HashMap<String, HashSet<String>> components, HashMap<String, HashSet<String>> streams) {
		this.components = components;
		this.streams = streams;
		layout();
	}

	private void layout() {
		
		// build the nodes and edges
		for (String fromComponent : components.keySet()) {
			GraphNode from = nodeFor(fromComponent);
			HashSet<String> outStreams = components.get(fromComponent);
			if (outStreams==null) {
				outStreams = new HashSet<String>();
			}
			// for each stream coming out of fromComponent...
			for (String stream : outStreams) {
				// for each component reading from stream...
				HashSet<String> dests = streams.get(stream);
				if (dests==null) {
					dests = new HashSet<String>();
				}
				for (String toComponent : dests) {
					GraphNode to = nodeFor(toComponent);
					GraphEdge edge = new GraphEdge(from, to, stream);
					edges.add(edge);
					from.addChild(to);
					to.addParent(from);
					from.addOutboundEdge(edge);
					to.addInboundEdge(edge);
				}
			}
		}
		
		// set the root node depths
		for (GraphNode node : nodes.values()) {
			if (!node.hasParents()) {
				node.setDepth(1);
			} else {
				node.setDepth(0);
			}
		}
		
		// set the remaining node depths
		boolean changed = true;
		while (changed) {
			changed = false;
			for (GraphEdge edge : edges) {
				GraphNode from = edge.getFrom();
				GraphNode to = edge.getTo();

				if (from.hasDepth()) {
					if (to.isParentOf(from)) {
						changed = changed | !edge.hasBeenUsed();
						edge.setLoopBack(true);
						edge.setUsed(true);
					} else {
						if (!edge.hasBeenUsed()) {
							changed = true;
							to.addParents(from.getParents());
							to.addParent(from);
						}
						if (from.getDepth() >= to.getDepth()) {
							to.setDepth(from.getDepth()+1);
							changed = true;
						}
						edge.setUsed(true);
					}
				} else {
					// can't do anything with this edge until the from node has a depth
				}
			}
		}
		
		// calculate the paths for the edges
		
		int margin = 25;
		int nodeWidth = 6;

		int max = maxDepth();
		int leftToRight = margin;
		int width[] = new int[max+1];
		
		// build a list, left to right, of the edges coming out of the root nodes
		ArrayList<GraphEdge> currentPaths = new ArrayList<GraphEdge>();
		for (GraphNode node : nodes.values()) {
			if (node.getDepth()==1) {
				currentPaths.addAll(node.getOutboundEdges());
				node.setOrder(leftToRight);
				leftToRight = leftToRight + nodeWidth + margin;
			}
		}
		width[1] = leftToRight-1;
		
		for (int level = 2; level <= max; level++) {
			int inboundSubLevel = 0;
			int loopBackSubLevel = 0;
			ArrayList<GraphEdge> nextPaths = new ArrayList<GraphEdge>();
			leftToRight = margin;
			for (GraphEdge edge : currentPaths) {
				GraphNode currentNode = edge.getTo();
				if (edge.isLoopBack()) {
					// grab an inbound sub-level altitude
				} else {
					if (currentNode.getDepth()==level) {
						// if a node already has an order then it's already contributed to the next paths
						if (!currentNode.hasOrder()) {
							currentNode.setOrder(leftToRight);
							leftToRight = leftToRight + nodeWidth + margin;
							for (GraphEdge outbound : currentNode.getOutboundEdges()) {
								if (outbound.isLoopBack()) {
									// Do nothing.  The loopback edge is already in currentPaths because
									// the edge's to node was processed for a previous level 
								} else {
									nextPaths.add(outbound);
									// grab an outbound sub-level altitude
								}
							}
							for (GraphEdge inbound : currentNode.getInboundEdges()) {
								if (inbound.isLoopBack()) {
									nextPaths.add(inbound);
									loopBackSubLevel++;
									inbound.addWayPoint(level, leftToRight);
									leftToRight = leftToRight + margin + nodeWidth;;
									// grab an outbound sub-level altitude
								}
							}
						}
					} else {
						// just passing through
						nextPaths.add(edge);
						inboundSubLevel--;
						edge.addWayPoint(level,leftToRight);
						leftToRight = leftToRight + margin + nodeWidth;
						// grab an inbound sub-level altitude
						// grab an outbound sub-level altitude
					}
				}
			}
			width[level] = leftToRight-1;
			currentPaths = nextPaths;
		}
		
		// Gather all segments for all edges
		ArrayList<PathSegment> segments = new ArrayList<PathSegment>();
		for (GraphEdge edge : edges) {
			segments.addAll(edge.getSegments());
		}

		HashMap<WayPoint, ArrayList<PathSegment>> departures = new HashMap<WayPoint, ArrayList<PathSegment>>();
		HashMap<WayPoint, ArrayList<PathSegment>> arrivals;

		// Group segments by the WayPoint they leave
		for (PathSegment segment : segments) {
			WayPoint key = segment.getFrom();
			ArrayList<PathSegment> segmentsFromWaypoint = departures.get(key);
			if (segmentsFromWaypoint == null) {
				segmentsFromWaypoint = new ArrayList<PathSegment>();
				departures.put(key, segmentsFromWaypoint);
			}
			segmentsFromWaypoint.add(segment);
		}
		
		// For each group of segments leaving a waypoint, group the segments further by destination waypoint
		for (WayPoint key: departures.keySet()) {
			ArrayList<PathSegment> segmentsFromWaypoint = departures.get(key);
			arrivals = new HashMap<WayPoint, ArrayList<PathSegment>>();
			for (PathSegment segment: segmentsFromWaypoint) {
				WayPoint destination = segment.getTo();
				ArrayList<PathSegment> segmentsToWaypoint = arrivals.get(destination);
				if (segmentsToWaypoint == null) {
					segmentsToWaypoint = new ArrayList<PathSegment>();
					arrivals.put(destination, segmentsToWaypoint);
				}
				segmentsToWaypoint.add(segment);
			}
			
			// Each segment that goes between unique waypoints is a directFrom
			// Every other segment needs to have a fromAngle calculated
			
			for (WayPoint destination: arrivals.keySet()) {
				ArrayList<PathSegment> segmentsToWaypoint = arrivals.get(destination);
				if (segmentsToWaypoint.size() == 1) {
					segmentsToWaypoint.get(0).setDirectFrom(true);
				} else {
					double count = (double) segmentsToWaypoint.size();
					double range = (count - 1.0) * indirectSpan;
					double angleFrom = range / -2.0;
					for (PathSegment segment: segmentsToWaypoint) {
						segment.setDirectFrom(false);
						segment.setAngleFrom(angleFrom);
						angleFrom = angleFrom + indirectSpan;
					}
				}
			}
		}

		// Group segments by the WayPoint they arrive at
		arrivals = new HashMap<WayPoint, ArrayList<PathSegment>>();
		for (PathSegment segment : segments) {
			WayPoint key = segment.getTo();
			ArrayList<PathSegment> segmentsToWaypoint = arrivals.get(key);
			if (segmentsToWaypoint == null) {
				segmentsToWaypoint = new ArrayList<PathSegment>();
				arrivals.put(key, segmentsToWaypoint);
			}
			segmentsToWaypoint.add(segment);
		}
		
		// For each group of segments departing from a waypoint, group the segments further by destination waypoint
		for (WayPoint key: arrivals.keySet()) {
			ArrayList<PathSegment> segmentsToWaypoint = arrivals.get(key);
			departures = new HashMap<WayPoint, ArrayList<PathSegment>>();
			for (PathSegment segment: segmentsToWaypoint) {
				WayPoint departure = segment.getFrom();
				ArrayList<PathSegment> segmentsFromWaypoint = departures.get(departure);
				if (segmentsFromWaypoint == null) {
					segmentsFromWaypoint = new ArrayList<PathSegment>();
					departures.put(departure, segmentsFromWaypoint);
				}
				segmentsFromWaypoint.add(segment);
			}
			
			// Each segment that goes between unique waypoints is a directTo
			// Every other segment needs to have a toAngle calculated
			
			for (WayPoint departure: departures.keySet()) {
				ArrayList<PathSegment> segmentsFromWaypoint = departures.get(departure);
				if (segmentsFromWaypoint.size() == 1) {
					segmentsFromWaypoint.get(0).setDirectTo(true);
				} else {
					double count = (double) segmentsFromWaypoint.size();
					double range = (count - 1.0) * indirectSpan;
					double angleFrom = range / -2.0;
					for (PathSegment segment: segmentsFromWaypoint) {
						segment.setDirectTo(false);
						segment.setAngleTo(angleFrom);
						angleFrom = angleFrom + indirectSpan;
					}
				}
			}
		}
		
		
		// Finally, iterate down the levels, setting the x-coordinate for each level then calculating
		// the inner paths of any segments that draw to that level
		
		double x[] = new double[max+1];
		
		for (int level = 1; level <= max; level++) {
			
			// Count the loopback segments
			int from_count = 0;
			int to_count = 0;
			for (PathSegment segment : segments) {
				if ((segment.getTo().getDepth() == level) && (segment.getType() == PathSegment.TYPE_LOOPBACK_TO)) {
					to_count++;
					segment.setCutoverSubLevel(to_count * cutoverWidth);
				}
				if ((segment.getTo().getDepth() == level) && (segment.getType() == PathSegment.TYPE_LOOPBACK_FROM)) {
					from_count++;
					segment.setCutoverSubLevel(from_count * cutoverWidth);
				}
			}
			
			// Set the level's x-coordinate
			if (level == 1) {
				x[level] = margin + cutoverWidth * (to_count + from_count);
			} else {
				double d = cutoverWidth * (to_count + from_count);
				if (d < perLevel) {
					d = perLevel;
				}
				x[level] = x[level-1] + d;
			}

			// Calculate the inner paths for any segment ending on the current level
			for (PathSegment segment : segments) {
				if (segment.getTo().getDepth() == level) {
					segment.layoutInnerPath(x);
				}
			}
			
		}
		
		// Set the x-coordinate for nodes
		for (GraphNode node: nodes.values()) {
			int depth = node.getDepth();
			depth = (int) x[depth];
			node.setDepth(depth);
		}
		
	}

	private int maxDepth() {
		int max = -1;
		for (GraphNode node : nodes.values()) {
			if (node.getDepth() > max) {
				max = node.getDepth();
			}
		}
		return max;
	}

	private GraphNode nodeFor(String component) {
		GraphNode node = nodes.get(component);
		if (node == null) {
			node = new GraphNode(component);
			nodes.put(component,node);
		}
		return node;
	}
	 
	/**
	 * Convenience method for the display of graph connections
	 */
	public void print() {
		for (GraphNode node : nodes.values()) {
			System.out.println(node.toString());
		}
		for (GraphEdge edge : edges) {
			System.out.println(edge.toString());
		}
	}

	/**
	 * Answer a Json representation of the graph
	 * 
	 * @return a Json object containing connection, layout and labeling 
	 * information about the graph
	 * 
	 */
	public JSONObject asJson() {
		
		if (json==null) {

			json = new JSONObject();
			
			try {
				JSONArray jarr = new JSONArray();
				for (GraphEdge edge : edges) {
					jarr.put(edge.getJson());
				}
				json.put("edges", jarr);
				
				jarr = new JSONArray();
				for (GraphNode node : nodes.values()) {
					jarr.put(node.getJson());
				}
				json.put("nodes", jarr);
				
				json.put("created", System.currentTimeMillis());
				
//				System.out.println(json.toString(4));
				
			} catch (JSONException e) {
			}
		}
		
		return json;
	}

}
