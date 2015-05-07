package com.lthorup.parsergen;

import java.util.*;
import java.io.*;

import com.lthorup.parsergen.LispParserExample.Act;
import com.lthorup.parsergen.LispParserExample.TokenMachine;
import com.lthorup.parsergen.Symbol.Type;

public class ParserGen {	

	enum RegExpToken  { LPN, RPN, LBK, RBK, DSH, STR, PLS, QST, OR, SYM, END }
	
	private ArrayList<Token> tokens = new ArrayList<Token>();
	private ArrayList<Product> productions = new ArrayList<Product>();
	private ItemListList canon = new ItemListList();
	private Action[][] parserTable;
	
	private String def;
	private String className;
	private String errorAction;
	
	private String regExp;
	private int regExpNext;
	private int regExpEnd;
	private char regExpChar;
	private boolean regExpTokenReady;
	private RegExpToken regExpToken;
	
	private String input;
	private int inputNext;
	private int inputEnd;
	private int lineNum;
	
	//---------------------------------------------
	public static void main(String[] args) {
		//if (args.length != 2) {
		//	System.out.printf("SYNTAX ERROR: pgen <filename>\n");
		//	return;   
		//}

		System.out.printf("generating parser...\n");
		
		System.out.println(System.getProperty("user.dir"));

		//String fileName = args[1];
		String fileName = "/Users/layne/Documents/workspace/PGen/LispParser.p";
		ParserGen pgen = new ParserGen();
		if (! pgen.createParser(fileName)) {
			System.out.printf("parser generation failed\n");
			return;
		}
		System.out.printf("parser successfully created\n");
	}
	
	//---------------------------------------------
	public ParserGen() {}
	
	//---------------------------------------------
	public boolean createParser(String fileName) {
		try {
			loadGrammer(fileName);
		} catch (Exception e) {
			System.out.printf("ERROR(line %d): %s\n", lineNum, e.getMessage());
			return false;
		}
		buildLexStateMachines();
		if (! buildParserStateMachine())
			return false;
		outputParser();
		return true;
	}
	
	//---------------------------------------------
	private void loadGrammer(String fileName) throws Exception {
		// load in a symbol representing EOF (this will be at index zero)
		// create a token entry for EOF as the first token (index zero)
		Symbol eofSymbol = Symbol.add("END", false);
		eofSymbol.type = Symbol.Type.TERM;
		tokens.add(new Token("END", null, null, "{ return END; }"));

		// open the parser description file
		try {
			input = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
			inputNext = 0;
			inputEnd = input.length();
			lineNum = 1;
		} catch (Exception e) { throw new Exception("unable to open and read parser description file: " + fileName); }

		// get the name of the parser class
		GrammerToken t = nextGrammerToken(GrammerToken.Type.SYMBOL);
		if (! t.name.equals("Parser"))
			throw new Exception("Parser keyword expected");
		t = nextGrammerToken(GrammerToken.Type.SYMBOL);
		className = t.name;

		// read in the definitions section
		t = nextGrammerToken(GrammerToken.Type.SYMBOL);
		if (! t.name.equals("Definitions"))
			throw new Exception("Definitions keyword expected");
		t = nextGrammerToken(GrammerToken.Type.ACTION);
		def = t.name.substring(1, t.name.length()-1); // strip off brackets
		
		// read in the token descriptions
		t = nextGrammerToken(GrammerToken.Type.SYMBOL);
		if (! t.name.equals("Tokens"))
			throw new Exception("Tokens keyword expected");
		while (true)
		{
			t = nextGrammerToken(GrammerToken.Type.SYMBOL);
			if (t.name.equals("Syntax"))
				break;
			String name = t.name;
			t = nextGrammerToken(GrammerToken.Type.SYMBOL);
			String regExp = t.name;
			t = nextGrammerToken(GrammerToken.Type.ACTION);
			String action = t.name;

			// if not an empty action, then this is a language terminal symbol
			if (! action.equals("{}"))
			{
				Symbol s = Symbol.add(name, true);
				s.type = Symbol.Type.TERM;
			}
			Node syntaxTree = parseRegExp(regExp);
			tokens.add(new Token(name, regExp, syntaxTree, action));
		}

		// read in the syntax description
		if (! t.name.equals("Syntax"))
			throw new Exception("Syntax keyword expected");
		boolean startFound = false;
		while (true)
		{
			t = nextGrammerToken(GrammerToken.Type.SYMBOL);
			if (t.name.equals("Error"))
				break;
			String headName = t.name;

			if (! startFound)
			{
				Product prod = new Product(Symbol.add(headName + "'", true));
				prod.tail.add(Symbol.add(headName, true));
				prod.action = "{}";
				productions.add(prod);
				startFound = true;
			}

			Product prod = new Product(Symbol.add(headName, false));
			t = nextGrammerToken(GrammerToken.Type.EQ);
			while (true) {
				t = nextGrammerToken(GrammerToken.Type.ANY);
				if (t.type == GrammerToken.Type.ACTION)
					break;
				if (t.type == GrammerToken.Type.SYMBOL)
					prod.tail.add(Symbol.add(t.name, false));
				else
					throw new Exception("production symbol or action expected");
			}
			prod.action = t.name;
			productions.add(prod);
		}

		// read in the error handler action
		t = nextGrammerToken(GrammerToken.Type.ACTION);
		errorAction = t.name;
	}
	
