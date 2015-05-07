package com.lthorup.parsergen;

import java.util.*;

public class Token {

	public Token(String name, String regExp, Node syntaxTree, String action) {
		this.name = name;
		this.regExp = regExp;
		this.syntaxTree = syntaxTree;
		states = new ArrayList<TokenState>();
		this.action = action;
	}
	
	public String name;
	public String regExp;
	public Node syntaxTree;
	public ArrayList<TokenState> states;
	public String action;
}
