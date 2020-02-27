package calculator;

/**
 * This class extends the SimpleCalculator class to implement a simple
 * shift-reduce calculator with no precedence.
 * This calculator can evaluate expressions of the form NUM or NUM OP NUM.
 * @author Elijah Locke
 * Lab Section 3
 */
public class NoPrecedenceCalculator extends SimpleCalculator {
    
    /**
     * Creates a calculator by configuring the simple calculator which
     * configures the console and initializes an empty stack.
     * @param title a title for the console
     */
    public NoPrecedenceCalculator(String title) {
        super(title);
    }
    
    /**
     * Creates a by configuring the simple calculator which configures
     * the console and initializes an empty stack with the title
     * "Calculator Without Operator Precedence".
     */
    public NoPrecedenceCalculator() {
        this("Calculator Without Operator Precedence");
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
        while(true){
            switch(getState()){
                case START:
                    start();
                    break;
                case NUMBER:
                    number();
                    break;
                case OPERATOR:
                    operator();
                    break;
                case END:
                    end();
                    return (Double)getStack().pop();
                default:
                    throw new Error("Something is wrong in NoPrecedenceCalculator.evaluate"); // shouldn't happen
            }
        }
    }
    
    /**
     * Performs the processing in the state named START.
     * A token is parsed, and if it is not a number an error is signaled.
     * Otherwise, the state is changed to NUMBER.
     */
    private void start() {
        getDispenser().advance();
        if (!getDispenser().tokenIsNumber()) syntaxError(NUM);
        setState(State.NUMBER);
    }
    
    /**
     * Performs the processing in the state named NUMBER.
     * The number is shifted onto the stack, the stack is reduced, and
     * the next token is parsed.
     * If the token is EOF the state is changed to END.
     * If the token is an operator the state is changed to OPERATOR.
     * Otherwise an error is signaled.
     */
    private void number() {
        shift();
        getDispenser().advance();
        if (getDispenser().tokenIsEOF()) setState(State.END);
        else if (getDispenser().tokenIsOperator()) setState(State.OPERATOR);
        else syntaxError(OP_OR_END);
    }
    
    /**
     * Performs the processing in the state named OPERATOR.
     * The stack is reduced, the operator is shifted onto the stack,
     * and the next token is parsed.
     * If the next token is not a number an error is signaled.
     * Otherwise the state is changed to NUMBER.
     */
    private void operator() {
        reduce();
        shift();
        getDispenser().advance(); 
        if (!getDispenser().tokenIsNumber()) syntaxError(NUM);
        setState(State.NUMBER);
    }
    
}
