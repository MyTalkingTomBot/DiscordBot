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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * contains a lot of useful methods for the event classes
 *
 * @author Alex
 */
public abstract class Utility {
    


    protected void printMessage(MessageReceivedEvent event, String content) {
        try {
            new MessageBuilder(TestBot.client).withChannel(event.getMessage()
                    .getChannel()).withContent(content).build();
        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
            e.getStackTrace();
        }
    }

    public static Collection ReadFile(String filename) throws IOException {
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
}
