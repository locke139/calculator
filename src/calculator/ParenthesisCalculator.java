package calculator;

/**
 * This class extends the PrecedenceCalculator class to implement a
 * shift-reduce calculator with precedence and parentheses.
 * This calculator can evaluate expressions of the form NUM or NUM OP NUM
 * or NUM OP NUM OP NUM.
 * @author Elijah Locke
 * Lab Section 3
 */
public class ParenthesisCalculator extends PrecedenceCalculator {
    
    /**
     * Creates a calculator by configuring the precedence calculator which
     * configures the console and initializes an empty stack.
     * @param title a title for the console
     */
    public ParenthesisCalculator(String title) {
        super(title);
    }
    
    /**
     * Creates a by configuring the simple calculator which configures
     * the console and initializes an empty stack with the title
     * "Calculator With Operator Precedence and Parentheses".
     */
    public ParenthesisCalculator() {
        this("Calculator With Operator Precedence and Parentheses");
    }
    
    /**
     * Performs the evaluation of the input expression.
     * Evaluation proceeds by implementing the state machine
     * described by the associated state diagram.
     * Note that the case clauses of the switch statement
     * correspond to state names in the diagram, and that
     * similarly named private methods carry out the required
     * processing.
     * The state names are values of the enumerated type called State.
     * This method overrides the method SimpleCalculator.evaluate().
     * This method is public so it can be overridden by subclasses.
     * @return the value of the input expression.
     */
    @Override
    public double evaluate() {
        setState(State.START);
        while (true) {
            switch(getState()) {
                case START:
                    start();
                    break;
                case NUMBER:
                    number();
                    break;
                case OPERATOR:
                    operator();
                    break;
                case LEFT_PAREN:
                    leftParen();
                    break;
                case RIGHT_PAREN:
                    rightParen();
                    break;
                case END:
                    end();
                    return (Double)getStack().pop();
                default:
                    throw new Error("Something is wrong in ParenthesisCalculator.evaluate");
            }
        }
    }
    
    /**
     * Performs a reduction on the operation in the form of NUM OP NUM.
     * The body of the function is only executed if the state is not NULL.
     * The state determines the branch of the switch statement executed.
     * When state is OPERATOR call the method PrecedenceCalculator.priorityReduce
     * if both the token is an operator and NUM OP NUM is on the stack.
     * When state is RIGHT_PAREN check that the token is right parenthesis.
     * If the stack contains a left parenthesis throw a runtime exception.
     * While there is not a left parenthesis call the method
     * SimpleCalculator.reduceNumOpNum().
     * Then assign the top stack value to aNum, pop the stack again to
     * remove the left parenthesis, then push the result back onto the stack.
     * When state is END and the token is EOF, if the stack contains a left
     * parenthesis throw a RuntimeException. Then, while NUM OP NUM is on the
     * stack, call the method SimpleCalculator.reduceNumOpNum(). If the size
     * of the stack is not 1, throw a RuntimeException for mismatched parentheses.
     */
    @Override
    public void reduce() {
        if(null != getState())
            switch(getState()) {
                case OPERATOR:
                    if (getDispenser().tokenIsOperator() && numOpNumOnStack())
                        priorityReduce();
                    break;
                case RIGHT_PAREN:
                    if (getDispenser().tokenIsRightParen()) {
                        if (!getStack().contains('('))
                            throw new RuntimeException("Error -- mismatched parentheses");
                        while ((char)getStack().get(getStack().size() - 2) != '(')
                            reduceNumOpNum();
                        double aNum = (double)getStack().pop();
                        getStack().pop();
                        getStack().push(aNum);
                    }
                    break;
                case END:
                    if (getDispenser().tokenIsEOF()) {
                        if(getStack().contains('('))
                            throw new RuntimeException("Error -- mismatched parentheses");
                        while(numOpNumOnStack())
                            reduceNumOpNum();
                        if(getStack().size() != 1)
                            throw new RuntimeException("Error -- mismatched parentheses");
                    }
                    break;
            }
    }
    
    /**
     * Performs the processing in the state named START.
     * A token is parsed, and if it is not a number and rather
     * a left parentheses the state is changed to LEFT_PAREN.
     * If it is not a number and not a left parentheses, 
     * an error will be thrown. Otherwise, the state is changed to NUMBER.
     */
    private void start() {
        getDispenser().advance();
        if (getDispenser().tokenIsNumber()) setState(State.NUMBER);    
        else if (getDispenser().tokenIsLeftParen()) setState(State.LEFT_PAREN);
        else syntaxError(NUM_OR_LEFT_PAREN);
    }
    
    /**
     * Performs the processing in the state named NUMBER.
     * The number is shifted onto the stack, the 
     * operation is reduced and the next token is parsed.
     * If the token is EOF the state is changed to END.
     * If the token is an operator the state is changed to OPERATOR.
     * If the token is a left parentheses the state is changed to LEFT_PAREN.
     * If the token is a right parentheses the state is changed to RIGHT_PAREN.
     * Otherwise an error is signaled.
     */
    private void number() {
        shift();
        getDispenser().advance();
        if (getDispenser().tokenIsEOF()) setState(State.END);
        else if (getDispenser().tokenIsOperator()) setState(State.OPERATOR);
        else if (getDispenser().tokenIsRightParen()) setState(State.RIGHT_PAREN);
        else syntaxError(OP_OR_END);
    }
    
    /**
     * Performs the processing in the state named OPERATOR.
     * The operation is first reduced, then the operator is shifted
     * onto the stack and the next token is parsed.
     * If the next token is a number the state is changed to NUMBER.
     * If the next token is a left parentheses the state is changed
     * to LEFT_PAREN.
     */
    private void operator() {
        reduce();
        shift();
        getDispenser().advance();
        if(getDispenser().tokenIsNumber()) setState(State.NUMBER);
        else if (getDispenser().tokenIsLeftParen()) setState(State.LEFT_PAREN);
        else syntaxError(NUM);
        
    }
    
    /**
     * Performs the processing in the state named LEFT_PAREN.
     * A token is parsed, and if it is a left parentheses the state
     * is changed to LEFT_PAREN. If it is a number the state is changed
     * to NUMBER. Otherwise, an error is signaled.
     */
    private void leftParen() {
        shift();
        getDispenser().advance();
        if (getDispenser().tokenIsLeftParen()) setState(State.LEFT_PAREN);
        else if (getDispenser().tokenIsNumber()) setState(State.NUMBER);
        else syntaxError(NUM_OR_LEFT_PAREN);
    }
    
    /**
     * Performs the processing in the state named RIGHT_PAREN.
     * A token is parsed, and if it is EOF the state is changed to END.
     * If the token is an operator the state is changed to OPERATOR.
     * If the token is a right parentheses the state is changed to RIGHT_PAREN.
     * Otherwise, an error is signaled.
     */
    private void rightParen() {
        reduce();
        getDispenser().advance();
        if (getDispenser().tokenIsEOF()) setState(State.END);
        else if (getDispenser().tokenIsOperator()) setState(State.OPERATOR);
        else if (getDispenser().tokenIsRightParen()) setState(State.RIGHT_PAREN);
        else if (getDispenser().tokenIsLeftParen()) syntaxError(OP);
        else syntaxError(OP_OR_END);
            
    }
}
