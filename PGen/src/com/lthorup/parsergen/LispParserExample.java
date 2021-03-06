package com.lthorup.parsergen;

//-----------------------------------------------------
//This parser file is auto generated by PGEN.EXE and
//should not be edited by hand.
//-----------------------------------------------------

public class LispParserExample
{
	// constructor
	public LispParserExample() {}
	
	

	// parses the given input string
	public Object parse(String input)
	{
		initInput(input);
		mTokValue = null;
		mStack[0] = 0;
		mTos = 0;
		Act err = new Act(ERR,0);

		int t = nextToken();
		while (true)
		{
			int state = (Integer)mStack[mTos];
			Act a = (t == ERR) ? err : mAction[state][t];

			if (a.mType == SFT)
			{
				mStack[++mTos] = mTokValue;
				mStack[++mTos] = t;
				mStack[++mTos] = a.mValue;
				t = nextToken();
			}
			else if (a.mType == RED)
			{
				int argCnt = 0;
				int head = 0;
				switch (a.mValue)
				{
					case 1: argCnt = 2; head = 8; break;
					case 2: argCnt = 0; head = 8; break;
					case 3: argCnt = 1; head = 9; break;
					case 4: argCnt = 1; head = 9; break;
					case 5: argCnt = 1; head = 9; break;
					case 6: argCnt = 1; head = 9; break;
					case 7: argCnt = 2; head = 9; break;
					case 8: argCnt = 3; head = 10; break;
					case 9: argCnt = 2; head = 11; break;
					case 10: argCnt = 0; head = 11; break;
				}
				mArg[0] = 0;
				for (int i = argCnt; i >= 1; i--)
				{
					mTos -= 2;
					mArg[i] = mStack[mTos--];
				}

				switch (a.mValue)
				{
					case 1: { System.out.println("explist		= exp explist"); } break;
					case 2: { System.out.println("explist		="); } break;
					case 3: { System.out.println("exp			= SYMBOL"); } break;
					case 4: { System.out.println("exp			= REAL");; } break;
					case 5: { System.out.println("exp			= INTEGER"); } break;
					case 6: { System.out.println("exp			= list"); } break;
					case 7: { System.out.println("exp			= QUOTE exp"); } break;
					case 8: { System.out.println("list		= LPN listbody RPN"); } break;
					case 9: { System.out.println("listbody	= exp listbody"); } break;
					case 10: { System.out.println("listbody	="); } break;
				}

				state = (Integer)mStack[mTos];
				mStack[++mTos] = mArg[0];
				mStack[++mTos] = head;
				mStack[++mTos] = mAction[state][head].mValue;
			}
			else if (a.mType == ACC)
			{
				mTos -= 2;
				return mStack[mTos];
			}
			else
			{
				{ System.out.println("Error"); }
				return 0;
			}
		}
	}

	// size constants
	final int STACK_SIZE = 65536;
	final int MAX_ARGS = 50;

	// token type ids
	final int ERR = -1;
	final int END = 0;
	final int SYMBOL = 1;
	final int REAL = 2;
	final int INTEGER = 3;
	final int LPN = 4;
	final int RPN = 5;
	final int QUOTE = 6;

	// parser utility functions
	private int  nextToken()
	{
		// Find a state
		int tok;
		for (tok = 0; tok < 9; tok++)
		{
			TokenMachine tm = mTokMachines[tok];
			int state = 0;
			char c = nextChar();

			while (tm.nextState(state, c) != ERR)
			{
				state = tm.nextState(state, c);
				c = nextChar();
			}
			if (tm.mAccepted[state])
			{
				acceptTokStr();
				if (mTokIgnore[tok])
					tok = -1;
				else
					break;
			}
			else
				rejectTokStr();
		}

		switch (tok)
		{
			case 0: { return END; }
			case 1: {} break;
			case 2: {} break;
			case 3: {
				return SYMBOL;
			}
			case 4: {
				return REAL;
			}
			case 5: {
				return INTEGER;
			}
			case 6: { return LPN; }
			case 7: { return RPN; }
			case 8: { return QUOTE; }
			default: break;
		}

		return ERR;
	}

	private char nextChar()
	{
		char c = (char)0;
		if (mInputNext < mInput.length())
			c = mInput.charAt(mInputNext);
		mInputNext++;
		if (c == '\n')
			mLine++;
		return c;
	}

	void initInput(String buffer)
	{
		mInput      = buffer;
		mInputStart = 0;
		mInputNext  = 0;
		mLine = 1;
	}

	void acceptTokStr()
	{
		if (mInputStart < mInput.length())
		{
			mInputNext--;
			mTokStr = mInput.substring(mInputStart, mInputNext-mInputStart);
			mInputStart = mInputNext;
		}
	}

	void rejectTokStr()
	{
		mInputNext  = mInputStart;
	}

	// Parser input string state
	int		mLine;
	String	mInput;
	int		mInputStart;
	int		mInputNext;
	String	mTokStr;
	Object	mTokValue;

