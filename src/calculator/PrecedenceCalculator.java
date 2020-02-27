package calculator;

/**
 * This class extends the NoPrecedenceCalculator class to implement a
 * shift-reduce calculator with precedence but without parentheses.
 * This calculator can evaluate expressions of the form NUM or NUM OP NUM
 * or NUM OP NUM OP NUM.
 * @author Elijah Locke
 * Lab Section 3
 */
public class PrecedenceCalculator extends NoPrecedenceCalculator {
    
    /**
     * Creates a calculator by configuring the simple calculator which
     * configures the console and initializes an empty stack.
     * @param title a title for the console
     */
    public PrecedenceCalculator(String title) {
        super(title);
    }
    
    /**
     * Creates a by configuring the simple calculator which configures
     * the console and initializes an empty stack with the title
     * "Calculator With Operator Precedence".
     */
    public PrecedenceCalculator() {
        this("Calculator With Operator Precedence");
    }
    
    /**
     * Performs a reduction on the operation in the form of NUM OP NUM.
     * Reduction begins with a call to method
     * PrecedenceCalculator.priorityReduce() if both an operation in the form
     * of NUM OP NUM is on the stack, and the token is an operator.
     * If token is EOF, then while an operation in the form of NUM OP NUM
     * is on the stack, make a call to SimpleCalculator.numOpNumOnStack().
     */
    @Override
    public void reduce() {
        if(numOpNumOnStack() && getDispenser().tokenIsOperator()) priorityReduce();
        if (getDispenser().tokenIsEOF())
            while (numOpNumOnStack()) reduceNumOpNum();
    }
    
    /**
     * Performs a reduction on the operation on our stack with 
     * precedence in mind.
     * This method is public so that it can be used by subclasses.
     * The method begins with identifying the operator currently on the stack.
     * If that operator is division or multiplication call
     * SimpleCalculator.reduceNumOpNum().
     * If the size of the stack is greater than one but the operator is not
     * division or multiplication, perform addition or subtraction with a call
     * to SimpleCalculator.reduceNumOpNum().
     */
    public void priorityReduce() {
        char item = (char)getStack().get(getStack().size()-2);
        if(item == '/' || item == '*') reduceNumOpNum();
        if(getStack().size() > 1) {
            char item2 = (char)getStack().get(getStack().size()-2);
            item = (char)getDispenser().getToken();
            if ((item == '-' || item == '+') && (item2 == '-' || item2 == '+')) reduceNumOpNum();
        }
    }
}
