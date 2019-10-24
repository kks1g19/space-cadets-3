//package com.konradsobczak.bbeat;

/**
  * BBFunctionInterpreter - executes BareBones function statements
  *
  * @author Konrad Sobczak
  */

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;

class BBFunctionInterpreter {
    private HashMap<String, BBVariable> variables = new HashMap<String, BBVariable>();
    private HashMap<String, BBFunction> functions = new HashMap<String, BBFunction>();
    private ArrayDeque<BBLoopCondition> loops = new ArrayDeque<BBLoopCondition>();
    private final BBParser parser;
    private boolean verbose;
    private MathEval math = new MathEval();
    public String returnValue = null;

    private final List invalidNames = Arrays.asList(new String[]{"clear", "copy", "decr", "do", "end", "incr", "init", "not", "to", "while", 
        "print", "+", "-", "*", "/", "^", "%", "^", "×", "·", "±", "E", "Euler", "LN2", "LN10", "LOG2E", "LOG10E", "PHI", "PI", "abs", "acos", 
        "asin", "atan", "cbrt", "ceil", "cos", "cosh", "exp", "expm1", "floor", "log", "log10", "log1p", "max", "min", "random", "round", "roundHE", 
        "signum", "sin", "sinh", "sqrt", "tan", "tanh", "toDegrees", "toRadians", "ulp"});

    /**
      * Create a new BB Interpreter with given source code
      *
      * @param source BareBones source code
      * @param args Argumenst given to main function
      */
    public BBFunctionInterpreter(String source, String[] args){
        this.parser = new BBParser(source);

        if(Arrays.asList(args).contains("-v")){
            this.verbose = true;
        }
        for(String arg : args){
            if(arg.charAt(0) != '-' && arg != args[args.length -1]){
                String name = arg.substring(0, arg.lastIndexOf("=")).trim();
                String value = arg.substring(arg.lastIndexOf("=") + 1, arg.length()).trim();
                setVariable(name, new BBVariable(value));
            }
        }
        if(this.verbose){
            System.out.println("Initialised variables: " + variables);
        }
    }

    /**
      * Execute entire program
      * @return true if all statements executed correctly
      */
    public boolean executeSource(){
        boolean success = true;
        BBStatement statement;
        while((statement = this.parser.nextStatement()) != null) {
            success = success ? this.execute(statement) : false;
            if(verbose && !statement.getOperation().equals("while") && !statement.getOperation().equals("end")){
                System.out.println(variables);
            }
        }
        if(verbose){
            System.out.println("Finished with variables: " + variables);
        }
        return success;
    }

    /**
      * Execute a single statement
      * @param statement BBStatement to be executed
      * @return True if statement executed correctly
      */
    private boolean execute(BBStatement statement){
        boolean success = false;
        switch (statement.getOperation()){
            case "print":
                success = print(statement.getArguments());
                break;
            case "incr":
                success = incr(statement.getArguments());
                break;
            case "decr":
                success = decr(statement.getArguments());
                break;
            case "copy":
                success = copy(statement.getArguments());
                break;
            case "set":
                success = set(statement.getArguments());
                break;
            case "clear":
                success = clear(statement.getArguments());
                break;
            case "init":
                success = init(statement.getArguments());
                break;
            case "while":
                success = startLoop(statement.getArguments());
                break;
            case "return":
                success = returnValue(statement.getArguments());
                break;
            case "end":
                success = end();
                break;
            default:
                if(this.functions.get(statement.getOperation()) != null){
                    this.functions.get(statement.getOperation()).execute(statement.getArguments(), this.variables);
                    success = true;
                } else {
                    System.out.println("Error: no such operation " + statement.getOperation());
                    System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
                    System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
                    System.exit(1);
                }
        }
        return success;
    }