	//---------------------------------------------
	private GrammerToken nextGrammerToken(GrammerToken.Type type) throws Exception {

		GrammerToken tok = null;
		
		// scan off white space
		while (inputNext < inputEnd && Character.isWhitespace(input.charAt(inputNext))) {
			if (input.charAt(inputNext) == '\n')
				lineNum++;
			inputNext++;
		}
		if (inputNext == inputEnd) {
			if (type == GrammerToken.Type.END) 
				tok = new GrammerToken("", GrammerToken.Type.END);
			else
				throw new Exception("unexpected end of file");
		}

		else if (input.charAt(inputNext) == '=' && (type == GrammerToken.Type.EQ || type == GrammerToken.Type.ANY))
		{
			tok = new GrammerToken("=", GrammerToken.Type.EQ);
			inputNext++;
		}
		else if (input.charAt(inputNext) == '{' && (type == GrammerToken.Type.ACTION || type == GrammerToken.Type.ANY))
		{
			int nest = 0;
			int n = inputNext+1;
			while (n < inputEnd)
			{
				if (input.charAt(n) == '{')
					nest++;
				else if (input.charAt(n) == '}')
				{
					if (nest == 0)
						break;
					else
						nest--;
				}
				n++;
			}
			if (n == inputEnd)
				throw new Exception("unterminated action");
			n++;
			String action = input.substring(inputNext, n);
			inputNext = n;
			tok = new GrammerToken(action, GrammerToken.Type.ACTION);
		}
		else if (type == GrammerToken.Type.SYMBOL || type == GrammerToken.Type.ANY)
		{
			int n = inputNext;
			while (n < inputEnd && ! Character.isWhitespace(input.charAt(n)))
				n++;
			String name = input.substring(inputNext,  n);
			inputNext = n;
			tok = new GrammerToken(name, GrammerToken.Type.SYMBOL);
		}
		if (tok == null)
			throw new Exception("expected " + type.toString());
		return tok;
	}
	
	//---------------------------------------------
	private Node parseRegExp(String regExp) throws Exception {
		PosNode.resetNextPos();
		this.regExp = regExp;
		regExpNext = 0;
		regExpEnd = regExp.length();
		regExpTokenReady = false;

		Node root = parseExpList(RegExpToken.END);
		if (root == null)
			throw new Exception("null regular expression not allowed");

		root = new CatNode(root, new PosNode(Const.AUG));
		return root;
	}
	
