package com.lthorup.parsergen;

public class Action {

	public enum Type { SHIFT, REDUCE, ACCEPT, GOTO, ERROR };
	
	public Type type;
	public int value;
	
	public Action() {
		type = Type.ERROR;
		value = 0;
	}
	
	public Action(Type type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public void set(Type type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public boolean equal(Action a) {
		return a.type == type && a.value == value;
	}
}
