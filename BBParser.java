//package com.konradsobczak.bbeat;

/**
  * BBParser - parses BareBones statements to more useful form
  *
  * @author Konrad Sobczak
  */

import java.util.ArrayList;
import java.util.Arrays;

public class BBParser {
    private final String source;
    private final ArrayList<BBStatement> statements;
    private int currentStatement = 0;

    /**
      *
      * @param source BareBones source to be parsed
      */
    public BBParser(String source){
        this.source = source;
        this.statements = new ArrayList<BBStatement>();
        for(String statement : source.split(";")){
            if(statement.length() > 0 && statement.charAt(0) != '#' && !statement.matches("(^\n$)|(^\r$)|(^\r\n$)")){
                ArrayList<String> words = new ArrayList<String>(Arrays.asList(statement.split(" ")));
                words.removeIf(a -> a.equals(""));
                for(int i = 0; i < words.size(); i++){
                    String word = words.get(i);
                    if(word.charAt(0) == '"' && word.charAt(word.length() - 1) != '"'){
                        String nextWord = word;
                        int j = 1;
                        ArrayList<String> toRemove = new ArrayList<String>();
                        while(nextWord.charAt(nextWord.length() - 1) != '"'){
                            nextWord += " " + words.get(i + j);
                            toRemove.add(words.get(i + j));
                            j++;
                        }
                        words.removeIf(a -> toRemove.contains(a));
                        words.set(i, nextWord);
                    }
                }
                this.statements.add(new BBStatement(words));
            }
        }
    }

    /**
      * Get next statement
      *
      * @return next BBStatement or null if doesn't exist
      */
    public BBStatement nextStatement(){
        try {
            BBStatement statement = this.statements.get(currentStatement);
            currentStatement++;
            return statement;
        } catch (Exception e){
            return null;
        }
    }

    /**
      * Get statement with address
      * @param address address of the statement
      *
      * @return BBStatement or null if doesn't exist
      */
    public BBStatement getStatement(int address){
        try {
            BBStatement statement = this.statements.get(address);
            return statement;
        } catch (Exception e){
            return null;
        }
    }

    /**
      * Create a branch
      *
      * @param address Address to jump to
      */
    public void branch(int address){
        this.currentStatement = address;
    }

    /**
      * Get current statement number
      *
      * @return int Statement number
      */
    public int currentAddress(){
        return this.currentStatement;
    }

    /**
      * Get all statements
      *
      * @return ArrayList of all statements
      */
    public ArrayList<BBStatement> getStatements(){
        return this.statements;
    }

    /**
      * Reset the addres to 0
      */
    public void reset(){
        currentStatement = 0;
    }
}