	// Functions used to get the value of the current token
	String  tokenStr()		{ return mTokStr;						}
	int     tokenInt()		{ return Integer.parseInt(mTokStr);			}
	float   tokenFloat()	{ return Float.parseFloat(mTokStr);	}
	double  tokenDouble()	{ return Double.parseDouble(mTokStr);			}

	// parser stack, and production argument passing
	int	     mTos;
	Object[] mStack = new Object[STACK_SIZE];
	Object[] mArg   = new Object[MAX_ARGS];

	// token state machine
	class TokenMachine
	{
		public TokenMachine(int[][] state, boolean[] accepted) { mState = state; mAccepted = accepted; }
		public int nextState(int state, char c) { return mState[state][c]; }
		public boolean accepted(int state) { return mAccepted[state]; }
		public int[][] mState;
		public boolean[] mAccepted;
	}
	TokenMachine[] mTokMachines = 
	{
	new TokenMachine( /* END(0) */
		new int[][] {
			{1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,}),
	new TokenMachine( /* WS(1) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,}),
	new TokenMachine( /* COMMENT(2) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,ERR,},
			},
		new boolean[] {false,true,}),
	new TokenMachine( /* SYMBOL(3) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,1,1,ERR,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,1,ERR,ERR,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,1,ERR,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,1,1,2,1,1,1,2,2,2,2,2,2,2,2,2,2,ERR,ERR,1,1,1,ERR,ERR,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,1,ERR,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,2,ERR,ERR,ERR,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,ERR,ERR,2,2,2,ERR,ERR,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,ERR,ERR,ERR,ERR,2,ERR,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,true,}),
	new TokenMachine( /* REAL(4) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,0,ERR,0,ERR,ERR,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,2,ERR,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,3,3,3,3,3,3,3,3,3,3,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,3,3,3,3,3,3,3,3,3,3,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,false,false,true,}),
	new TokenMachine( /* INTEGER(5) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,0,ERR,0,ERR,ERR,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,}),
	new TokenMachine( /* LPN(6) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,}),
	new TokenMachine( /* RPN(7) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,}),
	new TokenMachine( /* QUOTE(8) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new boolean[] {false,true,}),
	};

	boolean[] mTokIgnore = {false,true,true,false,false,false,false,false,false,};

	// parser state machine
	final int SFT = 0;
	final int RED = 1;
	final int ACC = 2;
	final int GTO = 3;
	class Act
	{ 
		public int mType;
		public int mValue;
		public Act(int type, int value){ mType = type; mValue = value; }
	};
	Act[][] mAction =
	{
		{new Act(RED,2),new Act(SFT,1),new Act(SFT,2),new Act(SFT,3),new Act(SFT,4),new Act(ERR,0),new Act(SFT,5),new Act(ERR,0),new Act(GTO,6),new Act(GTO,7),new Act(GTO,8),new Act(ERR,0),},
		{new Act(RED,3),new Act(RED,3),new Act(RED,3),new Act(RED,3),new Act(RED,3),new Act(RED,3),new Act(RED,3),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,4),new Act(RED,4),new Act(RED,4),new Act(RED,4),new Act(RED,4),new Act(RED,4),new Act(RED,4),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,5),new Act(RED,5),new Act(RED,5),new Act(RED,5),new Act(RED,5),new Act(RED,5),new Act(RED,5),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(ERR,0),new Act(SFT,1),new Act(SFT,2),new Act(SFT,3),new Act(SFT,4),new Act(RED,10),new Act(SFT,5),new Act(ERR,0),new Act(ERR,0),new Act(GTO,9),new Act(GTO,8),new Act(GTO,10),},
		{new Act(ERR,0),new Act(SFT,1),new Act(SFT,2),new Act(SFT,3),new Act(SFT,4),new Act(ERR,0),new Act(SFT,5),new Act(ERR,0),new Act(ERR,0),new Act(GTO,11),new Act(GTO,8),new Act(ERR,0),},
		{new Act(ACC,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,2),new Act(SFT,1),new Act(SFT,2),new Act(SFT,3),new Act(SFT,4),new Act(ERR,0),new Act(SFT,5),new Act(ERR,0),new Act(GTO,12),new Act(GTO,7),new Act(GTO,8),new Act(ERR,0),},
		{new Act(RED,6),new Act(RED,6),new Act(RED,6),new Act(RED,6),new Act(RED,6),new Act(RED,6),new Act(RED,6),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(ERR,0),new Act(SFT,1),new Act(SFT,2),new Act(SFT,3),new Act(SFT,4),new Act(RED,10),new Act(SFT,5),new Act(ERR,0),new Act(ERR,0),new Act(GTO,9),new Act(GTO,8),new Act(GTO,13),},
		{new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(SFT,14),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,7),new Act(RED,7),new Act(RED,7),new Act(RED,7),new Act(RED,7),new Act(RED,7),new Act(RED,7),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,1),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(RED,9),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,8),new Act(RED,8),new Act(RED,8),new Act(RED,8),new Act(RED,8),new Act(RED,8),new Act(RED,8),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
	};
}
