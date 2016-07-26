/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;

/**
 *
 * @author Alex
 */
public interface EventHandlerInterface {
    
        
    @EventSubscriber
    public void onReady(ReadyEvent event);
    
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event);
    
    
}
