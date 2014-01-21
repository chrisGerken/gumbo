
function hist(data,id,metricGroup,metrics) {
	
	var list = metrics.split(" ");
	
	var histData = { labels: data.labels , datasets: [] };
	var mg;
	for (var i in data.metricGroups) {
		if (data.metricGroups[i].metricGroup == metricGroup) {
		    mg = data.metricGroups[i];
		}
	}
    var max = 20;
	for (var i in mg.metrics) {
	if ((metrics=="*") || ($.inArray(mg.metrics[i].metric,list) > -1)) {
	  		var ds = { 
				fillColor: "rgba("+mg.metrics[i].color+",0.0)" ,
        	    strokeColor: "rgba("+mg.metrics[i].color+",1.0)" , 
				pointColor: "rgba("+mg.metrics[i].color+",0.1)" ,
				pointStrokeColor: "rgba("+mg.metrics[i].color+",0.1)" ,
				data: mg.metrics[i].values };
			histData.datasets.push(ds);
		    if (max < mg.metrics[i].max) {
	    	    max = mg.metrics[i].max;
		    }
		}
	}

    var ctxLine = document.getElementById(id).getContext("2d");
    var optsLine = { 
    	pointDotRadius : 1 , 
    	pointDotStrokeWidth : 0 , 
    	animation : false ,
    	scaleOverride : true ,
    	scaleSteps : 10 ,
    	scaleStepWidth : max / 10 + 1 ,
    	scaleStartValue : 0
    	}; 		
    new Chart(ctxLine).Line(histData, optsLine);		
};

function taskSoV(data,id,metricGroup,metric) {
	
	var sovData = { labels: [] , datasets: [] };
	var mg;
	for (var i in data.metricGroups) {
		if (data.metricGroups[i].metricGroup == metricGroup) {
		    mg = data.metricGroups[i];
		}
	}
	var m;
	for (var i in mg.metrics) {
		if (mg.metrics[i].metric == metric) {
		    m = mg.metrics[i];
		}
	}
	
	var ds = { 
			fillColor: "rgba("+m.color+",0.0)" ,
            strokeColor: "rgba("+m.color+",1.0)" , 
            data: []
	};
	sovData.datasets.push(ds);
	
	var max = 5;
	for (var i in m.tasks) {
		sovData.labels.push(""+m.tasks[i].task);
		var value = m.tasks[i].values[data.latestOffsetWithData];
		ds.data.push(value);
		if (max < value) {
		    max = value;
		}
	}

    var ctxSoV = document.getElementById(id).getContext("2d");
    var optsSoV = { 
    	animation : false ,
    	scaleOverride : true,
    	scaleSteps: 5,
    	scaleStepWidth: max/5,
    	scaleStartValue: 0 
    	}; 		
    new Chart(ctxSoV).Bar(sovData, optsSoV);		
};

function metricSoV(data,id,metricGroup) {
	
	var sovData = [ ];
	var mg;
	for (var i in data.metricGroups) {
		if (data.metricGroups[i].metricGroup == metricGroup) {
		    mg = data.metricGroups[i];
		}
	}

	for (var i in mg.metrics) {
		var wedge = {
			value: mg.metrics[i].values[data.latestOffsetWithData],
			color: "rgba("+mg.metrics[i].color+",1.0)" 
		};
		sovData.push(wedge);
	}

	if (sovData.length < 1) {
		var wedge = {
			value: 20,
			color: "rgba(240,240,240,1.0)" 
		};
		sovData.push(wedge);
	}
	
    var ctxMSoV = document.getElementById(id).getContext("2d");
    var optsMSoV = { 
    	animation : false ,
    	animateRotate : false ,
    	animateScale : false
    	}; 		
    new Chart(ctxMSoV).Doughnut(sovData, optsMSoV);		
};

function legend(data,id,metricGroup) {
    var canvasLegend = document.getElementById(id);
    var ctxLegend = canvasLegend.getContext("2d");
	ctxLegend.clearRect(0, 0, canvasLegend.width, canvasLegend.height);
	ctxLegend.font = '14pt Calibri';

	var mg;
	for (var i in data.metricGroups) {
		if (data.metricGroups[i].metricGroup == metricGroup) {
		    mg = data.metricGroups[i];
		}
	}

    var width = 0;
	for (var i in mg.metrics) {
		mg.metrics[i].legend = mg.metrics[i].metric + " ("+ mg.metrics[i].values[data.latestOffsetWithData] + ")";
		var len = ctxLegend.measureText(mg.metrics[i].legend).width;
		if (len > width) {
			    width = len;
		} 
	}
	width = width + 25;

 	var x = 10;
 	var y = 25;
	for (var i in mg.metrics) {
		ctxLegend.fillStyle = "rgba("+mg.metrics[i].color+",1.0)";
		ctxLegend.fillRect(x,y-18,20,20);
		ctxLegend.fillStyle = "rgba(0,0,0,1.0)";
		ctxLegend.fillText(mg.metrics[i].legend,x+25,y);
		y = y + 25;
		if (y > (canvasLegend.height - 30)) {
		    y = 25;
		    x = x + width + 15;
		}
	}
			
};

function monitorStorm() {
	document.body.style.background="#e3e3e3";
	placeWidgets();
	beginMonitoring();
}

function beginMonitoring() {

		var tid = setInterval(getData, 1000);
		
};

function placeWidgets() {

	var root = document.getElementById("storm-monitor");
	
	var header = document.createElement("H2");
	var title = document.createTextNode(monitorConfig.title);
    root.appendChild(header);
    header.appendChild(title);

	for (var i in monitorConfig.widgets) {
		var widget = monitorConfig.widgets[i];
		var newdiv = document.createElement("div");
		newdiv.style.position = "absolute";
		newdiv.style.left = widget.x+"px";
		newdiv.style.top  = widget.y+"px";
		root.appendChild(newdiv);
		
		header = document.createElement("H3");
		title = document.createTextNode(widget.title);
		newdiv.appendChild(header);
		header.appendChild(title);
		
		var canvas = document.createElement("canvas");
		canvas.id = widget.id;
		canvas.height = widget.height;
		canvas.width = widget.width;
		newdiv.appendChild(canvas);
	}

};

function prepCanvas(canvasId) {
    var canvas = document.getElementById(canvasId);
    var ctx = canvas.getContext("2d");
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0,0,canvas.width, canvas.height);
};

function success(data) {
	for (var i in monitorConfig.widgets) {
		var widget = monitorConfig.widgets[i];
		if (widget.kind == "hist") {
			if (widget.hasOwnProperty("metrics")) {
				hist(data,widget.id,widget.metricGroup,widget.metrics);
			} else { 
				hist(data,widget.id,widget.metricGroup,"*");
			};
		}
		if (widget.kind == "tasksov") {
			taskSoV(data,widget.id,widget.metricGroup,widget.metric);
		}
		if (widget.kind == "metricsov") {
			metricSoV(data,widget.id,widget.metricGroup);
		}
		if (widget.kind == "legend") {
			legend(data,widget.id,widget.metricGroup);
		}
	}
};


function getData() {
	$.ajax({
		  dataType: "json",
		  url: metricsSourceURL,
		  success: success
		});
	};

function abortTimer(id) { // to be called when you want to stop the timer
	clearInterval(id);
}
