Parser TestParser
	Tokens
		WS		[\s\n\t]+		{}
		PLUS	\+				{ return PLUS; }
		MULT	\*				{ return MULT; }
		LPN		\(				{ return LPN;  }
		RPN		\)				{ return RPN;  }
		NUM		[0-9]+			{ mTokValue = (void*)tokenInt(); return NUM;  }

	Syntax
		E	=   E PLUS T		{ mArg[0] = (void*)((int)mArg[1] + (int)mArg[3]); cout << "E = E + T" << endl; }
		E	=	T				{ mArg[0] = mArg[1]; cout << "E = T" << endl;     }
		T	=   T MULT F		{ mArg[0] = (void*)((int)mArg[1] * (int)mArg[3]); cout << "T = T * F" << endl; }
		T	=	F				{ mArg[0] = mArg[1]; cout << "T = F" << endl;     }
		F	=	LPN E RPN		{ mArg[0] = mArg[2]; cout << "F = (E)" << endl;   }
		F	=	NUM				{ mArg[0] = mArg[1]; cout << "F = NUM=" << (int)mArg[1] << endl;    }

	Error						{ cout << "parse error on line " << mLine << endl; }
