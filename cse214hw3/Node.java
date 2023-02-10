package cse214hw3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class Node<E> {
    private int num;
    private int min;
    private ArrayList<E> elements;
    private ArrayList<Node<E>> children;
    private boolean leaf;

    public Node (int min){
        num = 0;
        this.min = min;
        elements = new ArrayList<>(min*2-1);
        children = new ArrayList<>(min*2);
        leaf = true;
    }

    public String toString() {
            return toString(0);
        }
    // based on what toString() does, think about what ‘elements’ and ‘children’ can be
    public String toString(int depth) {
        StringBuilder builder = new StringBuilder();
        String blankPrefix = new String(new char[depth]).replace("\0", "\t");
        List<String> printedElements = new LinkedList<>();
        for (E e : elements) printedElements.add(e.toString());
            String eString = String.join(" :: ", printedElements);
            builder.append(blankPrefix).append(eString).append("\n");
            children.forEach(c -> builder.append(c.toString(depth + 1)));
            return builder.toString();
        }
    public boolean isLeaf(){
            return leaf;
        }
    public boolean isFull(){
        return num==(min*2-1);
    }

    public int getNum(){
        return num;
    }

    ArrayList<Node<E>> getChildren(){
        return children;
    }

    boolean setChildren(ArrayList<Node<E>> list){
        children = list;
        return true;
    }
    void setNum(int i){
        num = i;
    }
    public E getVal(int i){
            return elements.get(i);
        }
    ArrayList<E> getElements(){ return elements;}

    boolean setElements(ArrayList<E> list){
        elements = list;
        return true;
    }

    boolean setLeaf(boolean b){
        return leaf = b;
    }

}

