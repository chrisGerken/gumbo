package com.gerken.gumbo.monitor.contract;

import java.util.Map;

import backtype.storm.Config;

import com.gerken.gumbo.monitor.client.kafka.MetricsHistoryKafkaClient;
import com.gerken.gumbo.monitor.server.MetricsHistory;
	
public class MetricsHistoryFactory {

	public static final String PROPERTY_SERVER_KIND 	= "gumbo.server.kind";
	public static final String PROPERTY_TOPOLOGY 		= "gumbo.server.key";
	public static final String PROPERTY_ENABLED 		= "gumbo.enabled";
	public static final String PROPERTY_START	 		= "gumbo.start";
	public static final String PROPERTY_BUCKET_SIZE		= "gumbo.bucketSize";
	public static final String PROPERTY_PORT	 		= "gumbo.local.port";
	public static final String PROPERTY_DEBUG	 		= "gumbo.debug";
	public static final String PROPERTY_KAFKA_BROKERS	= "gumbo.kafka.brokers";
	
	public static IMetricsHistory connect(Map config) {

		Long start = getLong(PROPERTY_START, config, 0L);
		Long bucketSize = getLong(PROPERTY_BUCKET_SIZE, config, 1000L);
		int port = getInteger(PROPERTY_PORT, config, 8085);

		String kind = getString(PROPERTY_SERVER_KIND, config, "local");

		if ("local".equalsIgnoreCase(kind)) {
			return new MetricsHistory(start,bucketSize,port);
		}

		String brokers = getString(PROPERTY_KAFKA_BROKERS, config, "localhost:9092");
		MetricsHistoryKafkaClient client = new MetricsHistoryKafkaClient(brokers);
		client.restart(start, bucketSize, port);
		return client;
	}
	
	public static String clientKey(Map config) {
		return getString(PROPERTY_TOPOLOGY, config, "topology");
	}
	
	public static String getString(String property, Map config, String def) {
		String value = (String) config.get(property);
		if (value == null) {
			System.out.println("Missing property: "+property);
			value = def;
		}
		return value;
	}
	
	public static Long getLong(String property, Map config, Long def) {
		Object value = config.get(property);
		if (value == null) {
			System.out.println("Missing property: "+property);
			return def;
		}
		if (value instanceof Long) {
			return (Long)value;
		}
		if (value instanceof String) {
			try { return Long.parseLong((String)value); } catch (Throwable t) {  };
		}
		return def;
	}
	
	public static Integer getInteger(String property, Map config, Integer def) {
		Object value = config.get(property);
		if (value == null) {
			System.out.println("Missing property: "+property);
			return def;
		}
		if (value instanceof Integer) {
			return (Integer)value;
		}
		if (value instanceof Long) {
			long l = (Long) value;
			return (int) l;
		}
		if (value instanceof String) {
			try { return Integer.parseInt((String)value); } catch (Throwable t) {  };
		}
		return def;
	}
	
	public static Boolean getBoolean(String property, Map config, Boolean def) {
		Object value = config.get(property);
		if (value == null) {
			System.out.println("Missing property: "+property);
			return def;
		}
		if (value instanceof Boolean) {
			return (Boolean)value;
		}
		if (value instanceof String) {
			try { return Boolean.parseBoolean((String)value); } catch (Throwable t) {  };
		}
		return def;
	}
	
	private static boolean isDebug(Map config) {
		String value = (String) config.get(PROPERTY_DEBUG);
		if (value == null) {
			return true;
		}
		try { return Boolean.parseBoolean(value); } catch (Throwable t) {  };
		return true;
	}
	
	public static Long getStart(Map config) {
		return getLong(PROPERTY_START, config, 0L);
	}

	public static Long getBucketSize(Map config) {
		return getLong(PROPERTY_BUCKET_SIZE, config, 1000L);
	}

	public static boolean getEnabled(Config config) {
		return getBoolean(PROPERTY_ENABLED, config, false);
	}
}
