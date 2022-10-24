package cse214hw1;
/*
 *@author Bennett Schirmer 115039456
 *
 * CSE 214 DoublyLinkedList
 * */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<E> implements ListAbstractType<E> {

    private int size;
    private Node<E> head;//first node
    private Node<E> tail;//last node

    //UNIVERSAL ITERATOR
    //used for navigating the list in most scenarios to make it faster
    //will always sit at the last location iterated to
    private DoublyLinkedListIterator uniIt;
    //ties uniIt to an index position of the list
    private int uniIndex = 0;
    //records corresponding index of last removed element
    private int removalIndex = 0;
    //records what element the remove operation was last called on
    private E lastRemoved;

    @Override
    public boolean add(E element) {
        Node<E> ins = new Node<>(element);
        if (head==null) {//Adds the first element if empty
            head = ins;
            uniIt = new DoublyLinkedListIterator(head);
        }
        else{//Otherwise adds at the end
            ins.prev = tail;
            tail.next = ins;
        }
        tail = ins;
        size++;
        return true;
    }

    @Override
    //removes the first occurrence of element from the list
    public boolean remove(E element) {
        if (isEmpty()||element==null) throw new NoSuchElementException();
        if (head.element.equals(element)){//if removing the head, decrease uniIndex unless uniIndex is the head
            lastRemoved = element;
            removalIndex = 0;
            return removeHead();
        }
        //if the last removed element is the same is the element currently being looked for
        //(i.e. in cases where a method iterates through list and removes every element equal to x)
        if (lastRemoved!=null&&element.equals(lastRemoved)){
            //if last operation put uniIt somewhere else, move it back to the index of the removed element
            if (uniIndex!=removalIndex){
                if (uniIndex>removalIndex){
                    for (int i = 0; i<uniIndex-removalIndex;i++) uniIt.previous();
                }
                else{
                    for (int i = 0; i<removalIndex-uniIndex;i++) uniIt.next();
                }
                uniIndex = removalIndex;//basically, if the first occurrence of element has already been removed,
                //uniIt starts iterating from that element's position
            }
        }
        else{//otherwise restart at the head
            uniIt = new DoublyLinkedListIterator(head);
            uniIndex = 0;
        }
        while(uniIt.hasNext()){
            if (uniIt.next().equals(element)){//if element being pointed to is the one being
                uniIt.previous();//moves back so next points to the correct element
                lastRemoved = element;//records what was just removed
                removalIndex = uniIndex;//sets removal index to current index
                uniIt.remove();//removes element
                size--;//shrinks list
                return true;
            }
            uniIndex++;//move index up each time it moves to the next element
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E element) {
        if (isEmpty()) return false;
        for (E e : this){
            if (e.equals(element)) return true;
        }
        return false;
    }

    @Override
    public E get(int index) {//WRITE THIS SHIT
        if (0>index||index>=size) throw new IndexOutOfBoundsException();
        if (size-index>=Math.abs(uniIndex-index)&&index>=Math.abs(uniIndex-index)){//if the universal iterator is the closest index
            //from the universal iterator's index
            navigate(index-uniIndex>=0,Math.abs(index-uniIndex),uniIt);
            uniIndex+=(index-uniIndex+1);//adjusts uniIndex by the distance between uniIt node and desired node,
            //taking account of direction
            return uniIt.next();
        }
        else{
            boolean forward = size/2>=index;
            TwoWayListIterator<E> it = iterator(forward);//start from head and go forward
            //if index is on left side, tail and backwards if index is on right
            if (!forward) navigate(false,size-index,it); //navigates until next points at
                //desired element
            else navigate (true,index,it);
            return it.next();//returns next value
        }
    }



    @Override
    public E set(int index, E element) {
        if (0>index||index>=size) throw new IndexOutOfBoundsException();
        lastRemoved=null;
        removalIndex=0;
        E placeholder;
        if (size-index>=Math.abs(uniIndex-index)&&index>=Math.abs(uniIndex-index)){//if the universal iterator is the closest index
            navigate(index-uniIndex>=0,Math.abs(index-uniIndex),uniIt);
            placeholder=uniIt.next();
            uniIndex+=(index-uniIndex)+1;
            uniIt.set(element);
        }
        else {
            boolean forward = size / 2 >= index;
            TwoWayListIterator<E> it = iterator(forward);//start from head and go forward
            //if index is on left side, tail and backwards if index is on right
            if (!forward) {
                navigate(false, size - index - 1, it); //navigates until next points at desired element
                placeholder = it.previous();//sets placeholder to the element to be replaced
                it.set(element);//sets last returned element from previous to @param element
            } else {
                navigate(true, index, it);
                placeholder = it.next();
                it.set(element);
            }
        }
        return placeholder;
    }

    @Override
    public void add(int index, E element) {
        if (0>index||index>size) throw new IndexOutOfBoundsException();
        if (index<=removalIndex){
            lastRemoved = null;
            removalIndex = 0;
        }
        Node<E> ins = new Node<>(element);
        if (index == size) { //handles head/tail cases
            add(element);
            return;
        }

        else if (index==0) {
            head.prev = ins;
            ins.next = head;
            head = ins;
        }

        else if (size-index>=Math.abs(uniIndex-index)&&index>=Math.abs(uniIndex-index)){//if the universal iterator is the closest index
            navigate(index-uniIndex>=0,Math.abs(index-uniIndex),uniIt);
            uniIndex+=(index-uniIndex);
            uniIt.add(element);
        }
        else{//if starting from one or the other end
            boolean forward = index<=(size/2+2);//true if iterating forward, false if backward
            TwoWayListIterator<E> it = iterator(forward);
            if (forward){
                navigate(true,index,it);
            }
            else{
                navigate(false,size-index,it);
            }
            it.add(element);
        }
        if (uniIndex>index)
            uniIndex++;
        size++;
    }

    @Override
    public void remove(int index) {
        if (0>index||index>size) throw new IndexOutOfBoundsException();
        lastRemoved = null;
        removalIndex = 0;

        if (index == 0){//if removing the head, decrease uniIndex unless uniIndex is the head
            removeHead();
            return;
        }
        if (index == size-1){
            if (uniIndex == size){
                uniIt.previous();
                uniIndex--;
            }
            tail = tail.prev;
            tail.next = null;
            size--;
            return;
        }



        if (size-index>=Math.abs(uniIndex-index)&&index>=Math.abs(uniIndex-index)){//if the universal iterator is the closest index
            navigate(index-uniIndex>=0,Math.abs(index-uniIndex),uniIt);
            uniIndex+=(index-uniIndex);
            uniIt.remove();
        }
        else{//if starting from one or the other end
            boolean forward = index<=(size/2+2);//true if iterating forward, false if backward
            TwoWayListIterator<E> it = iterator(forward);
            if (forward){
                navigate(true,index,it);
            }
            else{
                navigate(false,size-index,it);
            }
            it.remove();
        }
        if (index<uniIndex) uniIndex--;
        size--;
    }

    private boolean removeHead(){//returns true if head was successfully removed from the list
        if (size == 1) {
            head = null;
            size--;
            uniIndex = 0;
            return true;
        }

        if (uniIndex==0){
            uniIt.next();
        }
        else uniIndex--;
        head = head.next;
        head.prev = null;
        size--;
        return true;
    }

    @Override
    public TwoWayListIterator<E> iterator() {
        return new DoublyLinkedListIterator(head);
    }

    private TwoWayListIterator<E> iterator(boolean forward){
        //@param forward: true if iterating from front, false if from back
        if (forward)
            return new DoublyLinkedListIterator(head);
        else
            return new DoublyLinkedListIterator(tail);
    }

    private void navigate(boolean forward, int i, TwoWayListIterator<E> it){
        //moves iterator cursor i times forward or backward in the list
        //@param forward true if navigating forward, false if backward
        //@param i number of next/previous calls
        //@param it the list iterator being used to navigate
        if (forward){
            for (int j = 0; j<i;j++)
                it.next();
        }
        else{
            for (int j = 0; j<i; j++)
                it.previous();
        }

    }
    private class DoublyLinkedListIterator implements TwoWayListIterator<E>{

        public Node<E> cursor;
        private boolean rnext = false;//if last operation was next
        private boolean rprev = false;//if last operation was previous
        public DoublyLinkedListIterator(Node<E> cursor){
            this.cursor = new Node<>(null);
            if (cursor==null){
                this.cursor.next = null;
                this.cursor.prev = null;
            }
            else if (cursor.next==null&&cursor.prev!=null){
                this.cursor.prev = cursor;
            }
            else {
                this.cursor.next = cursor;
                this.cursor.prev = cursor.prev;
            }
        }

        @Override
        public boolean hasPrevious() {
            return cursor.prev!=null;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) throw new NoSuchElementException();
            cursor.next = cursor.prev;//moves the cursor back
            cursor.prev = cursor.prev.prev;
            rprev = true;
            rnext = false;
            return cursor.next.element;//returns the element passed
        }

        @Override
        public void add(E element) {
            Node<E> ins = new Node<>(element);
            ins.next = cursor.next;//links ins at position cursor element's next would be at
            ins.prev = cursor.prev;
            if (hasNext()) ins.next.prev = ins;//if ins has a next element set its previous pointer to ins
            if (hasPrevious()) ins.prev.next = ins;//if ins has a previous element set its next pointer to ins
            cursor.next = ins;//moves cursor "backward" so next is pointing at inserted element and next is unchanged
            rnext = false;
            rprev = false;
        }

        @Override
        public void set(E element) {
            if (!rnext&&!rprev)//throws if node was added/removed/at head or tail
                //(i.e. cases where the current node has been detached)
                throw new IllegalStateException();
            if (rnext)//if last operation was next, return prev element (it would have been returned by next)
                cursor.prev.element = element;
            else//if last operation was previous, return next element (it would be the element returned by
                //the last previous operation)
                cursor.next.element = element;
        }

        @Override
        public boolean hasNext() {
            return cursor.next!=null;
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            cursor.prev = cursor.next;//moves next to its next node
            cursor.next = cursor.next.next;//moves prev to current next
            rnext = true;
            rprev = false;
            return cursor.prev.element;//returns the element passed
        }

        @Override
        public void remove() {//relinks previous element and element two steps ahead to each other
            //detaches cursor.next from link sequence
            if (!hasNext()) throw new NullPointerException();
            if (cursor.next.next!=null) cursor.next.next.prev = cursor.prev;
            if (hasPrevious()) cursor.prev.next = cursor.next.next;
            cursor.next = cursor.next.next;
            rnext = false;
            rprev = false;
        }

    }

    private static class Node<E>{
        public E element;
        public Node<E> next = null;
        public Node<E> prev = null;

        public Node(E element){
            this.element = element;
        }
    }


    @Override
    public String toString() {
        Iterator<E> it = this.iterator();
        if (!it.hasNext())
            return "[]";
        StringBuilder builder = new StringBuilder("[");
        while (it.hasNext()) {
            E e = it.next();
            builder.append(e.toString());
            if (!it.hasNext())
                return builder.append("]").toString();
            builder.append(", ");
        }
// code execution should never reach this line
        return null;
    }
}
