/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;

/**
 * \][e DiscordAgainstHumanity 0.5v all the basics have been added now and will
 * look to start the building of the game around the basic concepts of pulling
 * the cards. so far: !white - pulls a random white card !black - pulls a random
 * black card !cah - lets the bot play a turn (not implemented multiple spaces
 * to fill yet)
 *
 * @author Alex
 */
public class DiscordAgainstHumanity extends Utility implements EventHandlerInterface{

    private static final Logger log = LoggerFactory.getLogger(DiscordAgainstHumanity.class);
    //cards to load into the game
    private static ArrayList<String> whiteCards; //loaded in white cards
    private static ArrayList<String> blackCards; //loaded in black cards
    private static ArrayList<String> cardFilesWhite; //ArrayLists to pull the files and load white cards
    private static ArrayList<String> cardFilesBlack; //ArrayList to pull the files and load black cards
    private static final String CARD_LOC = ".\\src\\main\\resources\\discord_against_humanity\\"; //location dir of card files

    //map to hold the users cards
    private static Map<String, String[]> usersCards = new HashMap<>();

    //round vars
    private static Map<String, String> selectedRoundCards = new HashMap<>();
    private String usersPlaying = "";

    //game logic
    private static boolean gameStart = true;
    private static boolean readyPeriod = true;
    private Timer timer = new Timer();

    /**
     * Once the bot loads up all the card files are loaded into arrayLists
     *
     * @param event
     */
    @EventSubscriber
    public void onReady(ReadyEvent event) {
        try {
            loadCards();
            log.info("All cards loaded!");
        } catch (Exception e) {
            log.warn("Error whilst reading file", e);
        }
    }

    /**
     * Main control class for the card pulling will act on the user events, main
     * event tree
     *
     * @param event
     */
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String content = message.getContent().toLowerCase();