	//---------------------------------------------
	private Node parseExpList(RegExpToken terminator) throws Exception {
		RegExpToken peek = peekRegExpToken();
		if (peek == terminator)
		{
			nextRegExpToken();
			return null;
		}

		Node exp = parseExp();
		if (exp == null)
			throw new Exception("unable to parse expression");

		Node rest = parseExpList(terminator);
		if (rest == null)
			return exp;
		else return new CatNode(exp, rest);
	}
	
	//---------------------------------------------
	private Node parseExp() throws Exception {
		RegExpToken t = nextRegExpToken();
		Node exp;

		if (t == RegExpToken.LPN)
		{
			exp = parseExpList(RegExpToken.RPN);
			if (exp == null)
				throw new Exception("empty expression list not allowed");
		}
		else if (t == RegExpToken.LBK)
		{
			exp = parseSymList();
			if (exp == null)
				throw new Exception("empty symbol set not allowed");
		}
		else if (t == RegExpToken.SYM)
			exp = new PosNode(regExpChar);
		else
			throw new Exception("bad expression found");

		RegExpToken peek = peekRegExpToken();
		if (peek == RegExpToken.STR)
		{
			nextRegExpToken();
			exp = new StarNode(exp);
		}
		else if (peek == RegExpToken.PLS)
		{
			nextRegExpToken();
			Node exp2 = exp.copy();
			exp = new CatNode(exp, new StarNode(exp2));
		}
		else if (peek == RegExpToken.QST)
		{
			nextRegExpToken();
			exp = new OrNode(exp, new PosNode(Const.EPS));
		}
		else if (peek == RegExpToken.OR)
		{
			nextRegExpToken();
			Node other = parseExp();
			if (other == null)
				throw new Exception("bad OR statement found");
			exp = new OrNode(exp, other);
		}
		return exp;
	}
	
	//---------------------------------------------
	private Node parseSymList() throws Exception {
		RegExpToken t = nextRegExpToken();
		Node sym;
		if (t == RegExpToken.RBK)
			sym = null;

		else if (t == RegExpToken.SYM)
		{
			char startSym = regExpChar;
			if (peekRegExpToken() == RegExpToken.DSH)
			{
				nextRegExpToken();
				t = nextRegExpToken();
				if (t != RegExpToken.SYM)
					throw new Exception("incomplete symbol range specified");

				char endSym = regExpChar;
				if (endSym <= startSym)
					throw new Exception("bad symbol ramge specified");

				sym = new PosNode(startSym++);
				while (startSym <= endSym)
					sym = new OrNode(sym, new PosNode(startSym++));
			}
			else
			{
				sym = new PosNode(startSym);
			}

			Node rest = parseSymList();
			if (rest != null)
				sym = new OrNode(sym, rest);
		}
		else
			throw new Exception("invalid symbol found in symbol range");
		return sym;
	}
	
	//---------------------------------------------
	private char getRegExpChar() {
		if (regExpNext == regExpEnd)
			return (char)0;
		return regExp.charAt(regExpNext++);
	}
	
	//---------------------------------------------
	private RegExpToken peekRegExpToken() throws Exception {
		if (! regExpTokenReady)
		{
			regExpToken = nextRegExpToken();
			regExpTokenReady = true;
		}
		return regExpToken;
	}
	
	//---------------------------------------------
	private RegExpToken nextRegExpToken() throws Exception {
		if (regExpTokenReady)
		{
			regExpTokenReady = false;
			return regExpToken;
		}

		regExpChar = getRegExpChar();
		if (regExpChar == 0)
			return RegExpToken.END;

		switch(regExpChar)
		{
		case '\\':
			regExpChar = getRegExpChar();
			if (regExpChar == 0)
				throw new Exception("Unexpected EOS");
			else if (regExpChar == 's')
				regExpChar = ' ';
			else if (regExpChar == 'n')
				regExpChar = '\n';
			else if (regExpChar == 'r')
				regExpChar = '\r';
			else if (regExpChar == 't')
				regExpChar = '\t';
			return RegExpToken.SYM;

		case '(': return RegExpToken.LPN;
		case ')': return RegExpToken.RPN;
		case '[': return RegExpToken.LBK;
		case ']': return RegExpToken.RBK;
		case '-': return RegExpToken.DSH;
		case '*': return RegExpToken.STR;
		case '+': return RegExpToken.PLS;
		case '?': return RegExpToken.QST;
		case '|': return RegExpToken.OR;
		default:  return RegExpToken.SYM;
		}
	}
	
