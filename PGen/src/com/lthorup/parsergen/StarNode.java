package com.lthorup.parsergen;

public class StarNode extends Node {

	Node child;
	
	public StarNode(Node child) {
		this.child = child;
	}
	
	@Override
	public void initialize() {
		child.initialize();
		nullable = true;
		firstPos = child.firstPos;
		lastPos = child.lastPos;
		
		for (PosNode last : lastPos)
			for (PosNode first : firstPos)
				last.followPos.add(first);
	}

	@Override
	public Node copy() {
		return new StarNode(child.copy());
	}

	@Override
	public void dumpFollows() {
		child.dumpFollows();

	}

}