        //print white card out(cards with a noun on it, generally)
        if (content.equals("!white")) {
            processCommand(() -> onWhite(event));
            //prints a black card (cards with statements, questions)
        } else if (content.equals("!black")) {
            processCommand(() -> onBlack(event));
            //will print out a random black card followed by white    
        } else if (content.equals("!cah")) {
            processCommand(() -> onCAH(event));
            //starts the game
        } else if ((content.equals("!cardstart"))) {
            if (gameStart == false) {
                processCommand(() -> onCardStart(event));
            } else {
                printMessage(event, "Sorry game is currently in progress"); //if game is in progress
            }
            //ready up command to start the game for each player and sends them cards
        } else if ((content.equals("!r")) || (content.equals("!ready"))) {
            if (readyPeriod == true) {
                processCommand(() -> onUserReady(event));
            } else {
                printMessage(event, "Sorry ready period has ended!");
            }
            //This is to send your chosen card to the bot
        } else if ((content.startsWith("!c")) || (content.startsWith("!card"))) {
            if (gameStart == true) {
                processCommand(() -> onCardSelect(event));
            } else {
                printMessage(event, "Sorry you cannot select a card now!");
            }
        }
    }

    // method to handle the selecting and pulling a random white card
    private void onWhite(MessageReceivedEvent event) {
        Random rand = new Random();
        int ranNo = rand.nextInt(whiteCards.size());
        try {
            printMessage(event, whiteCards.get(ranNo));
        } catch (Exception e) {
            log.warn("Error when sending a message with white ", e);
        }
    }

    //method to handle the selecting and pulling of a random black card
    private void onBlack(MessageReceivedEvent event) {
        Random rand = new Random();
        int ranNo = rand.nextInt(blackCards.size());
        try {
            printMessage(event, blackCards.get(ranNo));
        } catch (Exception e) {
            log.warn("Error when sending a message with blackCards ", e);
        }
    }

    //method to pull a random black card followed by a white card
    private void onCAH(MessageReceivedEvent event) {
        Random rand = new Random();
        int ranNo = rand.nextInt(whiteCards.size());
        int ranNo1 = rand.nextInt(blackCards.size());
        try {
            printMessage(event, blackCards.get(ranNo1) + "\n" + whiteCards.get(ranNo));
        } catch (Exception e) {
            log.warn("Error when compiling bot play onCAH", e);
        }
    }

    //this method will pull the cards from the dir and load them into the two arrayLists
    private static void loadCards() {
        cardFilesWhite = new ArrayList<>();
        cardFilesBlack = new ArrayList<>();
        File folder = new File(CARD_LOC);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if ((listOfFiles[i].isFile()) && (listOfFiles[i].getName().startsWith("White"))) {
                cardFilesWhite.add(listOfFiles[i].getName());
            }
            if ((listOfFiles[i].isFile()) && (listOfFiles[i].getName().startsWith("Black"))) {
                cardFilesBlack.add(listOfFiles[i].getName());
            }
        }
        whiteCards = (ArrayList) openCardFiles(cardFilesWhite);
        blackCards = (ArrayList) openCardFiles(cardFilesBlack);
    }

    //IO method used to read the data from the files and push them
    private static Collection openCardFiles(Collection cardFile) {
        ArrayList<String> cardList = new ArrayList<>();

        for (Object x : cardFile) {
            File inFile = new File(CARD_LOC + x);
            BufferedReader bin = null;
            String fileReading = "";
            String[] readLines;
            try {
                FileReader fin = new FileReader(inFile);
                bin = new BufferedReader(fin);
                String line = bin.readLine();

                while (line != null) {
                    fileReading += line + "\n";
                    line = bin.readLine();
                }
                readLines = fileReading.split("\n");
                for (String y : readLines) {
                    cardList.add(y);
                }
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(DiscordAgainstHumanity.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.info("Loaded " + x);
        }

        return cardList;
    }
    /*
     GAME LOGIC BELOW------------------------------------------------------>>
     */

    /**
     * the inital method used once the game begins
     *
     * @param event
     */
    private void onCardStart(MessageReceivedEvent event) {
        gameStart = true;
        printMessage(event, "Discord Against Humanity loading...\n"
                + "all players press !r"
                + "You have 30 seconds...");

        timer.schedule(new TaskOver() {
            @Override
            public void run() {
                readyPeriod = true;

            }
        }, 1 * 30 * 1000);

    }
    /*
    !r || !ready will inital the game commands
    checks the game has begun and will push the cards to the user so they get
    their starting hand
     */

    private void onUserReady(MessageReceivedEvent event) {
        if (gameStart == true) { //check game has started
            String userHandOutput = "";
            String getUserId = event.getMessage().getAuthor().getName();
            log.info("userId: " + getUserId);

            setUsersPlaying(event); //adds user to the list for later
            String[] temp = userHand(); //generates the users starting hand

            //checks to see if the user has already requested cards
            if (!usersCards.containsKey(getUserId)) {
                usersCards.put(getUserId, temp);
            }
            //will output the 10 cards they get in their starting hand
            String[] mapReturn = usersCards.get(getUserId);
            for (int i = 0; i < 10; i++) {
                userHandOutput += "[" + (i + 1) + "]" + mapReturn[i] + "\n";
            }
            log.debug("String userHand declared");
            //content to whisper to the user
            String content = (getUserId + " "
                    + "your cards are: \n"
                    + userHandOutput);
            //the whisper command that will send all the details privately to the user
            whisperMessage(event, content);
            //once games starts send message
        } else {
            printMessage(event, "Start the game already! try !cardStart");
        }
    }

    //!c <int> || !card <int> to select the card
    private void onCardSelect(MessageReceivedEvent event) {
        String[] cardMsg = (event.getMessage().getContent()).split(" ");
        String[] cards;
        int cardNo = 0;
        String getUserId = event.getMessage().getAuthor().getName();

        //changes the string into Int to read card number
        try {
            cardNo = Integer.parseInt(cardMsg[1]);
        } catch (Exception e) {
            log.warn("Error user did not enter an integer value");
            whisperMessage(event, "Error you must enter an integer value");
        }
        //check the number is valid for the array
        if ((cardNo > 0) && (cardNo < 11)) {
            cardNo--; //added to compensate for telling user values 1 - 10 inclusive

            cards = usersCards.get(getUserId); //returns stored cards from the map
            String cardReturn = cards[cardNo]; //saves the old card string
            String newCard = "";

            //checks to see if the user has played this round if they haven't will accept
            if (!selectedRoundCards.containsKey(getUserId)) {
                selectedRoundCards.put(getUserId, cardReturn); //stores the selected card for round scoring
                System.out.println(selectedRoundCards.get(getUserId));
                newCard = newCard(cardNo, getUserId); //replaces the selected card
            } else {
                newCard = selectedRoundCards.get(getUserId) + " [previously selected] ";
            }

            //output whisper with card selection.
            whisperMessage(event, getUserId + " selected:"
                    + " card number: " + (cardNo + 1) + ": " + cardReturn + "\n"
                    + "New card is: " + "[" + (cardNo + 1) + "] " + newCard + "\n"
                    + getUsersHand(event, getUserId));
            cards = null;
        } else {
            whisperMessage(event, "Array not inilitised");
        }

    }
    /* 
    method used to give the user a new card
    requires a card number to be replaced whilst it adds the card to the hand
    */
    private String newCard(int cardNo, String getUserId) {
        String[] hand = usersCards.get(getUserId);
        //random number generation
        Random rand = new Random();
        int ranNo = rand.nextInt(whiteCards.size());
        //replacement logic
        hand[cardNo] = whiteCards.get(ranNo); //get a new random white card
        usersCards.put(getUserId, hand); //add it to the users hand
        String output = whiteCards.get(ranNo); //output the value
        whiteCards.remove(ranNo); //remove from deck
        
        //returns the new card string
        return output;
    }
    //pulls the Array out from the map and displays the current cards
    private String getUsersHand(MessageReceivedEvent event, String getUserId) {
        String[] hand = usersCards.get(getUserId);
        String output = "";
        //loop to display the cards from the hand
        for (int i = 0; i < hand.length; i++) {
            output += "[" + (i + 1) + "]" + hand[i] + "\n";
        }
        //outputs the cards on different lines, totalling 10
        return output;
    }
    //Method that generates the user hand by taking cards from the ArrayLists
    private String[] userHand() {
        String[] userHand = new String[10]; //output Array always 10 cards

        Random rand = new Random(); //random number

        /*
        main iteration loop that will pull random cards and add them to the array
        before removed them from the deck, output an Array
        */
        for (int i = 0; i < userHand.length; i++) {
            int ranNo = rand.nextInt(whiteCards.size());
            userHand[i] = whiteCards.get(ranNo);
            whiteCards.remove(ranNo);
        }

        return userHand; //String[]Array
    }
    
    /*
    Method that will random select a CardCzar, providing not Czar last turn
    maybe will be best to just get all users put them in an order and keep refreshing the order
    
    */
    private String CardCzar() {
        String output = "";

        return output;
    }
    //timer class that doesn't work currently
    class TaskOver extends TimerTask {

        public void run() {
            System.out.format("30 seconds over, Registration is over");
            gameStart = false;
            timer.cancel(); // Terminate the timer thread
        }
    }
    //String output of all users playing, username are seperated by \n
    public void setUsersPlaying(MessageReceivedEvent event) {
        String user = event.getMessage().getAuthor().getName();
        if (!usersPlaying.contains(user)) {
            usersPlaying += user + "\n";
        } else {
            log.warn("Error user is already added!");
            System.err.println(user + "already added \n" + usersPlaying); //debug
        }
    }

}
