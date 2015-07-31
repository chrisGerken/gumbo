package com.gerken.gumbo.monitor.server;

import java.util.HashMap;

import com.gerken.gumbo.monitor.contract.IMetricsHistory;

/**
 * In deployments where multiple instances of an application can be monitored by a single gumbo monitor instance, the
 * MetricsHistoryManager keeps track of the different MetricsHistory instances
 * 
 * @author chrisgerken
 *
 */
public class MetricsHistoryManager {
	
	private static HashMap<Long, IMetricsHistory> histories = new HashMap<Long, IMetricsHistory>();
	private static Long last = null;

	/**
	 * Answer the instance of MetricsHistory with the given key, creating it if necessry.
	 * 
	 * @param id the MetricsHistory key
	 * @return the keyed MetricsHistory
	 */
	public static IMetricsHistory getHistory(Long id) {
		IMetricsHistory history = histories.get(id);
		if (history == null) {
			history = new MetricsHistory();
			histories.put(id, history);
			last = id;
		}
		return history;
	}
	
	/**
	 * Get the most recently defined and configured MetricsHistory
	 * @return a MetricsHistory instance
	 */
	public static IMetricsHistory getLast() {
		return getHistory(last);
	}
	
	/**
	 * Remove the MetricsHistory instance with the specified key
	 * @param id the key of the MetricsHistory to be removed
	 */
	public static void removeHistory(Long id) {
		histories.remove(id);
	}
	
}
