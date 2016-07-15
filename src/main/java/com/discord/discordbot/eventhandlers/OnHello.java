/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 *
 * @author Alex
 */
public class OnHello extends Utility{
    
    @EventSubscriber
    public void onHello(MessageReceivedEvent event){
        if(event.getMessage().getContent().toLowerCase().startsWith("!hello")){
            printMessage(event,"Hello there " +event.getMessage().getAuthor().getName());
        }
    }
}
