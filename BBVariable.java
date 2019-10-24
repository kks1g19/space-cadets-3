//package com.konradsobczak.bbeat;

/**
  * BBVariable - Variables in BareBones
  *
  * @author Konrad Sobczak
  */


@SuppressWarnings("unchecked")

public class BBVariable {
    private Integer integerValue;
    private String stringValue;
    private Double doubleValue;
    private Boolean booleanValue;
    private String type;


    /**
      * Create new BBVariable
      *
      * @param value Value to be set
      */
    public BBVariable(String value){
        this.setValue(value);
    }

    /**
      * Set variable value 
      * @param value Value to be set
      */
    public void setValue(String value){
        value = value.replace("\"", "");
        this.integerValue = null;
        this.doubleValue = null;
        this.stringValue = null;
        this.booleanValue = null;
        try {
            this.integerValue = Integer.parseInt(value);
            this.type = "int";
            if(this.integerValue < 0){
              throw new Exception("Invalid value");
            }
        } catch (Exception e){
            try {
                this.doubleValue = Double.parseDouble(value);
                if(this.doubleValue == Math.floor(this.doubleValue)){
                    this.integerValue = (int) Math.round(this.doubleValue);
                    this.doubleValue = null;
                    this.type = "int";
                } else {
                    this.type = "double";
                }
            } catch (Exception e2){
                if(value.equals("true") || value.equals("false")){
                    this.booleanValue = value.equals("true") ? true : false;
                    this.type = "bool";
                } else {
                    this.stringValue = value;
                    this.type = "str";
                }
            }
        }
    }


    /**
      * Get the value of the variable
      *
      * @return int|double|String value
      */
    public <T> T getValue(){
        if(integerValue != null){
            return (T) integerValue;
        } else if(doubleValue != null){
            return (T) doubleValue;
        } else if(booleanValue != null){
            return (T) booleanValue;
        } else {
            return (T) stringValue;
        }
    }

    /**
      * Get type of the variable
      *
      * @return String type
      */
    public String getType(){
        return this.type;
    }

    /**
      * increment this variable
      */
    public boolean increment(){
        if(type.equals("int")){
            this.integerValue++;
        } else if(type.equals("double")){
            this.doubleValue++;
        } else {
            return false;
        }
        return true;
    }

    /**
      * decrement this variable
      */
    public boolean decrement(){
        if(type.equals("int")){
            this.integerValue--;
        } else if(type.equals("double")){
            this.doubleValue--;
        } else {
            return false;
        }
        return true;
    }

    /**
      * clear this variable
      */
    public boolean clear(){
        if(type.equals("int")){
            this.integerValue = 0;
        } else if(type.equals("double")){
            this.doubleValue = 0.0;
        } else {
            return false;
        }
        return true;
    }

    public String toString(){
        return "\033[3m" + this.getType() + "\033[0m: " + this.getValue();
    }
}