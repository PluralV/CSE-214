package cse214hw2.ArithmeticExpression;

import java.util.Stack;

public class BalancedWord {
    private final String word;
    public BalancedWord(String word) {
        if (isBalanced(word))
            this.word = word;
        else
            throw new IllegalArgumentException(String.format("%s is not a balanced word.", word));
    }
    private static boolean isBalanced(String word) {
        Stack<Character> parentheses = new Stack<>();
        for (char c : word.toCharArray()){
            if (c=='(')
                parentheses.push(c);
            else if (c==')'){
                if (parentheses.isEmpty()||parentheses.pop()!='(')
                    return false;
            }
        }
        return parentheses.isEmpty();
    }

    public String getWord() { return word; }
}