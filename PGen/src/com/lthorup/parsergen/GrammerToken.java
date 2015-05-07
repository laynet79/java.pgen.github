package com.lthorup.parsergen;

public class GrammerToken {

	enum Type { SYMBOL, EQ, ACTION, END, ANY }
	
	public String name;
	public Type type;
	
	public GrammerToken(String name, Type type) {
		this.name = name;
		this.type = type;
	}
}