    /**
      * Execute a function
      *
      * @param String statement to be executed
      * @return BBVariable value returned by the statement
      */
    private String execute(String statement){
        if(statement.matches("(^true$)|(^false$)|(^\".*\"$)")){
            return statement;
        } else {
            ArrayList<String> words = new ArrayList<String>(Arrays.asList(statement.split(" ")));
            words.removeIf(a -> a.equals(""));
            BBStatement parsedStatement = new BBStatement(words);
            switch(parsedStatement.getOperation()){
                case "eval":
                    return evaluate(parsedStatement.getArguments());
                case "return":
                    return this.returnValue;
                default:
                    if(getVariable(parsedStatement.getOperation()) != null){
                        return "" + getVariable(parsedStatement.getOperation()).getValue();
                    } else if(this.functions.get(parsedStatement.getOperation()) != null){
                        return this.functions.get(parsedStatement.getOperation()).execute(parsedStatement.getArguments(), this.variables);
                    } else if(parsedStatement.getOperation().matches("(^[0-9]*\\.?[0-9]?)|(^\"([a-z]|[A-Z])*\"$)|(^'([a-z]|[A-Z])*'$)")) {
                        return "" + new BBVariable(parsedStatement.getOperation()).getValue();
                    } else {
                        System.out.println("Error: Invalid statement");
                        System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
                        System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
                        System.exit(1);
                    }
                    return null;
            }
        }
    }

    /**
      * Evaluate maths expression
      *
      * @param args expression
      * @return string value of expression
      */
    private String evaluate(ArrayList<String> args){
        for(int i = 0; i < args.size(); i++){
            String arg = args.get(i);
            if(!Arrays.asList("+", "-", "*", "/", "^", "%", "^", "×", "·", "±", "E", "Euler", "LN2", "LN10", "LOG2E", 
                "LOG10E", "PHI", "PI", "abs", "acos", "asin", "atan", "cbrt", "ceil", "cos", "cosh", "exp", 
                "expm1", "floor", "log", "log10", "log1p", "max", "min", "random", "round", "roundHE", 
                "signum", "sin", "sinh", "sqrt", "tan", "tanh", "toDegrees", "toRadians", "ulp").contains(arg)){
                String variableName = arg.replace("(", "").replace(")", "");
                if(getVariable(variableName) != null){
                    BBVariable variable = getVariable(variableName);
                    String value = "" + variable.getValue();
                    //System.out.println(value);
                    args.set(i, arg.replace(variableName, value));
                }
            }
        }
        double returnValue = 0;
        try {
            returnValue = math.evaluate(String.join(" ", args));
        } catch (Exception e){
            System.out.println("Error: Invalid math expression");
            System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
            System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
            System.exit(1);
        }
        return String.valueOf(returnValue);
    }

    /**
      * Print argument
      * @param args name of variable to be printed
      * @return true if successful
      */
    private boolean print(ArrayList<String> args){
        if(args.get(0).equals("eval")){
            ArrayList<String> statement = new ArrayList<String>(args.subList(1, args.size()));
            System.out.println(new BBVariable(evaluate(statement)));
        } else if(args.get(0).contains("\"")){
            if(args.size() == 1){
                System.out.println(args.get(0).replace("\"", ""));
            } else {
                System.out.print(args.get(0).replace("\"", "") + " ");
                print(new ArrayList<String>(args.subList(1, args.size())));
            }
        } else {
            BBVariable variable = getVariable(args.get(0));
            if(variable != null){
                if(args.size() == 1){
                    System.out.println("" + variable.getValue());
                } else {
                    System.out.print(variable.getValue() + " ");
                    print(new ArrayList<String>(args.subList(1, args.size())));
                }
            } else {
                System.out.print("Error: Variable not found");
                System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
                System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
                return false;
            }
        }
        return true;
    }
    
    /**
      * Perform incr operation on argument
      * @param args name of variable to be incremented
      * @return true if successful
      */
    private boolean incr(ArrayList<String> args){
        String arg = args.get(0);
        return getVariable(arg).increment();
    }

    /**
      * Perform decr operation on argument
      * @param args name of variable to be decremented
      * @return true if successful
      */
    private boolean decr(ArrayList<String> args){
        String arg = args.get(0);
        return getVariable(arg).decrement();
    }

    /**
      * Perform copy operation on two arguments
      *
      * @param args name of variables to be copied
      * @return true if successful
      */
    private boolean copy(ArrayList<String> args){
        String arg = args.get(0);
        String target = args.get(2);
        BBVariable variable = getVariable(arg);
        if(variable != null){
            setVariable(target, new BBVariable(variable.getValue().toString()));
            return true;
        } else {
            System.out.print("Error: Variable not found");
            System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
            System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
            return false;
        }
    }

