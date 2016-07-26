/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot;

import com.discord.discordbot.eventhandlers.YouTube;
import com.discord.discordbot.archive.InputOutput;
import java.io.IOException;
import java.util.ArrayList;
import sx.blah.discord.api.*;
import com.discord.discordbot.eventhandlers.*;
/**
 * Basic bot to test out some things in the api
 * @author Alex
 */
public class TestBot {
    public static IDiscordClient client;
    
    public TestBot(){
        
    }
    
    public static void main(String[]args){
        try{
            ArrayList<String>loginToken = (ArrayList<String>) InputOutput.ReadFile("C:\\Users\\Alex\\Desktop\\discord bot log.txt");

            client = new ClientBuilder().withToken(loginToken.get(0)).login();
            client.getDispatcher().registerListener(new OnReady());
            client.getDispatcher().registerListener(new OnHelp());
            client.getDispatcher().registerListener(new OnBotNick());
            //client.getDispatcher().registerListener(new DiscordRadio());
            client.getDispatcher().registerListener(new OnHello());
            client.getDispatcher().registerListener(new OnMeme());
            client.getDispatcher().registerListener(new SaveActiveUsers());
            client.getDispatcher().registerListener(new YouTube());
            client.getDispatcher().registerListener(new RanGame());
            client.getDispatcher().registerListener(new DiscordAgainstHumanity());
        }catch(Exception e){
            System.err.println(e);
        }
 

    }
}
    
    

