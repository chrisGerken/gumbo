
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
	width = width + 40;

 	var x = 10;
 	var y = 25;
	for (var i in mg.metrics) {
		ctxLegend.fillStyle = "rgba("+mg.metrics[i].color+",1.0)";
		ctxLegend.fillRect(x,y-18,20,20);

		ctxLegend.fillStyle = "rgba(0,0,0,1.0)";
		ctxLegend.fillText(mg.metrics[i].legend,x+25,y);
		
		if (mg.metrics[i].trend > 0) {
			var len = ctxLegend.measureText(mg.metrics[i].legend).width;
			ctxLegend.fillStyle = "#FF0000";
			ctxLegend.beginPath();
			ctxLegend.arc(x+len+36,y-5,6,0,2.0*Math.PI);
			ctxLegend.closePath();
			ctxLegend.fill();
		}

		y = y + 25;
		if (y > (canvasLegend.height - 30)) {
		    y = 25;
		    x = x + width + 15;
		}
	}
			
};

function hotspot(data,id) {
    var canvas = document.getElementById(id);
    var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.font = '14pt Calibri';

	var height = 18;
	var radius = 11;
	var baseline = height;
	
	for (var i in data.hotspots) {
		var hotspot = data.hotspots[i];
		var lines = Math.max(hotspot.input.length,hotspot.output.length);

		var x = 10;
		var	y = baseline;
		var x1 = canvas.width*1/3;
		var x2 = canvas.width*2/3;
		
		ctx.fillStyle = "rgba(0,0,0,1.0)";
		var len = ctx.measureText(hotspot.component).width;
		ctx.fillText(hotspot.component,canvas.width/2-(len/2),y+10);
	    ctx.beginPath();

		var lx = canvas.width/2-(len/2)-radius;
		var rx = canvas.width/2+(len/2)+radius;
		ctx.moveTo(lx,y-radius+3);
		ctx.lineTo(rx,y-radius+3);
	    ctx.arc(rx,y+3,radius,1.5*Math.PI,0.5*Math.PI,false);	    
		ctx.lineTo(lx,y+radius+3);
	    ctx.arc(lx,y+3,radius,0.5*Math.PI,1.5*Math.PI,false);	    

		ctx.moveTo(x1,y-5);
		ctx.lineTo(x1,y+(lines*height));
		ctx.moveTo(x2,y-5);
		ctx.lineTo(x2,y+(lines*height));
		ctx.closePath();
		ctx.stroke();
				
		for (var s in hotspot.input) {
			x = 20;
			y = baseline + s * height * lines;
			ctx.fillStyle = "rgba(0,0,0,1.0)";
			len = ctx.measureText(hotspot.input[s].stream).width;
			ctx.fillText(hotspot.input[s].stream,x1-30-len,y+5);
			ctx.beginPath();
			if (hotspot.input[s].ok) {
				ctx.fillStyle = "rgba(0,240,0,1.0)";
			} else {
				ctx.fillStyle = "rgba(240,0,0,1.0)";
			};
			ctx.arc(x1-15,y,5,0,Math.PI*2.0,true);	    
			ctx.closePath();
			ctx.fill();
		}
		
		for (var s in hotspot.output) {
		    x = canvas.width*2/3;
			y = baseline + s * height * lines;
			ctx.fillStyle = "rgba(0,0,0,1.0)";
			ctx.fillText(hotspot.output[s].stream,x2+30,y+5);
		    ctx.beginPath();
			if (hotspot.output[s].ok) {
				ctx.fillStyle = "rgba(0,240,0,1.0)";
			} else {
				ctx.fillStyle = "rgba(240,0,0,1.0)";
			};
			ctx.arc(x2+15,y,5,0,Math.PI*2.0,true);	    
			ctx.closePath();
			ctx.fill();
		}
	
		baseline = baseline + height * lines + 70;
	}
			
};

function topology(data,id,vertical) {
    var canvas = document.getElementById(id);
    var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.font = '14pt Calibri';

    var graph = data.graph;
    var perlevel = 45;
    var perchar  = 8;
    var vmargin = 0;

	if (!canvas.gumboMsg) {
		canvas.gumboMsg = "";
	}
	
	ctx.fillStyle = "rgba(0,0,0,1.0)";
	ctx.fillText(canvas.gumboMsg,10,15);
    
    var hotspots = new Array();

	for (var i in graph.nodes) {
		var node = graph.nodes[i];

		var x = node.location.order;
		var y = node.location.depth - vmargin;

		if (!vertical) {
			var tx = y;  
			var ty = x ;
			x = tx;
			y = ty;
		}
		
		hotspots.push( { name:("Component: "+node.component), x:x, y:y } );
		
		ctx.fillStyle = "rgba(0,0,0,1.0)";
		ctx.strokeStyle = "rgba(0,0,0,1.0)";
//		ctx.fillText(node.component,x,y);
	    ctx.beginPath();
		ctx.arc(x,y,3,0,Math.PI*2.0,true);	    
		ctx.closePath();
		ctx.stroke();
		ctx.fill();
	}

	var prevwidth = ctx.lineWidth;
	ctx.lineWidth = 3;
	for (var i in graph.edges) {
		var edge = graph.edges[i];
		
		ctx.fillStyle = "rgba(0,0,0,1.0)";
		ctx.strokeStyle = "rgba(0,0,0,1.0)";
		if (edge.trend > 0) {
			ctx.fillStyle = "rgba("+edge.color+",1.0)";
			ctx.strokeStyle = "rgba("+edge.color+",1.0)";
		}
		
	    ctx.beginPath();

		var prevx = 0;
		var prevy = 0;
		var first = true;
		
		var x;
		var y;
		var prevx;
		var prevy;
		
		for (var s in edge.segments) {
			var segment = edge.segments[s];
			for (var ip in segment.innerPath) {
				var wp = segment.innerPath[ip];
				x = wp.depth;
				y = wp.order - vmargin;
			    if (first) {
			    	ctx.moveTo(x,y);		    
				    first = false;
		    	} else {
				    ctx.lineTo(x,y);
					hotspots.push( { name:("Stream: "+edge.stream), x:((x+prevx)/2), y:((y+prevy)/2) } );
		    	}
		    	prevx = x;
		    	prevy = y;
			}
		
		}
        
		ctx.closePath();
		ctx.stroke();
		
	}
	ctx.lineWidth = prevwidth;	
	
	if (graph.created != canvas.gumboId) {
	    canvas.gumboHs = hotspots;
		canvas.gumboId = graph.created;
		canvas.onmousemove = function(e) {

			var ex = e.pageX - $(this).offset().left;
			var ey = e.pageY - $(this).offset().top;

			var displayText = "";
			for (var i in e.target.gumboHs) {
				var hotspot = e.target.gumboHs[i];
                var dx = ex - hotspot.x;
                var dy = ey - hotspot.y;
                var d = Math.abs(dx)+Math.abs(dy); 
                if (d<10) {
                    displayText = hotspot.name;
                }
			}
			
			canvas.gumboMsg = displayText;
						
		}
	}
	
	// onmouseover  onmouseout	
		
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
	
	    newdiv.gumboId = 0;
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
		if (widget.kind == "hotspot") {
			hotspot(data,widget.id);
		}
		if (widget.kind == "topology") {
		    var vertical = false;
			if (widget.hasOwnProperty("vertical")) {
			    vertical = widget.vertical;
			};
			topology(data,widget.id,vertical);
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
