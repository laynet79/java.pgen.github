package com.lthorup.parsergen;

import java.util.*;

public abstract class Node {

	protected boolean nullable;
	protected Set<PosNode> firstPos = new HashSet<PosNode>();
	protected Set<PosNode> lastPos = new HashSet<PosNode>();
	protected Set<PosNode> followPos = new HashSet<PosNode>();
	
	public Node() {}
	
	public abstract void initialize();
	public abstract Node copy();
	public abstract void dumpFollows();
	
	public boolean nullable()       { return nullable;  }
	public Set<PosNode> firstPos()  { return firstPos;  }
	public Set<PosNode> lastPos()   { return lastPos;   }
	public Set<PosNode> followPos() { return followPos; }
	public void setFirstPos(Set<PosNode> firstPos)   { this.firstPos  = firstPos;  }
	public void setLastPos(Set<PosNode> lastPos)     { this.lastPos   = lastPos;   }
	public void setFollowPos(Set<PosNode> followPos) { this.followPos = followPos; }
}
