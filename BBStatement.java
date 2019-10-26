//package com.konradsobczak.bbeat;

import java.util.ArrayList;

/**
  * BBStatement - parsed BareBones statement
  *
  * @author Konrad Sobczak
  */
public class BBStatement{
    private final String operation;
    private final ArrayList<String> arguments;
    private final String text;

    /**
      * Create new statement
      *
      * @param words Words to be parsed into statement
      */
    public BBStatement(ArrayList<String> words){
        this.text = String.join(" ", words);
        this.operation = words.get(0).trim();
        words.remove(0);
        for(String word : words){
            word = word.trim();
        }
        this.arguments = words;
    }

    /**
      * Gets the arguments
      *
      * @return ArrayList<String> of arguments
      */
    public ArrayList<String> getArguments(){
        return this.arguments;
    }

    /**
      * Gets the operation
      *
      * @return String operation name
      */
    public String getOperation(){
        return this.operation;
    }

    /**
      * Get the statement text
      * 
      * @return String statement text
      */
    public String getText(){
        return this.text;
    }

    public String toString(){
        return String.join(" ", this.operation, String.join(" ", this.arguments));
    }
}