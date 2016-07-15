/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 *DiscordAgainstHumanity 0.5v all the basics have been added now and will look to start
 * the building of the game around the basic concepts of pulling the cards. so far:
 * !white - pulls a random white card
 * !black - pulls a random black card
 * !cah - lets the bot play a turn (not implemented multiple spaces to fill yet)
 * @author Alex
 */
public class DiscordAgainstHumanity extends Utility {
    
    private static final Logger log = LoggerFactory.getLogger(DiscordAgainstHumanity.class);
    
    private static ArrayList<String> whiteCards;
    private static ArrayList<String> blackCards;
    private static ArrayList<String> cardFilesWhite;
    private static ArrayList<String> cardFilesBlack;
    private static final String CARD_LOC = ".\\src\\main\\resources\\discord_against_humanity\\";

    /**
     * Once the bot loads up all the card files are loaded into
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
        if (content.equals("!white")) {
            processCommand(() -> onWhite(event));
        } else if (content.equals("!black")) {
            processCommand(() -> onBlack(event));
        } else if (content.equals("!cah")) {
            processCommand(() -> onCAH(event));
        }
    }
    
    private void onWhite(MessageReceivedEvent event) {
        Random rand = new Random();
        int ranNo = rand.nextInt(whiteCards.size());
        try {
            printMessage(event, whiteCards.get(ranNo));
        } catch (Exception e) {
            log.warn("Error when sending a message with white ", e);
        }
    }
    
    private void onBlack(MessageReceivedEvent event) {
        Random rand = new Random();
        int ranNo = rand.nextInt(blackCards.size());
        try {
            printMessage(event, blackCards.get(ranNo));
        } catch (Exception e) {
            log.warn("Error when sending a message with blackCards ", e);
        }
    }
    
    private void onCAH(MessageReceivedEvent event) {
        Random rand = new Random();
        int ranNo = rand.nextInt(whiteCards.size());
        int ranNo1 = rand.nextInt(blackCards.size());
        try {
            printMessage(event,blackCards.get(ranNo1) + "\n" + whiteCards.get(ranNo));
        } catch (Exception e) {
            log.warn("Error when compiling bot play onCAH", e);
        }
    }
    
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
    
}
