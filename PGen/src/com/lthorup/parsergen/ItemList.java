package com.lthorup.parsergen;

import java.util.*;

public class ItemList {

	private ArrayList<Item> items = new ArrayList<Item>();
	
	public ItemList() {}
	
	public int size() { return items.size(); }
	public void clear() { items.clear(); }
	public void add(Item i) { items.add(i); }	
	public Item get(int index) { return items.get(index); }
	public ArrayList<Item> items() { return items; }
	
	public ItemList copy() {
		ItemList a = new ItemList();
		for (Item i : items)
			a.add(i);
		return a;
	}
	
	public boolean equals(ItemList a) {
		if (a.items.size() != items.size())
			return false;
		for (Item s : a.items) {
			if (! contains(s))
				return false;
		}
		return true;
	}
	
	public boolean contains(Item i) {
		for (Item s : items) {
			if (s.equals(i))
				return true;
		}
		return false;
	}
	
	public String toString() {
		String s = "[";
		int cnt = 0;
		for (Item i : items) {
			s = s + String.format("%s{%d,%d}", cnt > 0 ? "," : "", i.prod.id, i.dot);
			cnt++;
		}
		s = s + "]";
		return s;
	}
	
	
}
