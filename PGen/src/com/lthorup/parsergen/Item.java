package com.lthorup.parsergen;

public class Item {

	public Product prod;
	public int dot;
	
	public Item(Product prod, int dot) {
		this.prod = prod;
		this.dot = dot;
	}
	
	public boolean equals(Item n) {
		return prod == n.prod && dot == n.dot;
	}
	
	public boolean lessThan(Item n) {
		return (prod.id < n.prod.id) || ((prod == n.prod) && (dot < n.dot));
	}
	
}
