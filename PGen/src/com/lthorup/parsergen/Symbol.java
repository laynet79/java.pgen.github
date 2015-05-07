package com.lthorup.parsergen;

import java.util.*;

public class Symbol {

	public enum Type { TERM, NONTERM };
	
	public String name;
	public int id;
	public Type type;
	public ArrayList<Symbol> follows;
	public boolean hasNull;
	public boolean firstTested;
	public boolean followsTested;
	
	private static int nextId = 0;
	private static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	
	public Symbol(String name, Type type) {
		this.name = name;
		this.id = nextId++;
		this.type = type;
		follows = new ArrayList<Symbol>();
		hasNull = firstTested = followsTested = false;
	}
	
	public static Symbol add(String name, boolean forceUnique) throws Exception {
		for (Symbol s : symbols)
			if (s.name.equals(name)) {
				if (forceUnique)
					throw new Exception("Symbol already defined: " + name);
				return s;
			}
		Symbol s = new Symbol(name, Type.NONTERM);
		symbols.add(s);
		return s;
	}
	
	public static ArrayList<Symbol> symbols() { return symbols; }
}
