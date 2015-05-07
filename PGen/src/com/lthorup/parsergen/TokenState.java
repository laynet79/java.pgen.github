package com.lthorup.parsergen;

import java.util.*;

public class TokenState {
	public TokenState() {
		accept = false;
		nextState = new int[128];
		pos = new HashSet<PosNode>();
		for (int i = 0; i < 128; i++)
			nextState[i] = Const.ERR;		
	}
	
	public TokenState(Set<PosNode> pos) {
		this.pos = pos;
		accept = false;
		nextState = new int[128];
		for (int i = 0; i < 128; i++)
			nextState[i] = Const.ERR;
	}
	
	public Set<PosNode> pos;
	public int[]        nextState;
	public boolean      accept;
}
