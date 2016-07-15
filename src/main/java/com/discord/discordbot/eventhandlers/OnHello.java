/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import com.discord.discordbot.TestBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 *
 * @author Alex
 */
public class OnHello extends Utility{
    /**
     * Simple event class that will return to the user hello there and their name.
     * @param event when the message starts with !hello will call.
     */
    @EventSubscriber
    public void onHello(MessageReceivedEvent event){
        if(event.getMessage().getContent().toLowerCase().startsWith("!hello")){
            printMessage(event,"Hello there " +event.getMessage().getAuthor().getName());
        }
    }
    
    /**
     * proof of concept build for the whisper to a user from the bot will open a 
     * new channel and whisper to the user.
     * @param event When !whello is typed this will cause the bot to send the message
     */
    @EventSubscriber
    public void onWhispHello(MessageReceivedEvent event){
        if(event.getMessage().getContent().toLowerCase().equals("!whello")){
            try{
            IPrivateChannel channel = TestBot.client.getOrCreatePMChannel(TestBot.client.getUserByID(event.getMessage().getAuthor().getID()));
            channel.sendMessage("Whispering sweet nothing in " + event.getMessage().getAuthor().getName() +" ear...");
            }
            catch(DiscordException | RateLimitException | MissingPermissionsException e){
                e.getStackTrace();
            }
        }
    }
}
