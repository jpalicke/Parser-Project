/*******************************************************************************
*
*   Joseph Palicke 
*   Craig Popovich
*
*   CS 31600 Project
*   front.java, a simple recursive descent parser.
*   
*   Class: front
*   Methods: lookup(), addChar(), getChar(), getNonBlank(), lex(), expr(),
*            term(), factor(), error()
*
*   Description:  Reads in from file in_fp.txt in the working director
*                 until it reads the EOF character, semicolon (;).  
*                  
*                 Parses the following rules:
*                 
*                 <expr> -> <term> {(+ | -) <term>}
*                 <term> -> <factor> {(* | /) <factor>}
*                 <factor> -> id | int_constant | (<expr>)
*
*******************************************************************************/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.*;

public class front {
	
	public static final String FILENAME = "in_fp.txt";
	
	private static int charClass;
	private static String lexeme = new String();
	private static char nextChar;
	private static int nextToken;
	private static int lexLen;
	private static int tempChar;
   
	private static BufferedReader br = null;
	private static InputStream is = null;
	private static InputStreamReader isr = null;
	
	/*Java enums work different than C, so I'm just going to define these codes as
	constants*/
	
	public static final int LETTER = 0;
	public static final int DIGIT = 1;
	public static final int UNKNOWN = 99;
   
	public static final int INT_LIT = 10;
	public static final int IDENT = 11;
	
	public static final int ASSIGN_OP = 20;
	public static final int ADD_OP = 21;
	public static final int SUB_OP = 22;
	public static final int MULT_OP = 23;
	public static final int DIV_OP = 24;
	public static final int LEFT_PAREN = 25;
	public static final int RIGHT_PAREN = 26;
   
	public static final int EOF = -1;
	
	/*Constants for the error function*/
	
	public static final int GETCHAR = 1;
	public static final int GETNONBLANK = 2;
	public static final int TERM_1 = 3;
	public static final int TERM_2 = 4;
	public static final int MAIN = 5;
	
	/*****************************************
		lookup(char examineMe)
		takes in char examineMe, and matches it
		to one of the operator or EOF cases.
		Method returns nextToken, an integer
		value which stands for one of the
		possible symbols this lexer recognizes.
		
		One deviation from the book; semicolon
		is recognized as EOF
	******************************************/
	public static int lookup(char examineMe) {
      
		switch(examineMe) {
      
		//using token codes from the book
      
			case '(': 
				addChar();
				nextToken = LEFT_PAREN;
				break;
			case ')':
				addChar();
				nextToken = RIGHT_PAREN;
				break;
			case '=':
				addChar();
				nextToken = ASSIGN_OP;
				break;
			case '+':
				addChar();
				nextToken = ADD_OP;
				break;
			case '-':
				addChar();
				nextToken = SUB_OP;
				break;
			case '*':
				addChar();
				nextToken = MULT_OP;
				break;
			case '/':
				addChar();
				nextToken = DIV_OP;
				break;
         case ';':
            addChar();
            nextToken = EOF;
			default:
				addChar();
				nextToken = EOF;
				break;
      } // end switch      
      return nextToken;
   } // end lookup
   
	/*****************************************
		addChar()
		takes no input. It checks the length of
		the current lexeme, if zero, it just adds
		the current character to the lexeme String
		If greater than zero, it just concatenates
		nextChar to the current string.
		
		Method is void, so it doesn't return a
		value.
	******************************************/
   
   public static void addChar() {
	   
	   if (lexLen == 0) {
         lexeme = Character.toString(nextChar);
      } else {
         lexeme = lexeme + Character.toString(nextChar);
      }
      
      lexLen++;
	   
   } // end addChar
   
	/*****************************************
		getChar()
		Method takes in no input, and being void
		it doesn't return anything.
		
		Method uses BufferedReader.read() to read
		in 1 character at a time.  If it doesn't
		see EOF, it checks nextChar to see if it
		is a letter, digit, or semicolon (which was
		chosen for an EOF symbol).  If none of those
		it sets charClass to unknown.
		
		Note the cast; read() returns
		an integer of the unicode value for the
		character that was read in, therefore the
		cast to char is needed.
		
		The exception try/catch is needed by the
		compiler.  If it catches the exception,
		an error message is printed.
	******************************************/
   
   public static void getChar() {
	   
      try {
         if ((nextChar = (char)br.read()) != EOF) {
            
            if (Character.isLetter(nextChar)) {
               charClass = LETTER;
		      } else if (Character.isDigit(nextChar)) {
               charClass = DIGIT;
		      } else if (Character.compare(';',nextChar) == 0) {
			      charClass = EOF;
		      } else {
               charClass = UNKNOWN;
            }
	      } else {
		      charClass = EOF;
	      }
      } catch (Exception e) {
         error(GETCHAR);
      }
	   
   } // end getChar
   
	/*****************************************
		getNonBlank()
		Method takes in no input, and being void
		it doesn't return anything.
		
		Method checks if the character read in
		is whitespace or not.  If so, call
		getChar to get the next character.  That
		way, the method is able to make sure nextChar
		is set to the next non-whitespace character.
		
		The exception try/catch is needed by the
		compiler.  If it catches the exception,
		an error message is printed.
	******************************************/
   
