Parser LispParser
	Definitions
		{
		}
		
	Tokens
		WS		[\s\t\n]+
				{}
		COMMENT	\;[\t\s-\~]*
				{}
		SYMBOL	[a-zA-Z\+\-\*\/\_\>\<\=\.&]+[0-9,a-zA-Z\+\-\*\/\_\>\<\=\.&]*
				{
					return SYMBOL;
				}
		REAL	[\+\-]*[0-9]+\.[0-9]+
				{
					return REAL;
				}
		INTEGER	[\+\-]*[0-9]+
				{
					return INTEGER;
				}
		LPN		\(
				{ return LPN; }
		RPN		\)
				{ return RPN; }
		QUOTE	\'
				{ return QUOTE; }

	Syntax
		explist		= exp explist		{ Console.WriteLine("explist	= exp explist"); }
		explist		=					{ Console.WriteLine("explist	="); }
		exp			= SYMBOL			{ Console.WriteLine("exp		= SYMBOL"); }
		exp			= REAL				{ Console.WriteLine("exp		= REAL");; }
		exp			= INTEGER			{ Console.WriteLine("exp		= INTEGER"); }
		exp			= list				{ Console.WriteLine("exp		= list"); }
		exp			= QUOTE exp			{ Console.WriteLine("exp		= QUOTE exp"); }
		list		= LPN listbody RPN	{ Console.WriteLine("list		= LPN listbody RPN"); }
		listbody	= exp listbody		{ Console.WriteLine("listbody	= exp listbody"); }
		listbody	=					{ Console.WriteLine("listbody	="); }

	Error		{ Console.WriteLine("Error"); }