	//---------------------------------------------
	private void buildLexStateMachines() {
		// construct state machine for each token
		for (Token t : tokens)
		{
			if (t.name.equals("END"))
				buildEofTransTable(t);
			else
			{
				t.syntaxTree.initialize();
				buildTransTable(t);
			}
			// dumpTransTable(*t);
		}
		
	}
	
	//---------------------------------------------
	private void buildEofTransTable(Token t) {
		t.states.add(new TokenState());
		t.states.add(new TokenState());
		t.states.get(0).nextState[0] = 1;
		t.states.get(1).accept = true;
	}
	
	//---------------------------------------------
	private void buildTransTable(Token t) {
		t.states.add(new TokenState(t.syntaxTree.firstPos()));
		int newCnt = 1;
		int next = 0;
		while (newCnt > 0)
		{
			newCnt--;
			TokenState d = t.states.get(next);

			for (int s = 0; s < 128; s++)
			{
				char sym = (char)s;
				Set<PosNode> newPos = new HashSet<PosNode>();

				for (PosNode p : d.pos)
				{
					if (p.symbol() == Const.AUG)
						d.accept = true;

					if (p.symbol() == sym)
					{
						for (PosNode k : p.followPos())
							newPos.add(k);
					}
				}

				if (newPos.size() != 0)
				{
					boolean alreadyExists = false;
					int target = 0;
					for (TokenState q : t.states)
					{
						if (newPos.equals(q.pos))
						{
							alreadyExists = true;
							break;
						}
						target++;
					}
					if (! alreadyExists)
					{
						t.states.add(new TokenState(newPos));
						newCnt++;
					}
					d.nextState[sym] = target;
				}
			}
			next++;
		}
	}
	
	//---------------------------------------------
	private void dumpTransTable(Token t) {
		System.out.printf("%s\n", t.regExp);

		int state = 0;
		for (TokenState d : t.states)
		{
			System.out.printf("%d\t", state);
			
			/*
			System.out.printf("(");
			for (PosNode p : d.pos)
			{
				System.out.printf("%d ", p.id());
			}
			System.out.printf("(\t");
			*/

			for (int s = 0; s < 128; s++)
			{
				char sym = (char)s;
				int nextState = d.nextState[sym];
				if (nextState != Const.ERR)
				{
					String symName;
					if (sym == ' ')
						symName = "\\s";
					else if (sym == '\n')
						symName = "\\n";
					else if (sym == '\t')
						symName = "\\t";
					else
						symName = String.format("%c", sym);
					System.out.printf("(%s,%d) ", symName, nextState);
				}
			}

			if (d.accept)
				System.out.printf("accept");
			System.out.printf("\n");
			state++;
		}
		System.out.printf("\n");		
	}
	
	//---------------------------------------------
	private boolean buildParserStateMachine() {
		Items();
		// dumpCanon();
		determineFollows();
		// dumpFollows();
		if (setActions())
		{
			//dumpParserTable();
			return true;
		}
		return false;
	}
	
	//---------------------------------------------
	private void Items() {
		canon.clear();
		ItemList start = new ItemList();
		start.add(new Item(productions.get(0), 0));
		ItemList close = new ItemList();
		closure(start, close);
		ItemListList N = new ItemListList();
		N.add(close);

		while (N.size() != 0)
		{
			for (ItemList n : N.itemLists())
				canon.add(n);
			N.clear();

			for (ItemList c : canon.itemLists())
			{
				for (Symbol s : Symbol.symbols())
				{
					if (s.id == 0)
						continue;
					ItemList g = new ItemList();
					goTo(c, s, g);
					if (g.size() != 0 && ! canon.contains(g) && ! N.contains(g))
						N.add(g);
				}
			}
		}
	}
	
