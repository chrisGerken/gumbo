package com.gerkenip.timers;

import java.util.Map;

public class Generator {
	private long start;
	private long returned;
	private long every;
	private long count;

	public Generator(Map config, String component) {
		start = System.currentTimeMillis();
		returned = 0L;
		every = getLong(config,"timer."+component+".every",10L);
		count = getLong(config,"timer."+component+".count",1L);
	}

	public Generator(long every, long count) {
		start = System.currentTimeMillis();
		returned = 0L;
		this.every = every;
		this.count = count;
	}

	/* 
	 * Return the number of pending ticks since the last call
	 */
	public boolean pending() {
		long now = System.currentTimeMillis();
		long total = (((now-start) / every) + 1L) * count;
		boolean result = returned < total;
		if (result) {
			returned++;
		}
		return result;
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

	public static void main(String[] args) {
		Generator g = new Generator(10000L,200L);
		long end = System.currentTimeMillis() + 30000L;
		while (end > System.currentTimeMillis()) {
			try { Thread.sleep(5l); } catch (InterruptedException e) {}
			System.out.println(g.pending());
		}
	}
}
