package cse214hw2.DynamicSet;

public class DynamicIntegerSet implements DynamicSet {
    private Node root;
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Integer x) {
        return contains(x,root);
    }

    private boolean contains(Integer x, Node n){
        if (n.data.equals(x)) return true;
        else {
            if (n.data<x&&n.hasLeft()) return contains(x,n.left);
            else if (n.hasRight()) return contains(x,n.right);
            else return false;
        }
    }

    public boolean add(Integer x) {
        if (size == 0) {
            root = new Node(x);
            size++;
            return true;
        }
        return add(x,root);
    }

    private boolean add(Integer x, Node n){
        int y = n.data;
        if (x>y) {
            if (n.hasRight()) return add(x, n.right);
            else{
                n.right = new Node(x);
                n.right.parent = n;
                size++;
                return true;
            }
        }
        else if (x<y){
            if (n.hasLeft()) return add(x,n.left);
            else {
                n.left = new Node(x);
                n.left.parent = n;
                size++;
                return true;
            }
        }
        else return false;
    }

    //REMOVE ALGORITHM
    /*
    * 1. Find element to remove
    * 2. Save its left and right in temp nodes
    * 3. Save the previous node in temp reference before accessing it
    * 4. Set previous node's left/right (whichever was the removed node) to the left child of
    *    the removed node
    * 5. Attach the right child of the removed node to the rightmost leaf of its left subtree*/
    public boolean remove(Integer x) {
        if (remove(x, root)) {
            size--;
            return true;
        }
        else return false;

    }

    private boolean remove (Integer x, Node n){
        if (x>n.data){
            if (n.hasRight()){
                if (n.right.data.equals(x)){
                    if (n.right.isChildless()) n.right = null;
                    else if (n.right.hasRight()&&(!n.right.hasLeft())) n.right = n.right.right;
                    else if ((!n.right.hasRight())&&n.right.hasLeft()) n.right = n.right.left;
                    else{
                        removeNode(n.right);
                    }
                    return true;
                }
                else return remove(x,n.right);
            }
            return false;
        }
        if (x<n.data){
            if (n.hasLeft()){
                if (n.left.data.equals(x)){
                    if (n.left.isChildless()) n.left = null;
                    else if (n.right.hasRight()&&(!n.right.hasLeft())) n.left = n.left.right;
                    else if ((!n.right.hasRight())&&n.right.hasLeft()) n.left = n.left.left;
                    else{
                        removeNode(n.left);
                    }
                    return true;
                }
                else return remove(x,n.left);
            }
            return false;
        }
        else{
           removeNode(n);
           return true;
        }
    }

    private void removeNode(Node n){
        int successor = findSuccessor(n,true);
        int replaceData = successor;
        this.remove(successor,root);
        n.data = replaceData;
    }

    private int findSuccessor (Node n, boolean b){//boolean value for whether this is the first call
        if (b) {
            if (n.hasRight()) {
                if (n.right.hasLeft()) return findSuccessor(n.right, false);
                else return n.right.data;
            }
            else return findSuccessorParent(n);
        }
        else{
            if (n.hasLeft()){
               return findSuccessor(n.left,false);
            }
            else return n.data;
        }
    }

    private int findSuccessorParent (Node n){
        if (n.parent.left.equals(n)) return n.parent.data;
        else return findSuccessorParent(n.parent);
    }

    public static class Node implements PrintableNode {
        Integer data;
        Node left, right,parent;

        Node(int x) {
            this(x, null, null);
        }

        Node(int x, Node left, Node right) {
            this.data = x;
            this.left = left;
            this.right = right;
        }

        @Override
        public String getValueAsString() {
            return data.toString();
        }

        @Override
        public PrintableNode getLeft() {
            return left;
        }

        @Override
        public PrintableNode getRight() {
            return right;
        }

        private boolean hasRight(){
            return right!=null;
        }
        private boolean hasLeft(){
            return left!=null;
        }

        private boolean isChildless(){
            return right==null&&left==null;
        }
    }

    // this method must be there exactly in this form
    public Node root() {
        return this.root;
    }
// rest of your code for this class, including the size, contains, add, and remove methods
}