package com.lthorup.parsergen;

//-----------------------------------------------------
// This parser file is auto generated by ParserGen
// and should not be edited by hand.
//-----------------------------------------------------

public class TestParser
{
	// constructor
	public TestParser () {}
	
	//----------- user definitions --------------

	//-------------------------------------------

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
					case 1: argCnt = 3; head = 7; break;
					case 2: argCnt = 1; head = 7; break;
					case 3: argCnt = 3; head = 8; break;
					case 4: argCnt = 1; head = 8; break;
					case 5: argCnt = 3; head = 9; break;
					case 6: argCnt = 1; head = 9; break;

				}
				mArg[0] = 0;
				for (int i = argCnt; i >= 1; i--)
				{
					mTos -= 2;
					mArg[i] = mStack[mTos--];
				}

				switch (a.mValue)
				{
					case 1: { mArg[0] = (void*)((int)mArg[1] + (int)mArg[3]); cout << "E = E + T" << endl; } break;
					case 2: { mArg[0] = mArg[1]; cout << "E = T" << endl;     } break;
					case 3: { mArg[0] = (void*)((int)mArg[1] * (int)mArg[3]); cout << "T = T * F" << endl; } break;
					case 4: { mArg[0] = mArg[1]; cout << "T = F" << endl;     } break;
					case 5: { mArg[0] = mArg[2]; cout << "F = (E)" << endl;   } break;
					case 6: { mArg[0] = mArg[1]; cout << "F = NUM=" << (int)mArg[1] << endl;    } break;

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
				{ cout << "parse error on line " << mLine << endl; }
				return null;
			}
		}
	}

	// size constants
	final int STACK_SIZE = 65536;
	final int MAX_ARGS = 50;

	// token type ids
	final int ERR = -1;
	final int END = 0;
	final int PLUS = 1;
	final int MULT = 2;
	final int LPN = 3;
	final int RPN = 4;
	final int NUM = 5;


	// parser utility functions
	private int  nextToken()
	{
		// Find a state
		int tok;
		for (tok = 0; tok < 7; tok++)
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
			case 2: { return PLUS; }
			case 3: { return MULT; }
			case 4: { return LPN;  }
			case 5: { return RPN;  }
			case 6: { mTokValue = (void*)tokenInt(); return NUM;  }

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
		new bool[] {false,true,}),
	new TokenMachine( /* WS(1) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new bool[] {false,true,}),
	new TokenMachine( /* PLUS(2) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new bool[] {false,true,}),
	new TokenMachine( /* MULT(3) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new bool[] {false,true,}),
	new TokenMachine( /* LPN(4) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new bool[] {false,true,}),
	new TokenMachine( /* RPN(5) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new bool[] {false,true,}),
	new TokenMachine( /* NUM(6) */
		new int[][] {
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			{ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,1,1,1,1,1,1,1,1,1,1,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,ERR,},
			},
		new bool[] {false,true,}),

	};

	boolean[] mTokIgnore = {false,true,false,false,false,false,false,};

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
		{new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(SFT,1),new Act(ERR,0),new Act(SFT,2),new Act(ERR,0),new Act(GTO,12),new Act(GTO,12),new Act(GTO,12),},
		{new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(SFT,1),new Act(ERR,0),new Act(SFT,2),new Act(ERR,0),new Act(GTO,12),new Act(GTO,12),new Act(GTO,12),},
		{new Act(RED,6),new Act(RED,6),new Act(RED,6),new Act(ERR,0),new Act(RED,6),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(ACC,0),new Act(SFT,7),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,2),new Act(RED,2),new Act(SFT,8),new Act(ERR,0),new Act(RED,2),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,4),new Act(RED,4),new Act(RED,4),new Act(ERR,0),new Act(RED,4),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(ERR,0),new Act(SFT,7),new Act(ERR,0),new Act(ERR,0),new Act(SFT,9),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(SFT,1),new Act(ERR,0),new Act(SFT,2),new Act(ERR,0),new Act(ERR,0),new Act(GTO,12),new Act(GTO,12),},
		{new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(SFT,1),new Act(ERR,0),new Act(SFT,2),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(GTO,12),},
		{new Act(RED,5),new Act(RED,5),new Act(RED,5),new Act(ERR,0),new Act(RED,5),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,1),new Act(RED,1),new Act(SFT,8),new Act(ERR,0),new Act(RED,1),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},
		{new Act(RED,3),new Act(RED,3),new Act(RED,3),new Act(ERR,0),new Act(RED,3),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),new Act(ERR,0),},

	};
}