	//---------------------------------------------
	private void goTo(ItemList I, Symbol s, ItemList G) {
		ItemList N = new ItemList();
		for (Item i : I.items())
			if (i.prod.tail.size() > i.dot && i.prod.tail.get(i.dot) == s)
				N.add(new Item(i.prod, i.dot + 1));
		closure(N, G);
	}
	
	//---------------------------------------------
	private void closure(ItemList I, ItemList C) {
		ItemList N = new ItemList(); // set of new items that are to be added
		N = I.copy();

		while (N.size() != 0)
		{
			for (Item i : N.items())
				C.add(i);
			N.clear();

			for (Item j : C.items())
				if (j.dot < j.prod.tail.size())
				{
					Symbol head = j.prod.tail.get(j.dot);
					if (head.type == Symbol.Type.NONTERM)
						for (Product p : productions) {
							Item n = new Item(p, 0);
							
							if (p.head == head && !C.contains(n) && !N.contains(n))
								N.add(n);
						}
				}
		}
	}
	
	//---------------------------------------------
	private void dumpCanon() {
		for (ItemList c : canon.itemLists())
		{
			for (Item i : c.items())
			{
				System.out.printf("%s = ", i.prod.head.name);
				int dot = 0;
				for (Symbol t : i.prod.tail)
				{
					if (i.dot == dot)
						System.out.printf(". ");
					System.out.printf("%s ", t.name);
					dot++;
				}
				if (i.dot == i.prod.tail.size())
					System.out.printf(".");
				System.out.printf("\n");
			}
			System.out.printf("\n");
		}
	}
	
	//---------------------------------------------
	private void determineFollows() {
		// determine which non-terminals derive to NULL
		for (Symbol s : Symbol.symbols())
			if (s.type == Symbol.Type.NONTERM)
			{
				for (Product p : productions)
					if (p.head == s && p.tail.size() == 0)
					{
						s.hasNull = true;
						break;
					}
			}

		// determine the list of terminal symbols that can follow each non-terminal
		for (Symbol s : Symbol.symbols())
			if (s.type == Symbol.Type.NONTERM)
				for (Symbol t : Symbol.symbols())
					if (t.type == Symbol.Type.TERM)
					{
						if (follows(t, s))
							s.follows.add(t);
					}
		
	}
	
	//---------------------------------------------
	private boolean follows(Symbol a, Symbol B) {
		for (Symbol s : Symbol.symbols())
			s.followsTested = false;
		B.followsTested = true;
		return followsRec(a, B);
	}
	
	//---------------------------------------------
	private boolean followsRec(Symbol a, Symbol B) {
		if (a == Symbol.symbols().get(0) && B == productions.get(1).head)
			return true;

		for (Product p : productions)
			for (int t = 0; t < p.tail.size(); t++)
				if (p.tail.get(t) == B)
				{
					int n;
					for (n = t+1; n < p.tail.size(); n++)
					{
						if (first(a, p.tail.get(n)))
							return true;

						if (! p.tail.get(n).hasNull)
							break;
					}
					Symbol head = p.head;
					if (n == p.tail.size() && ! head.followsTested)
					{
						head.followsTested = true;
						if (followsRec(a, head))
							return true;
					}
				}
		return false;
		
	}
	
	//---------------------------------------------
	private boolean first(Symbol a, Symbol b) {
		if (b.type == Symbol.Type.TERM)
			return a == b;
		for (Symbol s : Symbol.symbols())
			s.firstTested = false;
		b.firstTested = true;
		return firstRec(a, b);		
	}
	
