//package com.konradsobczak.bbeat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
  * FileReader - reads file and puts it in a single string
  *
  * @author Konrad Sobczak
  */
public class FileReader {
    /**
      * Read the file at provided path
      *
      * @param path Location of the file
      * @return The contents of the file
      * @throws FileNotFoundException
      */
    public static String read(String path) throws FileNotFoundException {
        String content = "";
        Scanner scanner = new Scanner(new FileInputStream(path));
        while(scanner.hasNext()){
            content += scanner.nextLine();
        }
        return content;
    }
}