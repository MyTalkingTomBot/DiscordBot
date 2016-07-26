package com.discord.discordbot.eventhandlers;

import com.discord.discordbot.TestBot;
import java.io.IOException;
import java.util.*;
import org.slf4j.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.handle.obj.Status.StatusType;

/**
 * Simple class that extends Utility and implements the package interface,
 * used to select a random game and then set it as the bots played game.
 * @author Alex
 */
public class RanGame extends Utility implements EventHandlerInterface{
    private ArrayList<String>gamesToPlay;
     private static final Logger log = LoggerFactory.getLogger(RanGame.class);
    
    @EventSubscriber
    public void onReady(ReadyEvent event){
        try{
        gamesToPlay = (ArrayList) readFile(".//src//main//resources//gamelist.txt");
        log.info("gamelist.text loaded");
        }
        catch(IOException e){
            log.warn("Could not access the file gamelist.txt in resources");
            e.getStackTrace();
        }
    }
    
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        String content = message.toString().toLowerCase();
        if((content.equals("!g")) || (content.equals("!game"))){
            processCommand(() -> onGame(event));
        }   
        
    }
    
    /*
    Once the !g command is executred this method is ran which wll pick a 
    random game from the list and display it as the bots name along with in chat
    */
    private void onGame(MessageReceivedEvent event){
        Random rand = new Random();
        int randNo = rand.nextInt(gamesToPlay.size());
        String game = gamesToPlay.get(randNo);
        
        printMessage(event,"Game to play: " + game);
        TestBot.client.changeGameStatus(game);
    }
    
    
}
