/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Alex
 */
public class TestEvent extends Event{
    private final IMessage message;
    
    public TestEvent(IMessage message){
        this.message = message;
    }
    
    public IMessage getMessage(){
        return message;
    }
}
