package cse214hw3;

class NodeIndexPair<E>{
    public final Node<E> nodeLocation;
    public final int index;

    public NodeIndexPair(Node<E> n, int i) {
        this.nodeLocation = n;
        this.index = i;
    }

    public String toString(){
        return (nodeLocation.getVal(index).toString())+" found at index "+index+(nodeLocation.isLeaf() ? " - in leaf" : " - in non-leaf");
    }
}
