//package com.konradsobczak.bbeat;

import java.io.FileNotFoundException;

/**
  * BBEAT - BareBones (Extended) Awful Interpreter
  *
  * @author Konrad Sobczak
  */

public class BBeat {
    /**
      * @param args predefined variables, options and filepath for source
      */

    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("Error: No source file given!");
            System.exit(1);
        }
        String source = "";
        try {
            source = FileReader.read(args[args.length - 1]);
        } catch (FileNotFoundException e){
            System.out.println("Error: Source file not found");
            System.exit(1);
        }
        BBInterpreter interpreter = new BBInterpreter(source, args);
        boolean success = interpreter.executeSource();
        if(success){
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
