package com.lthorup.parsergen;

import java.util.*;

public class ItemListList {

	private ArrayList<ItemList> itemLists = new ArrayList<ItemList>();
	
	public ItemListList() {}
	
	public int size() { return itemLists.size(); }
	public void clear() { itemLists.clear(); }
	public void add(ItemList i) { itemLists.add(i); }	
	public ItemList get(int index) { return itemLists.get(index); }
	public ArrayList<ItemList> itemLists() { return itemLists; }
	
	public boolean contains(ItemList itemList) {
		for (ItemList i : itemLists) {
			if (i.equals(itemList))
				return true;
		}
		return false;
	}
}