	//---------------------------------------------
	private boolean firstRec(Symbol a, Symbol b) {
		if (b.type == Symbol.Type.TERM)
			return a == b;

		for (Product p : productions)
		{
			Symbol head = p.head;
			if (head != b)
				continue;

			for (Symbol t : p.tail)
			{
				if (! t.firstTested)
				{
					t.firstTested = true;
					if (first(a, t))
						return true;
				}
				if (! t.hasNull)
					break;
			}
		}
		return false;
	}
	
	//---------------------------------------------
	private void dumpFollows() {
		for (Symbol s : Symbol.symbols())
			if (s.type == Symbol.Type.NONTERM)
			{
				System.out.printf("Follows(%s) { ", s.name);
				
				for (Symbol t : s.follows)
					System.out.printf("%s ", t.name);
				System.out.printf("}\n");
			}		
	}
	
	//---------------------------------------------
	private boolean setActions() {
		// allocate parser table with correct number of states and symbols
		parserTable = new Action[canon.size()][Symbol.symbols().size()];
		for (int i = 0; i < canon.size(); i++)
			for (int j = 0; j < Symbol.symbols().size(); j++)
				parserTable[i][j] = new Action();

		// determine the parsing actions for all terminal symbols 
		for (int state = 0; state < canon.size(); state++)
		{
			for (Item i : canon.get(state).items())
			{
				if (i.dot < i.prod.tail.size())
				{
					Symbol a = i.prod.tail.get(i.dot);
					if (a.type == Symbol.Type.TERM)
					{
						ItemList g = new ItemList();
						goTo(canon.get(state), a, g);
						int nextState;
						for (nextState = 0; nextState < canon.size(); nextState++)
							if (canon.get(nextState).equals(g))
								break;
						if (parserTable[state][a.id].type == Action.Type.SHIFT)
						{
							if (parserTable[state][a.id].value != nextState)
							{
								System.out.printf("warning SHIFT/SHIFT conflict found\n");
								return false;
							}
						}
						else if (parserTable[state][a.id].type == Action.Type.REDUCE)
							System.out.printf("warning SHIFT/REDUCE conflict found\n");

						parserTable[state][a.id].set(Action.Type.SHIFT, nextState);
					}
				}
				else if (i.dot == i.prod.tail.size())
				{
					Symbol head = i.prod.head;

					if (i.prod.id == 0)
					{
						if (parserTable[state][Const.END].type != Action.Type.ERROR)
							return false;
						parserTable[state][Const.END].set(Action.Type.ACCEPT, 0);
					}
					else
					{
						for (Symbol b : head.follows)
						{
							if (parserTable[state][b.id].type == Action.Type.SHIFT)
								System.out.printf("warning SHIFT/REDUCE conflict found\n");
							else if (parserTable[state][b.id].type == Action.Type.REDUCE)
							{
								if (parserTable[state][b.id].value != i.prod.id)
								{
									int r1 = parserTable[state][b.id].value;
									int r2 = i.prod.id;
									System.out.printf("REDUCE/REDUCE confict found (%d, %d)\n", r1, r2);
									return false;
								}
							}
							else
								parserTable[state][b.id].set(Action.Type.REDUCE, i.prod.id);
						}
					}
				}
			}

			// determine the GOTOs for all non-terminals
			for (Symbol s : Symbol.symbols())
				if (s.type == Symbol.Type.NONTERM)
				{
					ItemList g = new ItemList();
					goTo(canon.get(state), s, g);
					if (g.size() != 0)
					{
						int nextState;
						for (nextState = 0; nextState < canon.size(); nextState++)
							if (canon.get(nextState) == g)
								break;
						if (parserTable[state][s.id].type != Action.Type.ERROR)
							return false;
						parserTable[state][s.id].set(Action.Type.GOTO, nextState);
					}
				}
		}
		return true;
	}
	
