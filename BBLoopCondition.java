//package com.konradsobczak.bbeat;

/**
  * BBLoopCondition - Loops in BareBones
  *
  * @author Konrad Sobczak
  */

public class BBLoopCondition {
    private final BBVariable loopVariable;
    private final BBVariable loopConstant;
    private final int branchTo;
    private final boolean type;

    /**
      * Create a new BBLoopCondition
      *
      * @param loopVariable variable to be considered in the loop
      * @param loopConstant loop target value
      * @param branchTo Where to jump when loop is finished
      */
    public BBLoopCondition(BBVariable loopVariable, BBVariable loopConstant, int branchTo, String type){
        this.loopVariable = loopVariable;
        this.loopConstant = loopConstant;
        this.branchTo = branchTo;
        this.type = type.equals("not");
    }

    /**
      * Check if loop has finished
      *
      * @return True when the loop condition is met 
      */
    public boolean finished(){
        if(this.type){
            return this.loopVariable.getValue() == this.loopConstant.getValue();
        } else {
            return this.loopVariable.getValue() != this.loopConstant.getValue();
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