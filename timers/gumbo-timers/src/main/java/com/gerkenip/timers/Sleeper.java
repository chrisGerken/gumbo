package com.gerkenip.timers;

import java.util.Map;

public class Sleeper {
	private long time;
	public Sleeper(Map config, String component) {
		time = getLong(config,"timer."+component+".time",10L);
	}

	/* 
	 * Return the number of pending ticks since the last call
	 */
	public void sleep() {
		try { Thread.sleep(time); } catch (Throwable t) {  }
	}
	
	public long getLong(Map config, String prop, long def) {
		Object value = config.get(prop);
		if (value==null) {
			return def;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof String) {
			try { return Long.parseLong((String)value); } catch (Throwable t) { }
		}
		return def;
	}

}
