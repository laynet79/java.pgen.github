package com.lthorup.parsergen;

public class CatNode extends Node {

	private Node child1, child2;
	
	public CatNode(Node child1, Node child2) {
		this.child1 = child1;
		this.child2 = child2;
	}
	
	
	@Override
	public void initialize() {
		child1.initialize();
		child2.initialize();
		
		nullable = child1.nullable && child2.nullable;
		if (child1.nullable) {
			firstPos.addAll(child1.firstPos); // set union
			firstPos.addAll(child2.firstPos);
		}
		else
			firstPos = child1.firstPos;
		
		if (child2.nullable){
			lastPos.addAll(child1.lastPos); // set union
			lastPos.addAll(child2.lastPos);
		}
		else
			lastPos = child2.lastPos;
		
		for (PosNode last : child1.lastPos)
			for (PosNode first : child2.firstPos)
				last.followPos.add(first);
	}

	@Override
	public Node copy() {
		return new CatNode(child1.copy(), child2.copy());
	}

	@Override
	public void dumpFollows() {
		child1.dumpFollows();
		child2.dumpFollows();
	}
}
