package cse214hw2.ArithmeticExpression;

import java.util.*;

public class PostfixEvaluator implements Evaluator {


    public double evaluate(String expression){
        Stack<Double> tokenStack = new Stack<>();
        ArrayDeque<String> tokenQueue = new ArrayDeque<>(expression.length());
        int start = 0;

        //creates a queue of operators/operands to iterate through
        while (start<expression.length()){
            StringBuilder doubleMaker = new StringBuilder();
            while (expression.charAt(start)!=' '){
                doubleMaker.append(expression.charAt(start));
                start++;
            }
            tokenQueue.addLast(doubleMaker.toString());
            start++;
        }

        while (!tokenQueue.isEmpty()){
            String token = tokenQueue.removeFirst();
            if (Operator.isOperator(token.charAt(0))){
                Operator opReal = Operator.of(token);
                if (tokenStack.size()<2) throw new IllegalStateException();
                double operand2 = tokenStack.pop();//in order of operation
                double operand1 = tokenStack.pop();//first operand is lower in the stack
                double result;
                if (opReal.equals(Operator.ADDITION)) result = operand1 + operand2;
                else if (opReal.equals(Operator.SUBTRACTION)) result = operand1 - operand2;
                else if (opReal.equals(Operator.MULTIPLICATION)) result = operand1 * operand2;
                else result = operand1 / operand2;
                tokenStack.push(result);
            }
            else tokenStack.push(Double.parseDouble(token));
        }
        if (tokenStack.size()!=1) throw new IllegalArgumentException("Illegal expression passed to evaluator");
        return (double)(Math.round(tokenStack.pop()*100))/100;
    }


}
