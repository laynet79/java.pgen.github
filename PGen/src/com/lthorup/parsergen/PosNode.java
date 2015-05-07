package com.lthorup.parsergen;

public class PosNode extends Node {
	
	private int id;
	private char symbol;
	private static int nextPos = 1;
	
	public PosNode(char symbol) {
		id = nextPos++;
		this.symbol = symbol;
	}
	
	public static void resetNextPos() { nextPos = 1; }
	
	public int id() { return id; }
	public char symbol() { return symbol; }
	
	@Override
	public void initialize() {
		if (symbol == Const.EPS)
			nullable = true;
		else {
			nullable = false;
			firstPos.add(this);
			lastPos.add(this);
		}
	}

	@Override
	public Node copy() {
		return new PosNode(symbol);
	}

	@Override
	public void dumpFollows() {
		System.out.printf("%d ---> ", id);
		for (PosNode p : followPos)
			System.out.printf("%d ", p.id);
		System.out.printf("\n");
	}

}
