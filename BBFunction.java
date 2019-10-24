//package com.konradsobczak.bbeat;

/**
  * BBFunction - Functions in BareBones
  *
  * @author Konrad Sobczak
  */

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;

class BBFunction {
    private final ArrayList<String> parameters;
    private final int start;
    private final int end;
    private final BBFunctionInterpreter interpreter;
    private String source = "";
    private String name;

    public BBFunction(ArrayList<String> parameters, int start, int end, BBParser parser, String name){
        this.parameters = parameters; 
        this.start = start;
        this.end = end;
        this.name = name;

        for (int i = start; i < end; i++){
            String statementText = parser.getStatement(i).getText();
            this.source += statementText + ";\r\n";
        }

        this.interpreter = new BBFunctionInterpreter(this.source, new String[] {});
    }

    public String execute(ArrayList<String> arguments, HashMap<String, BBVariable> globals){
        if(arguments.size() != parameters.size()){
            System.out.print("Error: function " + this.name + " expects " + parameters.size() + " arguments, but got " + arguments.size() + ".");
            System.exit(1);
            return null;
        } else {
            HashMap<String, BBVariable> parsedArguments = new HashMap<String, BBVariable>();
            for(int i = 0; i < arguments.size(); i++){
                BBVariable value = null;
                if(globals.get(arguments.get(i)) != null){
                    value = globals.get(arguments.get(i));
                } else {
                    value = new BBVariable(arguments.get(i));
                }
                parsedArguments.put(parameters.get(i), value);
            }
            this.interpreter.setContext(globals, parsedArguments);
            this.interpreter.executeSource();
            return this.interpreter.returnValue;
        }
    }
}