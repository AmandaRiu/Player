package com.riusoft.deckofcards.player.model;

import javax.swing.*;

/**
 * Author: Amanda Riu
 * Date: 6/30/2014
 * Summary: This class represents a single card in a deck of playing cards.
 */
public class Card {

    private String suit;
    private int value;
    private ImageIcon cardImage;


    /**
     * Constructs a new card using the values provided.
     *
     * @param s The suit of the card.
     * @param v The value of the card (1=Ace, 11=Jack, 12=Queen, 13=King)
     */
    public Card(String s, int v){
        setCardImage(null);
        setSuit(s);
        setValue(v);
    }

    public String getSuit(){
        return suit;
    }
    public void setSuit(String suit){
        this.suit = suit;
    }

    public int getValue(){
        return value;
    }
    public void setValue(int value){
        this.value = value;
    }

    /**
     * If we don't already have the card image, lets load it dynamically
     * using the value and suit.
     * @return The ImageIcon for this card
     */
    public ImageIcon getCardImage() {
        if (this.cardImage == null) {
            setCardImage(getImageIcon(getValue(), getSuit()));
        }
        return cardImage;
    }
    public void setCardImage(ImageIcon cardImage) {
        this.cardImage = cardImage;
    }

    /**
     * Dynamically loads the image file using a combination of the suit
     * and value.
     *
     * @param value The numerical value of the card (ie - 1=Ace, 2, 3...)
     * @param suit The suit of the card (ie - Heart, Diamond...)
     *
     * @return The ImageIcon matching the card value and suit.
     */
    private ImageIcon getImageIcon(int value, String suit) {
        String imageName = value + suit + ".png";
        String imagePath = "assets/" + imageName;
        return new ImageIcon(getClass().getResource(imagePath));
    }


    public String toString(){
        return "\n"+value + " of "+ suit;
    }
}