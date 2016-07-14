/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot;

import java.io.IOException;
import java.util.ArrayList;
import sx.blah.discord.api.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RateLimitException;

/**
 * Basic bot to test out some things in the api
 * @author Alex
 */
public class TestBot {
    static IDiscordClient client;
    
    public TestBot(){
        
    }
    
    public static void main(String[]args){
        try{
            ArrayList<String>loginToken = (ArrayList<String>) InputOutput.ReadFile("C:\\Users\\Alex\\Desktop\\discord bot log.txt");

            client = new ClientBuilder().withToken(loginToken.get(0)).login();
            client.getDispatcher().registerListener(new ReadyListener());
            client.getDispatcher().registerListener(new AnnotationListener());
        }catch(Exception e){
            System.err.println(e);
        }
 

    }
}
    
    

