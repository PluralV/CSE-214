package cse214hw3;

import java.util.ArrayList;

public class BTree<E extends Comparable<E>> implements AbstractBTree<E> {
    private final int MIN;
    private Node<E> root;
    public BTree(int min){
        /*
        * BTree nodes must all have minimum of min-1 elements (keys)
        * but this restriction DOES NOT APPLY to the root
        * they must also have at most 2min-1 keys
        * Effectively, once the 2minth key is added, the node must be split into a root with min children
        * et cetera until the tree root is reached
        */
        if (min>=2) MIN = min;
        else throw new IllegalArgumentException();
        root = new Node<>(MIN);
    }
    @Override
    public NodeIndexPair<E> contains(E element) {
        return contains(element, root);
    }

    private NodeIndexPair<E> contains(E element, Node<E> n){
        ArrayList<E> elements = n.getElements();
        for (int i = 0; i<elements.size();i++){
            //compares element in node to search element
            int comp = elements.get(i).compareTo(element);
            //if equal, return:
            if (comp == 0) return new NodeIndexPair<>(n,i);
            //if unequal:
            else {
                //if the search element is less than the element in the node,
                //check its child
                if (comp>0)
                    return n.isLeaf() ? null : contains(element, n.getChildren().get(i));
                //if search element has reached the last element in node and still is greater,
                //check its far right child
                else if (i == elements.size()-1)
                    return n.isLeaf()? null : contains(element, n.getChildren().get(i+1));
            }
        }
        return null;
    }

    @Override
    public void add(E element) {
        if (root.isFull()) {
            splitChild(new Node<>(MIN),root,0);
        }
        addNonFull(element, root);
    }

//parent: current node; child: node being split; i: index where median value will be inserted into parent
//element arraylist
    //basic function: splits a node around the median, adds the median value to its parent, or creates a new
    //parent if split node is the root; sub-nodes become the child to the left and right of median value in
    //the parent node
    private void splitChild(Node<E> parent, Node<E> child, int i){

        //median: median value stored in the child node
        E median = child.getElements().get(MIN-1);

        //add median value to correct place in parent node, recorded by i
        //(This is the index which the child node descends from; i.e., everything
        //to the right of index i in parent.elements would be greater than every value in child,
        //and everything to the left of index i in parent.elements would be less than every value in child,
        // including the median lol)
        parent.getElements().add(i,median);
        //increase size of parent
        parent.setNum(parent.getNum()+1);
        //left: node storing everything to the left of the median, will be the new child index i
        //right: node storing everything to the right of the median, will be inserted into children at i+1
        Node<E> left = new Node<>(MIN);
        Node<E> right = new Node<>(MIN);

        //set elements in left to everything to the left of the median of child (at index MIN-1)
        //if the child node had children, set left's children to the left half of child.children
        left.setElements(new ArrayList<>(child.getElements().subList(0,MIN-1)));
        left.setNum(MIN-1);
        if (!child.isLeaf()) {
            left.setChildren(new ArrayList<>(child.getChildren().subList(0, MIN)));
            left.setLeaf(false);
        }

        //same thing, but for right
        right.setElements(new ArrayList<>(child.getElements().subList(MIN,MIN*2-1)));
        right.setNum(MIN-1);
        if (!child.isLeaf()) {
            right.setChildren(new ArrayList<>(child.getChildren().subList(MIN, MIN * 2)));
            right.setLeaf(false);
        }

        //in all cases except when the root is split (parent will be an empty node in that case):
        //1. remove the child node at index i
        //2. add "left" node to index i
        //3. add "right" node to index i+1
        //if the root is being split (and parent is an empty node), just add left and right
        //(index i will be 0, since it's empty)
        if (parent.getChildren().size()>0) parent.getChildren().remove(i);
        parent.getChildren().add(i,left);
        parent.getChildren().add(i+1,right);

        //if root was split: make the parent node the new root, and set it to non-leaf
        if (child==root){
            parent.setLeaf(false);
            root = parent;
        }
    }

    private void addNonFull(E element, Node<E> n){
        int i = n.getNum()-1;
        if (n.isLeaf()){
            while (i>=0 && (n.getVal(i)).compareTo(element)>=0){
                //while current element is greater than inserted element
                if (n.getVal(i).compareTo(element)==0) return;
                i--;
            }
            n.getElements().add(i+1,element);
            n.setNum(n.getNum()+1);
        } else {
            while (i>=0&&element.compareTo(n.getVal(i))<0) i--;
            i++;
            if (n.getChildren().get(i).isFull()){
                splitChild(n,n.getChildren().get(i),i);
                if (element.compareTo(n.getElements().get(i))>0) i++;
            }
            addNonFull(element,n.getChildren().get(i));
        }
    }

    public String toString(){
        return root.toString();
    }
}
