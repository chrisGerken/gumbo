package com.gerken.gumbo.storm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backtype.storm.Config;
import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.Grouping;
import backtype.storm.hooks.ITaskHook;
import backtype.storm.hooks.info.BoltAckInfo;
import backtype.storm.hooks.info.BoltExecuteInfo;
import backtype.storm.hooks.info.BoltFailInfo;
import backtype.storm.hooks.info.EmitInfo;
import backtype.storm.hooks.info.SpoutAckInfo;
import backtype.storm.hooks.info.SpoutFailInfo;
import backtype.storm.task.TopologyContext;

import com.gerken.gumbo.monitor.client.MonitorClient;
import com.gerken.gumbo.monitor.contract.MetricsHistoryFactory;

public class GumboTaskHook implements ITaskHook {

	private MonitorClient mclient;
	
	private boolean enabled = false;
	
	public GumboTaskHook() {

	}

	@Override
	public void prepare(Map conf, TopologyContext context) {
		String thisComponentId = context.getThisComponentId();
		int task = context.getThisTaskId();

		if (thisComponentId.startsWith("__")) { return; }
		
		try {
			mclient = MonitorClient.connect(conf);
		} catch (Exception e) {

		}
		
		Set<String> outputs = context.getComponentStreams(thisComponentId);
		for (String stream : outputs) {
			mclient.topologyConnect(thisComponentId, stream);
		}
		Map<GlobalStreamId, Grouping> inputs = context.getSources(thisComponentId);
		for (GlobalStreamId gsi : inputs.keySet()) {
			String stream = gsi.get_streamId();
			String fromComponentId = gsi.get_componentId();
			mclient.topologyConnect(fromComponentId, stream, thisComponentId);
			mclient.declare("Backlog",stream,task,thisComponentId);
			String colorKey = "gumbo."+stream+".color";
			if (conf.containsKey(colorKey)) {
				mclient.setColor(stream, (String)conf.get(colorKey));
			}
		}

		enabled = true;
	}

	@Override
	public void cleanup() {
		if (!enabled) { return; }
		mclient.cleanup();
	}

	@Override
	public void emit(EmitInfo info) {
		if (!enabled) { return; }
		String stream = info.stream;
		
		mclient.increment("Backlog", stream, 1L, info.outTasks);
//		System.out.println("Emit to "+stream);
	}

	@Override
	public void spoutAck(SpoutAckInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spoutFail(SpoutFailInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void boltExecute(BoltExecuteInfo info) {
//		System.out.println("Exec from "+info.tuple.getSourceStreamId()+" ("+enabled+")");
		if (!enabled) { return; }
		String stream = info.tuple.getSourceStreamId();
		mclient.increment("Backlog", stream, -1L, info.executingTaskId);
	}

	@Override
	public void boltAck(BoltAckInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void boltFail(BoltFailInfo info) {
		// TODO Auto-generated method stub

	}

	public static void registerTo(Config newConfig) {
		if (MetricsHistoryFactory.getEnabled(newConfig)) {
            List<String> hooksList= new ArrayList<String>();
            hooksList.add(GumboTaskHook.class.getName());
            newConfig.put(Config.TOPOLOGY_AUTO_TASK_HOOKS, hooksList);

            newConfig.put(MetricsHistoryFactory.PROPERTY_START,System.currentTimeMillis());
        }
	}

}
