package cse214hw2.ArithmeticExpression;

import java.util.Stack;

public class ToPostfixConverter implements Converter {
    private int start = 0;
    public String convert(ArithmeticExpression expression) {
        StringBuilder expressionBuilder = new StringBuilder();
        for (char c : expression.getExpression().toCharArray()){//removes white space from expression
            if (c!=' '){
                expressionBuilder.append(c);
            }
        }
        String s = expressionBuilder.toString();//
        StringBuilder postfixBuilder = new StringBuilder();//
        Stack<Operator> tokenStack = new Stack<>();//stack of operators/parentheses
        while (start<s.length()){
            String token = nextToken(s,start);//reads next token from start, moves by token length down string
            start+=token.length();
            if (isOperand(token)) {//if an operand, immediately append to the output
                postfixBuilder.append(token);
                postfixBuilder.append(' ');
            }
            else {//if an operator/parenthesis, create an Operator to check against the stack

                //opReal: Operator represented by current token
                Operator opReal = Operator.of(token);
                if (opReal.equals(Operator.RIGHT_PARENTHESIS)) {
                    //if you encounter a right parenthesis, pop stack until you reach a left
                    while (!(tokenStack.peek().equals(Operator.LEFT_PARENTHESIS))) {
                        postfixBuilder.append(tokenStack.pop().getSymbol());
                        postfixBuilder.append(' ');
                    }
                    tokenStack.pop();
                }
                else if (tokenStack.isEmpty()||//if stack empty, push the operator to the stack
                        opReal.equals(Operator.LEFT_PARENTHESIS)||//if operator is left parenthesis or previous operator
                        tokenStack.peek()==Operator.LEFT_PARENTHESIS||//is left parenthesis, push operator to the stack
                        opReal.getRank()<tokenStack.peek().getRank())//if opReal rank is higher than top of stack, push
                                                                    //operator to stack
                    tokenStack.push(opReal);

                else if (Operator.isOperator(opReal.getSymbol())){//if it is a binary operator, check stack:
                    //if operator is a lower rank than top of stack, add it to the stack
                    //if operator is higher rank to top of stack, pop stack, add to output, and test against the new
                    //top of the stack
                    //if operator is equal to stack top, pop stack and then push operator to stack
                    if (opReal.getRank()>=tokenStack.peek().getRank()){
                        while (tokenStack.peek().getRank()>opReal.getRank()) {
                            postfixBuilder.append(tokenStack.pop().getSymbol());
                            postfixBuilder.append(' ');
                            if (tokenStack.isEmpty()) break;
                        }
                        if (tokenStack.isEmpty()||tokenStack.peek().equals(Operator.LEFT_PARENTHESIS)
                        ||opReal.getRank()<tokenStack.peek().getRank()) //if stack emptied
                            tokenStack.push(opReal);
                        else if (tokenStack.peek().getRank()==opReal.getRank()){//if equal rank was found
                            postfixBuilder.append(tokenStack.pop().getSymbol());
                            postfixBuilder.append(' ');
                            tokenStack.push(opReal);
                        }
                    }
                }

            }
        }
        while (!tokenStack.isEmpty()){
            postfixBuilder.append(tokenStack.pop().getSymbol());
            postfixBuilder.append(' ');
        }
        s = postfixBuilder.toString();
        start = 0;
        return s;
    }

    @Override
    public String nextToken(String s, int start) {
        TokenBuilder TB = new TokenBuilder();
        String sub = s.substring(start);
        int i;
        for (i=0; i<sub.length();i++){
            char c = sub.charAt(i);
            if (i==0) {
                TB.append(c);
                if (!(isOperand(c+""))) break;
            }
            else {
                if (!(isOperand(c+""))) break;
                else TB.append(c);
            }
        }

        return TB.build();
    }

    @Override
    public boolean isOperand(String s) {
        char c = s.charAt(0);
        if (s.length()==1)//if it is a one-length token, return false if it is an operator/parenthesis
            return !(c==Operator.LEFT_PARENTHESIS.getSymbol()||
                   c==Operator.RIGHT_PARENTHESIS.getSymbol()||Operator.isOperator(c));
        //otherwise return true (we are assuming that all ArithmeticExpressions are valid)
        return true;
    }
}
