/*
 * Bug:
 * 1. isExpecting could be turned to a class so the line number error can be properly indicated.
      Wierd line number usually happen with new line and many spaces.
 */
package compilerdesignproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author 
 */
public class LoopAndCondSyntax {
    
    /**
     * This stack will be used to keep track of if else and terminator pairs
     */
    public static class MyStack extends Stack{
        @Override
        public StackElement pop(){
            // ~ is a sentinel value, cant be "" cause accessing charAt(0) will cause error
            return super.isEmpty() ? new StackElement("~", -1): (StackElement)super.pop();
        }
    }
    
    public static MyStack terminatorStack = new MyStack();
    public static MyStack ifElseStack = new MyStack();
    
    public static class StackElement{
        public String value;
        public String valueType;
        public int lineNumber;
        
        public StackElement (String string, int lineNumber){
            this.value = string;
            this.valueType = "~"; //Sentinel
            this.lineNumber = lineNumber;
        }
        
        public StackElement (Character character, String valueType, int lineNumber){
            this.value = character.toString();
            this.valueType = valueType;
            this.lineNumber = lineNumber;
        }
    }
    
    public static void main (String[] args){ 
        //srcCode is an array of string where each index represent one line in the source code string
        ArrayList<String> srcCode = readTxtFile("HeinirchTesting.txt");
        Character isExpecting = null;
        Boolean isElseIfPossible = false;
        
        String curWord = "";
        for(int lineNumber = 0; lineNumber<srcCode.size(); lineNumber++){//For each line
            String curSrcCodeLineTxt = srcCode.get(lineNumber);
            for(int charNdx=0; charNdx<curSrcCodeLineTxt.length(); charNdx++) {//Iterate through the line
                char curChar = curSrcCodeLineTxt.charAt(charNdx);
                
//                System.out.println("--------------------");
//                System.out.println("Line Number: "+(lineNumber+1));
//                System.out.println("curWord: "+curWord);
//                System.out.println("curChar: "+curChar);
//                System.out.println("isExpecting: "+isExpecting);
//                System.out.println("--------------------");
                
                
                if(charNdx==0 || isWordTerminator(curChar) || isExpecting!=null){ //Check if current character is a terminator, and do not append to curWord if still expecting something
                    
                    if(isElseIfPossible == true && !curWord.equals("else") && !curWord.equals("elseif") && !curWord.equals("")){
                        isElseIfPossible = false;
                    }
                    
                    if("if".equals(curWord) 
                        || "while".equals(curWord) 
                        || "foreach".equals(curWord)){
                            isExpecting = '(';
                            if("if".equals(curWord)){
                                ifElseStack.push(new StackElement("if", lineNumber));
                                isElseIfPossible = true;
                            }
                    }else if ("else".equals(curWord) || "elseif".equals(curWord)){
                        
                        if(isElseIfPossible == false){
                            String errorMsg = "Line number "+(lineNumber+1)+": Unexpected \""+curWord+"\"";
                            System.out.println(errorMsg);
                            //Terminate program
                            System.exit(0);
                        }
                        
                        StackElement se = ifElseStack.pop();
                        if(!se.value.equals("if") && !se.value.equals("elseif")){
                            String errorMsg = "Line number "+(lineNumber+(charNdx==0?0:1))+": Unexpected \"else\"";
                            System.out.println(errorMsg);
                            //Terminate program
                            System.exit(0);
                        }
                        
                        if("elseif".equals(curWord)){
                            ifElseStack.push(new StackElement("elseif", lineNumber));
                            isExpecting = '(';
                            isElseIfPossible = true;
                        }else{
                            isExpecting = '{';
                        }
                    }
                    
                    if(isExpecting == null){
                        if(curChar == '{'){
                            String errorMsg = "Line number "+(lineNumber+1)+": Unexpected '{'";
                            System.out.println(errorMsg);
                            //Terminate program
                            System.exit(0);
                        }
                        curWord = "";
                    }else{
                        //Expecting something
                        if(curChar == ' '){
                            continue;
                        }else if(isExpecting == '('){
                            if(curChar == '('){
                                terminatorStack.push(new StackElement('(', curWord, lineNumber));
                                isExpecting = null; 
                                curWord = "";
                            }else{
                                String errorMsg = "Line number "+(lineNumber+1)+": \""+curWord+"\" should be followed by '('";
                                System.out.println(errorMsg);
                                //Terminate program
                                System.exit(0);
                            }
                        }else  if (isExpecting == '{'){
                            if(curChar == '{'){
                                terminatorStack.push(new StackElement('{', curWord, lineNumber));
                                isExpecting = null; 
                                curWord = "";
                            }else{
                                String errorMsg = "Line number "+(lineNumber+(charNdx==0?0:1))+": Expecting '{' but not found.";
                                System.out.println(errorMsg);
                                //Terminate program
                                System.exit(0);
                            }
                        }
                    }
                    
                    //Closing terminators
                    if(curChar == '}'){
                        StackElement se = terminatorStack.pop();
                        if(se.value.charAt(0)!='{'){
                            String errorMsg = "Line number "+(lineNumber+1)+": Unexpected '}'";
                            System.out.println(errorMsg);
                            //Terminate program
                            System.exit(0);
                        }
                    }else if (curChar == ')'){
                        StackElement se = terminatorStack.pop();
                        if(se.value.charAt(0)!='('){
                            System.out.println(se.value);
                            String errorMsg = "Line number "+(lineNumber+1)+": Unexpected ')'";
                            System.out.println(errorMsg);
                            //Terminate program
                            System.exit(0);
                        }else{
                            if(se.valueType.equals("if")
                                || se.valueType.equals("while")
                                || se.valueType.equals("for")
                                || se.valueType.equals("elseif")){
                                    isExpecting = '{';
                            }
                        }
                    }
                    
                    if(charNdx==0 && !isWordTerminator(curChar)){ //Already a new line so curWord is reset
                        curWord = "";
                        curWord += curChar;
                    }
                }else{ //Continue forming the current word
                    curWord += curChar;
                }
            }
        }//End of main loop
        
        if(!terminatorStack.isEmpty()){
            StackElement se = terminatorStack.pop();
            String errorMsg = "Line number "+(se.lineNumber+1)+": No pair found for \""+se.value+"\"";
            System.out.println(errorMsg);
            //Terminate program
            System.exit(0);
        }
        
        if(!curWord.isEmpty()){
            System.out.println("Incomplete code. Last word is: "+curWord);
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
            case ';':;
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
