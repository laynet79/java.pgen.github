package com.lthorup.parsergen;

import java.util.*;

public class Product {

	public int id;
	public Symbol head;
	public ArrayList<Symbol> tail;
	public String action;
	
	private static int nextId = 0;
	
	public Product(Symbol head) {
		this.id = nextId++;
		this.head = head;
		this.tail = new ArrayList<Symbol>();
	}
}
