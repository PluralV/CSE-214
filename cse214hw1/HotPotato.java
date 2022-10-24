package cse214hw1;
import java.util.LinkedList;
/*
 *@author Bennett Schirmer 115039456
 *
 * CSE 214 HotPotato
 * */
public class HotPotato {
    public static DoublyLinkedList<Integer> playWithDoublyLinkedList(int numberOfPlayers, int lengthOfPass) {
        DoublyLinkedList<Integer> players = new DoublyLinkedList<>();
        DoublyLinkedList<Integer> winners = new DoublyLinkedList<>();
        for (int i = 1; i<=numberOfPlayers; i++) players.add(i);
        int playerIndex = 0;
        while (players.size()>1){
            int passes = 0;
            while (passes<lengthOfPass){
                playerIndex++;
                if (playerIndex>players.size()-1) playerIndex = 0;
                passes++;
            }
            winners.add(players.get(playerIndex));
            players.remove(playerIndex);
            if (playerIndex>players.size()-1) playerIndex = 0;
        }
        winners.add(players.get(0));
        return winners;
    }
    public static LinkedList<Integer> playWithLinkedList(int numberOfPlayers, int lengthOfPass) {
        LinkedList<Integer> players = new LinkedList<>();
        LinkedList<Integer> winners = new LinkedList<>();

        for (int i = 1; i<=numberOfPlayers; i++) players.add(i);
        int playerIndex = 0;
        while (players.size()>1){
            int passes = 0;
            while (passes<lengthOfPass){
                playerIndex++;
                if (playerIndex>players.size()-1) playerIndex = 0;
                passes++;
            }
            winners.add(players.get(playerIndex));
            players.remove(playerIndex);
            if (playerIndex>players.size()-1) playerIndex = 0;
        }
        winners.add(players.get(0));
        return winners;
    }

    public static void main(String... args) {
        // in both methods, the list is the order in which the players are eliminated
        // the last player (i.e., the last element in the returned list) is the winner
        System.out.println(playWithDoublyLinkedList(5, 0)); // expected output: [1, 2, 3, 4, 5]
        System.out.println(playWithLinkedList(5, 1)); // expected output: [2, 4, 1, 5, 3]
    }
}
