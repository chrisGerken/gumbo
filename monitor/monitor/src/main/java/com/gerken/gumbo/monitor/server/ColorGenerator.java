package com.gerken.gumbo.monitor.server;

import java.util.HashSet;

public class ColorGenerator {

	private HashSet<String> usedColors = new HashSet<String>();
	
	private int rinc = 97;
	private int ginc = 71;
	private int binc = 53;

	private int r = 0;
	private int g = 0;
	private int b = 0;
	
	private int cmax = 255;
	
	public ColorGenerator() {

	}

	public String nextColor() {
		String cand = genColor();
		while ((cand != null) && (usedColors.contains(cand))) {
			cand = genColor();
		}
		if (cand == null) {
			return "0,0,0";
		}
		usedColors.add(cand);
		return cand;
	}

	public String genColor() {

		r = r + rinc;
		if (r > cmax) {
			r = r % cmax;
			g = g + ginc;
			if (g > cmax) {
				g = g % cmax;
				b = b + binc;
				if (b > cmax) {
					b = b % cmax;
				}
			}
		}
		return ""+r+","+g+","+b;
	}

	public static void main(String args[]) {
		ColorGenerator cg = new ColorGenerator();
		int i = 0;
		String c = cg.nextColor();
		while (c!=null) {
			i++;
			System.out.println(""+i+"\t"+c);
			c = cg.nextColor();
		}
	}

	public void used(String color) {
		usedColors.add(color);
	}
}