    /**
      * Perform clear operation on argument
      *
      * @param args name of variable to be cleared
      * @return true if successful
      */
    private boolean clear(ArrayList<String> args){
        String arg = args.get(0);
        BBVariable variable = getVariable(arg);
        if(variable != null){
            return variable.clear();
        } else {
            setVariable(arg);
            return true;
        }
    }

    /**
      * Init variable with argument
      * @param args name of variable to be incremented
      * @return true if successful
      */
    private boolean init(ArrayList<String> args){
        String arg = args.get(0);
        BBVariable variable = getVariable(arg);
        if(variable == null){
            setVariable(arg, new BBVariable(args.get(2)));
            return true;
        } else {
            System.out.println("Error: trying to initialise an existing variable " + arg);
            System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
            System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
            System.exit(1);
            return false;
        }
    }

    /**
      * Set the variable value equal to the expression value
      * @param args name of variable to be incremented
      * @return true if successful
      */
    private boolean set(ArrayList<String> args){
        String arg = args.get(0);
        BBVariable variable = getVariable(arg);
        ArrayList<String> expression = new ArrayList<String>(args.subList(2, args.size()));
        if(variable != null){
            setVariable(arg, new BBVariable(execute(String.join(" ", expression))));
            return true;
        } else {
            System.out.println("Error: trying to access an inexisting variable " + arg);
            System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
            System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
            System.exit(1);
            return false;
        }
    }

    /**
      * Create a new BBLoopCondition
      * @param args Arguments to the while loop
      * @return true if loop was successful
      */
    private boolean startLoop(ArrayList<String> args){
        BBVariable loopVarible = getVariable(args.get(0));
        BBVariable loopConstant = new BBVariable(args.get(2));
        BBLoopCondition loopCondition = new BBLoopCondition(loopVarible, loopConstant, this.parser.currentAddress() - 1);
        if(loopCondition.finished()){
            this.parser.branch(findBlockEnd(this.parser.currentAddress()) + 1);
        } else {
            loops.push(loopCondition);
        }
        return true;
    }

    /**
      * Check whether a loop ended
      *
      * @return true 
      */
    private boolean end() {
        if(!loops.isEmpty()){
            BBLoopCondition loopCondition = loops.pop();
            if(!loopCondition.finished()){
                this.parser.branch(loopCondition.getBranch());
            }
        }
        return true;
    }

    /**
      * Find corresponding end statement
      @param address Address of while
      @return Address of end
      */
    private int findBlockEnd(int address){
        ArrayList<BBStatement> statements = this.parser.getStatements();
        int depth = 0;
        for(int i = address - 1; i < statements.size(); i++){
            String operation = statements.get(i).getOperation();
            if(operation.equals("while") || operation.equals("func")){
                depth++;
            } else if (operation.equals("end")){
                depth--;
                if(depth == 0){
                    return i;
                }
            }
        }
        return -1;
    }

    private void setVariable(String name, BBVariable value){
        if(!invalidNames.contains(name) && !(name.length() > 1 && Character.isUpperCase(name.charAt(0)))){
            if(variables.get(name) == null){
                variables.put(name, value);
            } else {
                variables.get(name).setValue("" + value.getValue());
            }
        } else {
            System.out.println("Error: Invalid name of variable: " + name);
            System.out.print("Line " + (this.parser.currentAddress() - 1) + ": ");
            System.out.println(this.parser.getStatement(this.parser.currentAddress() - 1));
            System.exit(1);
        }
    }

    private void setVariable(String name){
        setVariable(name, new BBVariable("0"));
    }

    private BBVariable getVariable(String name){
        return variables.get(name);
    }

    /**
      * Set the return value of the function
      *
      * @param args ArrayList<String> of arguments
      * @return true (always)
      */
    private boolean returnValue(ArrayList<String> args){
        this.returnValue = "" + new BBVariable(execute(String.join(" ", args))).getValue();
        return true;
    }

    /**
      * Set a context for the function execution
      *
      * @param globals HashMap<String, BBVariable> of global variables
      * @param arguments HashMap<String, BBVariable> of arguments given to the function
      */
    public void setContext(HashMap<String, BBVariable> globals, HashMap<String, BBVariable> arguments){
        this.variables.putAll(globals);
        this.variables.putAll(arguments);
        this.returnValue = null;
    }
}