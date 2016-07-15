/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import com.discord.discordbot.TestBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

/**
 *
 * @author Alex
 */
public class OnHelp extends Utility {

    @EventSubscriber
    public void onHelp(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().startsWith("!help")) {
            String output = "The current commands are: \n"
                    + "----------------------**General Commands**---------------------------------\n"
                    + "!help - load this dialog \n"
                    + "!botnick: XXX - where XXX is the name you would like to change the name to \n"
                    + "!hello - a lovely welcome message \n"
                    + "!meme - pull a random statement \n"
                    + "!meme: XXX - where XXX is the value to add \n"
                    + "\n"
                    + "--------------------------**Discord Radio**------------------------------------\n"
                    + "!q youtube.com OR !queue youtube.com -  youtube.com URL of video to add \n"
                    + "!s OR !skip - skips the current song playing \n"
                    + "!v XX OR !value XX - sets the volume where XX is an int 0 -> 100\n"
                    + "\n"
                    + "---------------------**Discord Against Humanity**----------------------------\n"
                    + "*Implementation of Cards Against Humanity card game*\n"
                    + "*Play a Black card first followed by a white card*\n"
                    + "\n"
                    + "!white - pull a random white card from the deck \n"
                    + "!black - pull a random black card from the deck \n"
                    + "!cah - the bot will play a turn and pull a random black and then white card\n"
                    + "\n";

            try {
                new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                        .getChannel()).withContent(output).build();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}
