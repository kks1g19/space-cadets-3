//package com.konradsobczak.bbeat;

/**
  * BBIfCondition - conditions in BareBones
  *
  * @author Konrad Sobczak
  */

public class BBIfCondition {
    private final BBVariable conditionVariable;
    private final BBVariable conditionConstant;
    private final int branchTo;
    private final boolean type;

    /**
      * Create a new BBIfCondition
      *
      * @param conditionVariable variable to be considered in the condition
      * @param conditionConstant condition target value
      * @param branchTo Where to jump when condition is finished
      */
    public BBIfCondition(BBVariable conditionVariable, BBVariable conditionConstant, int branchTo, String type){
        this.conditionVariable = conditionVariable;
        this.conditionConstant = conditionConstant;
        this.branchTo = branchTo;
        this.type = type.equals("not");
    }

    /**
      * Check if block should execute
      *
      * @return True when the condition condition is met 
      */
    public boolean finished(){
        if(this.type){
            return this.conditionVariable.getValue() == this.conditionConstant.getValue();
        } else {
            return this.conditionVariable.getValue() != this.conditionConstant.getValue();
        }
    }

    /**
      * Return target to jump
      *
      * @return int number of line to jump to 
      */
    public int getBranch(){
        return this.branchTo;
    }    
}