/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.eventhandlers;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import com.discord.discordbot.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

/**
 * contains a lot of useful methods for the event classes
 *
 * @author Alex
 */
public abstract class Utility {

   // private static final Logger log = LoggerFactory.getLogger(Utility.class);
    private Timer timer;

    public static Collection readFile(String filename) throws IOException {
        File inFile = new File(filename);
        BufferedReader bin = null;
        String fileReading = "";
        String[] readLines;
        ArrayList<String> outputCol = new ArrayList<>();

        try {
            FileReader fin = new FileReader(inFile);
            bin = new BufferedReader(fin);
            String line = bin.readLine();
            while (line != null) {
                fileReading += line + "\n";

                line = bin.readLine();
            }
            readLines = fileReading.split("\n");

            for (String x : readLines) {
                outputCol.add(x);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());

        } finally {
            if (bin != null) {
                bin.close();
            }
        }
        return outputCol;
    }

    public static void writeToFile(String filename, String[] output) {
        PrintWriter out = null;

        try {
            File outFile = new File(filename);

            FileWriter fout = new FileWriter(outFile);
            BufferedWriter bout = new BufferedWriter(fout);
            out = new PrintWriter(bout);

            for (String x : output) {
                out.println(x);
            }
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void printMessage(MessageReceivedEvent event, String content) {
        try {
            new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                    .getChannel()).withContent(content).build();
        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
            e.getStackTrace();
        }
    }

    protected void whisperMessage(MessageReceivedEvent event, String content) {

        try {
            IPrivateChannel channel = TestBot.client.getOrCreatePMChannel(TestBot.client.getUserByID(event.getMessage().getAuthor().getID()));
            channel.sendMessage(content);
        } catch (DiscordException | RateLimitException | MissingPermissionsException e) {
            e.getStackTrace();
        }
    }

    protected void whisperMessage(String userID, String content) {

        try {
            IPrivateChannel channel = TestBot.client.getOrCreatePMChannel(TestBot.client.getUserByID(userID));
            channel.sendMessage(content);
        } catch (DiscordException | RateLimitException | MissingPermissionsException e) {
            e.getStackTrace();
        }
    }

    /**
     * simple method to remove messages
     *
     * @param message
     */
    public static void deleteMessage(IMessage message) {
        RequestBuffer.request(() -> {
            try {
                message.delete();
            } catch (MissingPermissionsException | DiscordException e) {
                //log.warn("Failed to delete message", e);
            }
            return null;
        });
    }

    /**
     * CompletableFuture is an Async method that will carry out the methods
     * pushed in a logical stack to prevent errors occuring. requires J8
     *
     * @param runnable
     * @return
     */
    public static CompletableFuture<Void> processCommand(Runnable runnable) {
        return CompletableFuture.runAsync(runnable)
                .exceptionally(t -> {
                    //log.warn("Could not complete command", t);
                    return null;
                });
    }
}
