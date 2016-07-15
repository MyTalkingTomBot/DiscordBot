/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import com.discord.discordbot.TestBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 *
 * @author Alex
 */
public class OnBotNick extends Utility{

    @EventSubscriber
    public void onBotNick(MessageReceivedEvent event) {
        String messageCmd ="";
        if (event.getMessage().getContent().toLowerCase().startsWith("!botnick:")) {
            messageCmd = (event.getMessage().getContent()).substring(9);
            messageCmd = messageCmd.replaceAll("\\s", "");
            System.out.println(messageCmd);

            try {
                TestBot.client.changeUsername(messageCmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
