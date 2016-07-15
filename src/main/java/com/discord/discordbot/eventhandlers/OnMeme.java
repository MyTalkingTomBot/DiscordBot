/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import com.discord.discordbot.archive.InputOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;

/**
 *
 * @author Alex
 */
public class OnMeme extends Utility {

    private static final Logger log = LoggerFactory.getLogger(OnMeme.class);
    private final String MEME_TXT = ".//src//main//resources//memes.txt";
    private static ArrayList<String> memes;
    private boolean memesLoaded = false;
    private int memeStartSize;
    private int memeNewAdd = 0;

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        try {
            memes = (ArrayList) Utility.ReadFile(MEME_TXT);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (memes != null) {
            log.info("Meme.txt loaded successfully");
            memesLoaded = true;
            memeStartSize = memes.size();
        } else {
            log.warn("Error loading meme.txt IO issue");
        }
    }

    @EventSubscriber
    public void onMemes(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().equals("!meme")) {
            Random rand = new Random();
            int ranNo = rand.nextInt(memes.size());

            try {
                printMessage(event, memes.get(ranNo));
            } catch (Exception e) {
                log.warn("error in generating random meme!");
            }
        }
    }

    @EventSubscriber
    public void onMemesAdd(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().startsWith("!meme ")) {
            String meme = (event.getMessage().getContent().substring(5));
            meme = meme.trim();
            memes.add(meme);

            try {
                printMessage(event, "I'll save that for later...");
                memeNewAdd++;
                log.info("new meme added: " + meme);
                if (memeNewAdd >= 5) {
                    saveAMeme();
                }
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

    //Private methods live here!
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
