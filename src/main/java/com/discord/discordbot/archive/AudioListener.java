/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discord.discordbot.archive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.impl.events.AudioUnqueuedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * source used: https://gist.github.com/iabarca/dff8ee374bbbb3b30f28795a651f37b0#file-youtube-java-L26
 * altered and changed to work the way I want, well a little!
 * @author Alex
 */
public class AudioListener {

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String content = message.getContent();
        if (content.startsWith("!queue ")) {
            processCommand(() -> queueCommand(event));
        }
    }
    
    private void queueCommand(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        String content = message.getContent();
        IChannel channel = message.getChannel();
        
        if(channel.isPrivate()){
            System.out.println("does not work for a private message");
        }
        
        String url = content.trim().split(" ", 2)[1].trim();
        if(url.isEmpty()){
            System.out.println(url);
            System.out.println("url is empty");
        }
        
        try{
            AudioChannel audioChannel = message.getGuild().getAudioChannel();
            System.out.println("Testing url: " +url);
            
            if(queueFromYouTube(audioChannel,url)){
                IUser user = message.getAuthor();
                System.out.println(user.getName() + " added: " +url);
            }
        }
        catch(DiscordException e){
            System.out.println("Could not get audio channel");
            e.getStackTrace();
        }
        
        
    }
    
    private boolean queueFromYouTube(final AudioChannel audioChannel, final String id){
        String name = System.getProperty("os.name").contains("Windows") ? "youtube-dl.exe" : "youtube-dl";
        ProcessBuilder builder = new ProcessBuilder(name, "-q", "-f", "worstaudio",
        "--exec", "ffmpeg -hide_banner -nostats -loglevel panic -y -i {} -vn -q:a 5 -f mp3 pipe:1",
        "-o", "%(id)s", "--", id);
        try{
            Process process = builder.start();
            try{
                CompletableFuture.runAsync(() -> System.out.println(process.getErrorStream()));
                audioChannel.queue(AudioSystem.getAudioInputStream(process.getInputStream()));
                return true;
            }
            catch(UnsupportedAudioFileException e){
                e.getStackTrace();
                System.out.println("could not queue audio");
                process.destroyForcibly();
            }
        }
        catch(IOException e){
            e.getStackTrace();
            System.out.println("Could not start process");
        }
        return false;
    }

    public static CompletableFuture<Void> processCommand(Runnable runnable) {
        return CompletableFuture.runAsync(runnable)
                .exceptionally(t -> {
                    System.out.println("could not complete command");
                    return null;
                });
    }

        /*
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
         */
    }
