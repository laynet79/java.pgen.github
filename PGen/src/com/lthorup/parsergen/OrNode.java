package com.lthorup.parsergen;

public class OrNode extends Node {

	private Node child1, child2;
	
	public OrNode(Node child1, Node child2) {
		this.child1 = child1;
		this.child2 = child2;
	}
	
	@Override
	public void initialize() {
		child1.initialize();
		child2.initialize();
		nullable = child1.nullable || child2.nullable;

		firstPos.addAll(child1.firstPos); // set union
		firstPos.addAll(child2.firstPos);
		lastPos.addAll(child1.lastPos);   // set union
		lastPos.addAll(child2.lastPos);
	}

	@Override
	public Node copy() {
		return new OrNode(child1.copy(), child2.copy());
	}

	@Override
	public void dumpFollows() {
		child1.dumpFollows();
		child2.dumpFollows();
	}

}
