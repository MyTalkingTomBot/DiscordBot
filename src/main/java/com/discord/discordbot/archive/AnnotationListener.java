/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.archive;

import com.discord.discordbot.TestBot;
import java.io.IOException;
import java.util.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Main events class for the TestBot
 *
 * @author Alex
 */
public class AnnotationListener {

    private String messageCmd = "";
    private final String MEME_TXT = ".//src//main//resources//memes.txt";
    private static ArrayList<String> memes;
    private boolean memesLoaded = false;
    private int memeStartSize;
    private int memeNewAdd = 0;

    /**
     * Once the bot logs in this will occur
     *
     * @param event
     */
    @EventSubscriber
    public void onReady(ReadyEvent event) {
        try {
            memes = (ArrayList) InputOutput.ReadFile(MEME_TXT);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (memes != null) {
            System.out.println("ArrayList for Memes loaded successfully!");
            memesLoaded = true;
            memeStartSize = memes.size();
        }
    }

    /**
     * if !hello is typed this will occur
     *
     * @param event
     */
    @EventSubscriber
    public void onHello(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains("!hello")) {
            event.getClient().getDispatcher().dispatch(new TestEvent(event.getMessage()));
            System.out.println(event.getMessage().getAuthor().getName() + " said hello!");
            try {
                new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                        .getChannel()).withContent("Hello there "
                                + event.getMessage().getAuthor().getName()).build();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    /**
     * if the user types !botnick: followed by a string it will change the bots
     * name
     *
     * @param event
     */
    @EventSubscriber
    public void onBotNick(MessageReceivedEvent event) {
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

    /**
     *
     * @param event
     */
    @EventSubscriber
    public void onMemesAdd(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().startsWith("!meme ")) {
            messageCmd = (event.getMessage().getContent().substring(6));
            messageCmd = messageCmd.trim();
            memes.add(messageCmd);

            try {
                new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                        .getChannel()).withContent("I'll save that for later...").build();
                memeNewAdd++;
                System.out.println(memeNewAdd);
                if (memeNewAdd >= 5) {
                    saveAMeme();
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    @EventSubscriber
    public void onMemes(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().equals("!meme")) {
            Random rand = new Random();
            int ranN = rand.nextInt(memes.size());

            try {
                new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                        .getChannel()).withContent(memes.get(ranN)).build();
            } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                e.getStackTrace();
            }
        }
    }

    @EventSubscriber
    public void onJukeBox(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().startsWith("!jukebox")) {
            IGuild guild = TestBot.client.getGuilds().get(0);
            IVoiceChannel voiceChannel = guild.getVoiceChannels().get(0);
            System.out.println(voiceChannel.getID());

            try {
                voiceChannel.join();

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    @EventSubscriber
    public void onSaveMemes(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().equals("!savememes")) {
            saveAMeme();
        }
    }

    @EventSubscriber
    public void onHelp(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().startsWith("!help")) {
            String output = "The current commands are: \n"
                    + "!help - load this dialog \n"
                    + "!botnick: XXX - where XXX is the name you would like to change the name to \n"
                    + "!hello - a lovely welcome message \n"
                    + "!meme - pull a random statement \n"
                    + "!meme: XXX - where XXX is the value to add \n";

            try {
                new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                        .getChannel()).withContent(output).build();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void saveAMeme() {
        String[] outMeme = new String[memes.size()];
        for (int i = 0; i < outMeme.length; i++) {
            outMeme[i] = memes.get(i);
            System.out.println(outMeme[i]);
        }
        InputOutput.writeToFile(MEME_TXT, outMeme);
        memeNewAdd = 0;
        System.out.println("New memes added to the file");
    }

}
