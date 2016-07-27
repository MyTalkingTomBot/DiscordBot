/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import java.util.*;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import twitter4j.*;
import org.slf4j.Logger.*;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Alex
 */
public class TweetMe extends Utility implements EventHandlerInterface {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TweetMe.class);
    private ArrayList<Status> statuses;
    Twitter unauthenticatedTwitter = new TwitterFactory().getInstance();

    @Override
    public void onReady(ReadyEvent event) {

    }

    @Override
    public void onMessage(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String content = message.toString().toLowerCase();
        if ((content.startsWith("!trump")) || (content.equals("!t"))) {
            processCommand(() -> onTrump(event));
        }

    }

    private void onTrump(MessageReceivedEvent event) {
        Paging paging = new Paging(1, 10);
        try {
            statuses = (ArrayList<Status>) unauthenticatedTwitter.getUserTimeline("google", paging);
            log.info("ArrayList created size: " + statuses.size());
            printMessage(event,statuses.toString());
        } catch (TwitterException e) {
            log.warn("Error could not intitate twitter onTrump");
            System.err.println(e.getMessage() +"\n" + e.getStackTrace());
            
        }
    }

}