	//---------------------------------------------
	private void dumpParserTable() {
		boolean translate = false;
		int[] forward = { 0, 3, 4, 5, 1, 2, 7, 8, 6, 10, 11, 9 };
		int[] back    = { 0, 4, 5, 1, 2, 3, 8, 6, 7, 11, 9, 10 };
		String[] actionNames = { "S", "R", "A", "G", "E" };
		for (int s = 0; s < canon.size(); s++)
		{
			int state = s;
			if (translate)
				state = forward[state];
			System.out.printf(":\t");

			for (int sym = 0; sym < Symbol.symbols().size(); sym++)
				if (parserTable[state][sym].type != Action.Type.ERROR)
				{
					Action.Type type  = parserTable[state][sym].type;
					int value = parserTable[state][sym].value;
					if (translate && (type == Action.Type.SHIFT || type == Action.Type.GOTO))
						value = back[value];
					System.out.printf("(%s %s %d)", Symbol.symbols().get(sym).name, actionNames[type.ordinal()], value);
				}
			System.out.print("\n");
		}
	}
	
	//---------------------------------------------
	private void outputParser() {
		
		try {
			// open output file
			String fileName = className + ".java";
			FileWriter file = new FileWriter(fileName);
						
			// read template file in
			String template = new Scanner(getClass().getClassLoader().getResourceAsStream("resources/ParserTemplate.txt")).useDelimiter("\\Z").next();
	
			final int CLASS_NAME = 0;
			final int CONSTRUCTOR = 1;
			final int USER_DEFINITIONS = 2;
			final int NEXT_STATE = 3;
			final int RULE_ACTIONS = 4;
			final int ERROR_ACTION = 5;
			final int TOKEN_IDS = 6;
			final int TOKEN_CNT = 7;
			final int TOKEN_ACTIONS = 8;
			final int TOKEN_MACHINES = 9;
			final int TOKEN_IGNORE = 10;
			final int ACTION_TABLE = 11;
			
			String[] tagNames =
			{
				"<<<class name>>>",
				"<<<constructor>>>",
				"<<<user definitions>>>",
				"<<<next state>>>",
				"<<<rule actions>>>",
				"<<<error action>>>",
				"<<<token ids>>>",
				"<<<token count>>>",
				"<<<token actions>>>",
				"<<<token machines>>>",
				"<<<token ignore>>>",
				"<<<action table>>>"
			};
			
			int[] tagAddress = new int[tagNames.length];
			
			for (int i = 0; i < tagNames.length; i++)
				tagAddress[i] = template.indexOf(tagNames[i]);
			
			// output header
			String s = template.substring(0, tagAddress[CLASS_NAME]);
			file.write(s);
			
			// write class name
			file.write(className);
			s = template.substring(tagAddress[CLASS_NAME]+tagNames[CLASS_NAME].length(), tagAddress[CONSTRUCTOR]);
			file.write(s);
			
			// output constructor
			file.write(className);
			s = template.substring(tagAddress[CONSTRUCTOR]+tagNames[CONSTRUCTOR].length(), tagAddress[USER_DEFINITIONS]);
			file.write(s);
			
			// write user definitions
			file.write(def);
			s = template.substring(tagAddress[USER_DEFINITIONS]+tagNames[USER_DEFINITIONS].length(), tagAddress[NEXT_STATE]);
			file.write(s);
			
			// output next state switch entries
			for (int p = 1; p < productions.size(); p++) {
				s = String.format("\t\t\t\t\tcase %d: argCnt = %d; head = %d; break;\n", p, productions.get(p).tail.size(), productions.get(p).head.id);
				file.write(s);
			}
			s = template.substring(tagAddress[NEXT_STATE]+tagNames[NEXT_STATE].length(), tagAddress[RULE_ACTIONS]);
			file.write(s);
	
			// output rule action switch entries
			for (int p = 1; p < productions.size(); p++) {
				s = String.format("\t\t\t\t\tcase %d: %s break;\n", p, productions.get(p).action);
				file.write(s);
			}
			s = template.substring(tagAddress[RULE_ACTIONS]+tagNames[RULE_ACTIONS].length(), tagAddress[ERROR_ACTION]);
			file.write(s);
			
			// output error action
			s = String.format("\t\t\t\t%s", errorAction);
			file.write(s);
			s = template.substring(tagAddress[ERROR_ACTION]+tagNames[ERROR_ACTION].length(), tagAddress[TOKEN_IDS]);
			file.write(s);
			
			// output token IDS
			int nextTokId = 0;
			for (int tok = 0; tok < tokens.size(); tok++)
				if (! tokens.get(tok).action.equals("{}")) {
					s = String.format("\tfinal int %s = %d;\n", tokens.get(tok).name, nextTokId++);
					file.write(s);				
				}
			
			s = template.substring(tagAddress[TOKEN_IDS]+tagNames[TOKEN_IDS].length(), tagAddress[TOKEN_CNT]);
			file.write(s);
					
			// write out the token machine count
			s = String.format("%d", tokens.size());
			file.write(s);
			s = template.substring(tagAddress[TOKEN_CNT]+tagNames[TOKEN_CNT].length(), tagAddress[TOKEN_ACTIONS]);
			file.write(s);
			
			// output token actions
			for (int tok = 0; tok < tokens.size(); tok++) {
				s = String.format("\t\t\tcase %d: %s%s\n", tok, tokens.get(tok).action, tokens.get(tok).action.equals("{}") ? " break;" : "");
				file.write(s);
			}
			s = template.substring(tagAddress[TOKEN_ACTIONS]+tagNames[TOKEN_ACTIONS].length(), tagAddress[TOKEN_MACHINES]);
			file.write(s);
	
			// output token machines table
			for (int tok = 0; tok < tokens.size(); tok++)
			{
				Token t = tokens.get(tok);
				s = String.format("\tnew TokenMachine( /* %s(%d) */\n", t.name, tok);
				file.write(s);
				file.write("\t\tnew int[][] {\n");
	
				for (int state = 0; state < t.states.size(); state++)
				{
					TokenState ts = t.states.get(state);
					file.write("\t\t\t{");
					for (int sym = 0; sym < 128; sym++)
					{
						if (ts.nextState[sym] == Const.ERR)
							file.write("ERR,");
						else {
							s = String.format("%s,", ts.nextState[sym]);
							file.write(s);
						}
					}
					file.write("},\n");
				}
				file.write("\t\t\t},\n");
	
				file.write("\t\tnew bool[] {");
	
				for (int state = 0; state < t.states.size(); state++)
				{
					TokenState ts = t.states.get(state);
					s = String.format("%s,", ts.accept ? "true" : "false");
					file.write(s);
				}
				file.write("}),\n");
			}
			s = template.substring(tagAddress[TOKEN_MACHINES]+tagNames[TOKEN_MACHINES].length(), tagAddress[TOKEN_IGNORE]);
			file.write(s);
			
			// output token ignore table
			file.write("\tboolean[] mTokIgnore = {");
			for (Token t : tokens)
				file.write(String.format("%s,", t.action.equals("{}") ? "true" : "false"));
			file.write("};\n");			
			s = template.substring(tagAddress[TOKEN_IGNORE]+tagNames[TOKEN_IGNORE].length(), tagAddress[ACTION_TABLE]);
			file.write(s);
			
			// output action table
			String[] actionNames = {"SFT", "RED", "ACC", "GTO", "ERR"};
			for (int state = 0; state < canon.size(); state++)
			{
				file.write("\t\t{");
				for (int sym = 0; sym < Symbol.symbols().size(); sym++)
				{
					Action a = parserTable[state][sym];
					s = String.format("new Act(%s,%d),", actionNames[a.type.ordinal()], a.value);
					file.write(s);
				}
				file.write("},\n");
			}
			s = template.substring(tagAddress[ACTION_TABLE]+tagNames[ACTION_TABLE].length(), template.length());
			file.write(s);
			
			file.close();
		}
		catch (Exception e)
		{
			System.out.printf("ERROR: %s", e.getMessage());
		}
	}
}
