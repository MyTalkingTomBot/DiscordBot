/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import java.util.ArrayList;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Alex
 */
public class SaveActiveUsers extends Utility implements EventHandlerInterface{
    public static ArrayList<String>userIDs = new ArrayList<>();
    
    
    @EventSubscriber
    private void activeUser(MessageReceivedEvent event){
        String user = event.getMessage().getAuthor().getID();
        if(!userIDs.contains(user)){
            userIDs.add(user);
        }
    }
    
    @EventSubscriber
    public void getUserIDs(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        if(event.getMessage().getContent().toLowerCase().equals("!users")){
            whisperMessage(event,userIDs.toString());
            deleteMessage(message);
            
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        
    }

    @Override
    public void onMessage(MessageReceivedEvent event) {
       
    }
    
}
