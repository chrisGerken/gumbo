package com.gerken.gumbo.stress;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;

/**
 * Class representing one supplier of gumbo monitor metrics data.  
 * 
 * @author chrisgerken
 *
 */
public class StressApp {
	
	private String propsFile;
	private String mclientFile;
	private String nodeId;
	
	private Map	config;
	private ArrayList<StressTask> tasks = new ArrayList<StressTask>();
	
	public StressApp(String propsFile, String mclientFile, String nodeId) throws Exception {
		this.propsFile = propsFile;
		this.mclientFile = mclientFile;
		this.nodeId = nodeId;
		
		config = loadConfig();
		loadTasks();
	}

	public static void main(String[] args) {
	
		if (args.length < 3) {
			System.out.println("\n\njava com.gerken.gumbo.stress.StressApp  <properties-file>  <mclient-file>  <app-id> ");
			return;
		}
		
		String propsFile = args[0];
		String mclientFile = args[1];
		String nodeId = args[2];
		
		try {
			new StressApp(propsFile, mclientFile, nodeId).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void run() {
		long start = getTask("1").getStart(); 
		long wait = start - System.currentTimeMillis();
		if (wait > 0) {
			sleep(wait);
		}
		long stop = start + (20L * 60000L); 
		while (System.currentTimeMillis() < stop) {
			for (StressTask task: tasks) {
				task.generate(System.currentTimeMillis());
			}
		}
	}
	
	private void sleep(long wait) {
		try { Thread.sleep(wait); } catch (InterruptedException e) { }
	}

	private StressTask getTask(String id) {
		int taskId = Integer.parseInt(id);
		while (tasks.size() < (taskId)) {
			tasks.add(new StressTask(tasks.size()+1));
		}
		return tasks.get(taskId-1);
	}

	private void loadTasks() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(mclientFile));
		String line = br.readLine();
		while (line != null) {
			line = line.trim();
			if ((line.length() > 0) && (line.charAt(0) != '#')) {
				StringTokenizer st = new StringTokenizer(line," \t");
				String token[] = new String[st.countTokens()];
				for (int i = 0; i < token.length; i++) {
					token[i] = st.nextToken();
				}
				
				if (token[0].equalsIgnoreCase("node")) {
					// node <task> <node>
					if (nodeId.equalsIgnoreCase(token[2])) {
						StressTask task = getTask(token[1]);
						task.activate(config);
					}
				} else if (token[0].equalsIgnoreCase("connect")) {
					// connect <task> fromComponent stream toComponent
					StressTask task = getTask(token[1]);
					task.connect(token[2], token[3], token[4]);
				} else if (token[0].equalsIgnoreCase("declare")) {
					// declare <task> metricGroup  metric toComponent  
					StressTask task = getTask(token[1]);
					task.declare(token[2], token[3], task.getTaskId(), token[4]);
				} else if (token[0].equalsIgnoreCase("color")) {
					// color <task> metric r,g,b
					StressTask task = getTask(token[1]);
					task.setColor(token[2], token[3]);
				} else if (token[0].equalsIgnoreCase("increment")) {
					// increment <task> every amount  metricGroup  metric
					StressTask task = getTask(token[1]);
					task.increment(token[2], token[3], token[4], token[5]);
				} else if (token[0].equalsIgnoreCase("increments")) {
					// increments <task> every amount  metricGroup  metric task[]
					StressTask task = getTask(token[1]);
					String to[] = new String[token.length-6];
					for (int i = 0; i < to.length; i++) {
						to[i] = token[i+6];
					}
					task.increment(token[2], token[3], token[4], token[5], to);
				} 
				
			}
			line = br.readLine();
		}
		br.close();
	}
	
	private Map loadConfig() {
        Properties props = new Properties();
        Map map = new HashMap();
        try {
        	FileInputStream fis = new FileInputStream(propsFile);
        	props.load(fis);
           for (Object prop : props.keySet()) {
				Object value = props.get(prop);
				try {
					int intValue = Integer.parseInt(String.valueOf(value).trim());
					map.put(prop.toString(), intValue);
				} catch (NumberFormatException e) {
					map.put(prop.toString(), value);					
				}
			}
           fis.close();
           map.put(MetricsHistoryFactory.PROPERTY_START, System.currentTimeMillis());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return map;
	}
	
	
	
}
