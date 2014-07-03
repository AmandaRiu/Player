package com.riusoft.deckofcards.player.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Author: Amanda Riu
 * Date: 6/30/2014
 * Summary: Class representing a deck of cards.
 */
public class Deck {
    ArrayList<Card> cards = new ArrayList<Card>();

    private int[] values = {1,2,3,4,5,6,7,8,9,10,11,12,13};
    private String[] suits = {"Club","Spade","Diamond","Heart"};


    /**
     * Constructs a deck of cards. Loops through the suits and for
     * each suit, loops through all the possible values, and generates
     * a new Card object for each. Then it shuffles the deck.
     */
    public Deck() {
        // Create the initial deck of unsorted cards
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < values.length; j++) {
                this.cards.add(new Card(suits[i], values[j]));
            }
        }

        // Do the initial shuffle of the cards.
        shuffleCards();
    }


    /**
     * Return the current working deck of cards.
     * @return
     */
    public ArrayList<Card> getCards() {
        return this.cards;
    }


    /**
     * Will shuffle the current deck of cards.
     */
    private void shuffleCards() {
        Collections.shuffle(this.cards);
    }


    /**
     * Returns the number of cards currently in our deck. This is just a sanity
     * check to make sure we don't have an empty deck.
     * @return
     */
    public int getCardCount() {
        return cards.size();
    }
}
