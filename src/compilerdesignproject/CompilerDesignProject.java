/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilerdesignproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
/**
 *
 * @author Heinrich
 */
public class CompilerDesignProject {
    
    
    public static Stack stack = new Stack();
    
    public static class CharLineNumber {
        public char character;
        public int lineNumber;
        
        public CharLineNumber(char x, int y){
            this.character = x;
            this.lineNumber = y;
        }
    }
    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        //srcCode is an array of string where each index represent one line in the source code string
        ArrayList<String> srcCode = readTxtFile("C:\\Users\\davebaclayon\\Documents\\Dave\\CS148 Compiler Design\\CompilerDesignProject\\syntaxDraft.txt");
        
        
        for(int lineNumber = 0; lineNumber<srcCode.size(); lineNumber++){//For each line
            String curSrcCodeLine = srcCode.get(lineNumber);
            String curWord = "";
            for(int charNdx=0; charNdx<curSrcCodeLine.length(); charNdx++) {//Iterate through the line
                char curChar = curSrcCodeLine.charAt(charNdx);
                
                if(isWordTerminator(curChar)) //Check if current character is a terminator
                {                    
                    if("if".equals(curWord) 
                        || "while".equals(curWord) 
                        || "foreach".equals(curWord)){ //Check if current word is loop/condition keyword
                        
                        //Find for '(' disregarding whitespace
                        //Terminates program if '(' is not next
                        checkIfOpenParenthesisIsNext(lineNumber, charNdx, curSrcCodeLine, curWord);
                    }
                    
                    
                    /*
                    *   Comparison if the terminator is opening or closing
                    */
                    //TODO: Insert other checking modules here
                    if(curChar=='(' || curChar == '{'){
                        CharLineNumber cln = new CharLineNumber(curChar, lineNumber+1);
                        stack.push(cln);
                    } else if (curChar == '}'){
                        validatePoppedChar(curChar, lineNumber);
                    } else if (curChar == ')'){
                        validatePoppedChar(curChar,lineNumber);
                    }
                    
                    
                    /*
                    *   This is the end of Dave's code
                    */
                     
                    //Reset curWord
                    curWord = "";
                }else{ //Continue forming the current word
                    curWord += curChar;
                }
            }
        }
        
        // checks if the stack is not empty then a closing operator is not matching anything
        if(!stack.empty()){
            CharLineNumber cln = (CharLineNumber) stack.pop();
            System.out.println("Line number "+ cln.lineNumber+": No pair found for "+ cln.character);
        }
    }
    
    
    //Checks if the opens and closes are pairs
    public static void validatePoppedChar(char curChar, int lineNumber){
        
        CharLineNumber cln;
        if (!stack.empty()){
            cln =  (CharLineNumber) stack.pop();
            if(cln.character == '{' && curChar == '}'){
              return;
            } else if (cln.character  == '(' && curChar == ')'){
              return;
            }
        }
        String errorMsg = "Line number "+(lineNumber+1)+": no pair found for " + curChar;
        System.out.println(errorMsg);
        System.exit(0);
        
    }
    
    
    
    
    
    
    
    /**
     * Find for '(' disregarding whitespace
     * Terminates program if '(' is not next
     * @param lineNumber - current source code line number; used for printing the error
     * @param charNdx - the current char ndx for the source code line string
     * @param curSrcCodeLine  - the source code line string to be checked
     */
    private static void checkIfOpenParenthesisIsNext (int lineNumber, int charNdx, String curSrcCodeLine, String keyword){
        //Keep traversing till you reach the end of the line or you encounter a non space character
        int c = charNdx;
        for (; c<curSrcCodeLine.length() && curSrcCodeLine.charAt(c)==' '; c++){}
        //Check if you did not exceed the line and
        //if the last character is an opening parenthesis
        if(c<curSrcCodeLine.length() && curSrcCodeLine.charAt(c)=='('){
            //Do nothing
        }else{
            //Set the error message
            String errorMsg = "Line number "+(lineNumber+1)+": \""+keyword+"\" should be followed by '('";
            System.out.println(errorMsg);
            //Terminate program
            System.exit(0);
        }
    }
    
    /**
     * This function checks if the char parameter is a terminator
     * @param c
     * @return True->terminator; False->not termminator
     */
    private static boolean isWordTerminator (char c){
        boolean bool = false;
        switch(c){
            case ' ':;
            case '=':;
            case '(':;
            case ')':;
            case '{':;
            case '}': bool=true;
        }
        return bool;
    }
    
    /**
     * This function returns a String Arraylist where each index is one line of code
     * @param path - The path of text file to be read.
     * @return ArrayList<String>
     */
    private static ArrayList<String> readTxtFile(String path){
        ArrayList<String> ret = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                ret.add(sCurrentLine);
            }   

        } catch (IOException e) {
            System.out.println(e.toString());
        }
        
        return ret;
    }
}