   public static void getNonBlank() {
	   
      try {
	      while (Character.isWhitespace(nextChar)) {
		      getChar();
	      }
      } catch (Exception e) {
         error(GETNONBLANK);
      }
	   
   } // end getNonBlank
   
   	/*****************************************
		lex()
		Method takes in no input, and returns nextToken
		
		Method sets lexLen to zero, as if lex() is
		called, the calling method is looking for
		the next lexeme.  It then calls getNonBlank
		to get the next non-whitespace character.
		
		The switch statement implements a statement
		machine; if the first character is a letter
		it continues taking in letters and numbers
		and sets nextToken to IDENT.
		
		If the first character is a digit, the lexeme
		will be an INT_LIT, so continue reading in
		digits and set nextToken to INT_LIT.
		
		The other two states are for EOF and UNKNOWN,
		the latter being operators, the former being ;
	******************************************/
   
   public static int lex() {
	   
      lexLen = 0;
	  getNonBlank();
	   
	   switch (charClass) {
		   
		   case LETTER:
				addChar();
				getChar();
				while (charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = IDENT;
				break;
				
		   case DIGIT:
				addChar();
				getChar();
				while (charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = INT_LIT;
				break;
				
		   case UNKNOWN:
				lookup(nextChar);
				getChar();
				break;
				
		   case EOF:
				nextToken = EOF;
				lexeme = "EOF";
				break;
	   }
	   
      System.out.println("Next token is: " + nextToken + ", Next lexeme is: " + lexeme);
      return nextToken;
      
   } // end lex
   
    /*****************************************
		expr()
		Method takes in no input, and returns nothing
		as it is type void.
		
		Parses strings in the language generated 
		by the rule: <expr> -> <term> {(+ | -) <term>}
	******************************************/
 
   public static void expr(){
		
		System.out.println("Enter <expr>");

    /* Parse the first term */
		
		term();

    /* As long as the next token is + or -, get
    the next token and parse the next term */
		
		while (nextToken == ADD_OP || nextToken == SUB_OP) {
			
			lex();
			
			term();
			
		}
		
		System.out.println("Exit <expr>");
   
   } /* End of function expr */

   	/*****************************************
		term()
		Method takes in no input, and returns nothing
		as it is type void.
		
		Parses strings in the language generated 
		by the rule: <term> -> <factor> {(* | /) <factor>)
	******************************************/

   public static void term() {
		
		System.out.println("Enter <term>");
      
		/* Parse the first factor */
		
		factor();
		
		/* As long as the next token is * or /, get the
		next token and parse the next factor */
		
		while (nextToken == MULT_OP || nextToken == DIV_OP) {
			
			lex();
			
            factor();
			
      }
	  
      System.out.println("Exit <term>");
	  
   } /* End of function term */

   	/*****************************************
		factor()
		Method takes in no input, and returns nothing
		as it is type void.
		
		Parses strings in the language generated 
		by the rule: <factor> -> id | int_constant | ( <expr )
	******************************************/

   public static void factor() {
       System.out.println("Enter <factor>");
       /* Determine which RHS */
       if (nextToken == IDENT || nextToken == INT_LIT) {
           lex(); /* Get the next token */
       } else {
           /* If the RHS is (<expr>), call lex to pass over the 
           left parenthesis, call expr, and check for the right 
           parenthesis */
           if (nextToken == LEFT_PAREN) {
               lex(); 
               expr();
   
               if (nextToken == RIGHT_PAREN) {
                   lex(); 
               } else { 
                   error(TERM_1);
               }
           } /* End of if (nextToken == ... */
           /* It was not an id, an integer literal, or a left parenthesis */
           else 
           { 
               error(TERM_2); 
           }
       } /* End of else */
       System.out.println("Exit <factor>");;
   } /* End of function factor */

	/*****************************************
		error(int errorCode)
		Error method is void, so it doesn't return
		anything.  Method takes in an integer.  
      Method outputs simple error
      messages depending on where in the program
      error() was called from.
	******************************************/

   public static void error(int errorCode){
       
		switch (errorCode) {
		   
			case GETCHAR:
				System.out.println("Exception thrown in getChar(), exiting immediately.");
				break;
			case GETNONBLANK:
				System.out.println("Exception thrown in getNonBlank(), exiting immediately.");
				break;
			case TERM_1:
			case TERM_2:
				System.out.println("Error in <Term> rules, exiting immediately.");
				break;
			case MAIN:
				System.out.println("Exception thrown in main(), exiting immediately.");
				break;
	   }
      System.exit(0);
	   
   }
   
	/*****************************************
		main()
		Main function is void, so it doesn't return
		anything.  Function takes in input from
		file in_fp.txt
      
      Function creates new Stream readers to read
      input file, then calls lex() and expr()
      until EOF.
	******************************************/
   
   public static void main(String[] args) {
		
		// instantiate input stream
		
      try {
      
		   is = new FileInputStream(FILENAME);
         
      } catch (Exception e) {
         error(MAIN);
      }
		
		// instantiate input stream reader
		
		isr = new InputStreamReader(is);
		
		// instantiate buffered reader
		
		br = new BufferedReader(isr);

		char curChar;

		getChar();
		
		do {
			lex();
			expr();
		} while (nextToken != EOF);

   } // end main

} // end front
