package com.riusoft.deckofcards.player;

import com.google.gson.Gson;
import com.riusoft.deckofcards.player.model.Card;
import com.riusoft.deckofcards.player.model.Deck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Author: Amanda Riu
 * Date: 7/1/2014
 *
 * This application is a companion application to the DealerServer application. The
 * DealerServer must already be running in order for this application to run properly.
 * If the DealerServer is not running, we will exit with an Exception.
 *
 * The PlayerClient displays a deck provided by the DealerServer in the Window.
 *
 * The PlayerClient connects to the DealerServer using a standard Socket. Once connected,
 * we will listen for messages from the Dealer. New decks are delivered from the DealerServer
 * to this PlayerClient via a JSON Document. Once received, we will parse the JSON document
 * to a Deck object and then redraw the deck on our window.
 *
 * This application also has the ability to request a SHUFFLE of the cards. When the
 * SHUFFLE request is sent to the Dealer, the Dealer will re-shuffle the deck and send
 * a new JSON representation of the deck for processing and redrawing on our view.
 */
public class PlayerClient extends JPanel {

    /**
     * The host and port of the Dealer Server
     */
    private final int DEALER_PORT    = 60451;
    private final String DEALER_HOST = "127.0.0.1";

    /**
     * The static key used to request a SHUFFLE from the server.
     */
    private final String KEY_SHUFFLE     = "SHUFFLE";
    private final String KEY_DISCONNECT  = "DISCONNECT";

    /**
     * Additional GUI variables
     */
    private final String KEY_GUI_NAME   = "Deck o' Cards";
    private final String KEY_TITLE_NAME = "52 Pickup!";

    /**
     * Streams for handling input and output.
     */
    BufferedReader in;
    PrintWriter out;
    String currentLine;

    /**
     * GUI Controls
     */
    JFrame frame = new JFrame(KEY_GUI_NAME);
    JButton btnShuffle = new JButton("Shuffle");
    JButton btnQuit = new JButton("Quit");
    JPanel panelCard = new JPanel();
    JPanel panelControl = new JPanel();
    JPanel panelTitle = new JPanel();
    JLabel labelTitle = new JLabel();
    JLabel labelTitlePic = new JLabel(new ImageIcon(getClass().getResource("model/assets/Proscape.png")));


    /**
     * Constructor to create the screen
     */
    public PlayerClient() {
        // Set background colors
        panelTitle.setBackground(new Color(255,255,255));
        panelCard.setBackground(new Color(255,180,80));
        panelControl.setBackground(new Color(255,180,80));

        // Set layouts
        FlowLayout layoutCard = new FlowLayout();
        FlowLayout layoutControl = new FlowLayout();
        layoutCard.setAlignment(FlowLayout.LEFT);
        layoutControl.setAlignment(FlowLayout.RIGHT);
        panelCard.setLayout(layoutCard);
        panelControl.setLayout(layoutControl);
        panelTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add components
        panelControl.add(btnShuffle);
        panelControl.add(btnQuit);
        labelTitle.setText(KEY_TITLE_NAME);
        panelTitle.add(labelTitlePic);
        panelTitle.add(labelTitle);

        // Add a listener to the frame to process close requests.
        // Ask the user if they really want to exit, and if so, process the
        // close gracefully.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (verifyClose()) {
                    processDisconnect();
                }
            }
        });

        // Add a listener for a button press on the Shuffle button
        btnShuffle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("Processing a shuffle request");
                processShuffleRequest();
            }
        });

        // Add a listener for a button press on the Quit button.
        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verifyClose()) {
                    log("Processing disconnect");
                    processDisconnect();
                }
            }
        });

        // Add our panels to this window.
        setLayout(new BorderLayout());
        add(panelTitle, BorderLayout.NORTH);
        add(panelCard, BorderLayout.CENTER);
        add(panelControl, BorderLayout.SOUTH);
    } // END PlayerClient()


    /**
     * Loads all the card images in the provided list to the panel.
     * @param cards
     */
    private void loadCards(final ArrayList<Card> cards) {
        // First, we want to clear the current cards.
        clearCards();

        // Now we load them on the panel.
        for (Card card : cards) {
            JLabel jImage = new JLabel(card.getCardImage());
            panelCard.add(jImage);
        }

        // Redraw our panel
        panelCard.validate();
        panelCard.repaint();
    } // END loadCards()


    /**
     * Clears all the cards in the card panel so we can throw down
     * a newly shuffled deck.
     */
    private void clearCards() {
        panelCard.removeAll();
    } // END clearCards()


    /**
     * Display our Player window.
     */
    private void display() {
        // Create and set up the window
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(this);
        frame.setPreferredSize(new Dimension(1030,550));

        // Display the window
        frame.pack();
        frame.setVisible(true);
    } // END display()


    /**
     * Sends a SHUFFLE request to the DealerServer.
     */
    private void processShuffleRequest() {
        try {
            out.println(KEY_SHUFFLE);
        } catch (Exception e) {
            log("Exception, unable to send Shuffle request: " + e.getMessage());
            e.printStackTrace();
        }
    } // END processShuffleRequest()


    /**
     * Sends a DISCONNECT message to the DealerServer, and then close this
     * window, and stop the program.
     */
    private void processDisconnect() {
        try {
            out.println(KEY_DISCONNECT);
            System.exit(0); // Shuts down the application.
        } catch (Exception e) {
            log("Exception, unable to send Disconnect request: " + e.getMessage());
            e.printStackTrace();
        }
    } // END processDisconnect()


    /**
     *
     * @return
     */
    private boolean verifyClose() {
        return JOptionPane.showConfirmDialog(frame,
                "Are you sure you really want to close this window?",
                "Really Closing?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    } // END verifyClose()


    /**
     * Shortcut for printing message to a terminal. In a production
     * environment, we would have an asynchronous logging process in use.
     * @param msg
     */
    public void log(String msg) {
        System.out.println(msg);
    } // END log()


    /**
     * The guts of this application. Here we connect to the dealer server,
     * initialize our streams, and then listen for messages from the dealer.
     *
     * If a message is received from the Dealer, we process it into a deck
     * object to display.
     *
     * @throws IOException We just want this to bubble up so if we cannot
     * connect to the Dealer Server, we stop.
     */
    private void run() throws IOException {
        // Connect to the dealer server and initialize the streams
        Socket socket = new Socket(DEALER_HOST, DEALER_PORT);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        out = new PrintWriter(socket.getOutputStream(),true);

        // Process all messages from server
        while ((currentLine = in.readLine()) != null) {
            log("Message from dealer: " + currentLine);

            // We have a new message from the dealer, most likely this
            // is a new deck of cards to display. If not, we will handle
            // gracefully by dropping out of a try.
            try {
                Gson gson = new Gson();
                Deck deck = gson.fromJson(currentLine, Deck.class);

                // If the deck is not null and is valid, lets go ahead and load
                // the deck into our view.
                if (deck != null && deck.getCardCount() > 0) {
                    log("Looks like our deck is ready!");
                    loadCards(deck.getCards());
                } else {
                    log("We were unable to load our deck of cards from the dealer message!");
                }
            } catch (Exception e) {
                log("Error de-serializing json to Deck class: " + e.getMessage());
                e.printStackTrace();
            }
        }
    } // END run()


    /**
     * Runs the client as an application with a closeable frame.
     * @param args
     */
    public static void main(String[] args) throws Exception {
        PlayerClient player = new PlayerClient();
        player.display();
        player.run();
    } // END main()
}
