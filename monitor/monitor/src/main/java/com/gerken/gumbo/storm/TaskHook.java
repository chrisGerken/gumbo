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

import com.gerken.gumbo.monitor.MonitorClient;

public class TaskHook implements ITaskHook {

	private MonitorClient mclient;
	
	private boolean enabled = false;
	
	public TaskHook() {

	}

	@Override
	public void prepare(Map conf, TopologyContext context) {
		String id = context.getThisComponentId();
		int task = context.getThisTaskId();

		if (id.startsWith("__")) { return; }
		
		String host = (String) conf.get("storm.monitor.host");
		int port = Integer.parseInt(String.valueOf(conf.get("storm.monitor.port")));
		Long start = (Long) conf.get("storm.monitor.start");
		Long bsize = (Long) conf.get("storm.monitor.bucketSize");
		
		mclient = MonitorClient.connect(host,port,start,bsize);
		
		Set<String> outputs = context.getComponentStreams(id);
		for (String stream : outputs) {
			mclient.topologyConnect(id, stream);
		}
		Map<GlobalStreamId, Grouping> inputs = context.getSources(id);
		for (GlobalStreamId gsi : inputs.keySet()) {
			String stream = gsi.get_streamId();
			String fromNode = gsi.get_componentId();
			mclient.topologyConnect(fromNode, stream, id);
			mclient.declare("Backlog",stream,task,id);
		}

//		if (context.getThisSources().isEmpty()) {
//			reliabilityMetricGroup = id+"_Reliability";
//			mclient.declare(reliabilityMetricGroup, "Emit", task);
//			mclient.declare(reliabilityMetricGroup, "Ack", task);
//			mclient.declare(reliabilityMetricGroup, "Fail", task);
//		}

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
        if ("true".equals(newConfig.get("storm.monitor.enabled").toString())) {
            List<String> hooksList= new ArrayList<String>();
            hooksList.add(TaskHook.class.getName());
            newConfig.put(Config.TOPOLOGY_AUTO_TASK_HOOKS, hooksList);

            newConfig.put("storm.monitor.start",System.currentTimeMillis());
        }
	}

}
