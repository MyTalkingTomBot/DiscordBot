/*
 * Original source file: https://gist.github.com/iabarca/dff8ee374bbbb3b30f28795a651f37b0#file-youtube-java-L26
 */
package com.discord.discordbot.eventhandlers;

import com.discord.discordbot.TestBot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.slf4j.*;
import sx.blah.discord.api.events.*;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

/**
 * source used:https://gist.github.com/iabarca/dff8ee374bbbb3b30f28795a651f37b0#file-youtube-java-L26
 * 
 * This is an eventlistener class for Discord4J that will add the bot to the first channel
 * and then allow users to add their own music via youtube.
 *
 * @author Alex
 */
public class DiscordRadio extends Utility {

    private static final Logger log = LoggerFactory.getLogger(DiscordRadio.class);

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        IGuild guild = TestBot.client.getGuilds().get(0);
        IVoiceChannel voiceChannel = guild.getVoiceChannels().get(0);
        System.out.println(voiceChannel.getID());

        try {
            voiceChannel.join();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * Main control structure for the radio that will listen for different
     *
     * @param event
     */
    @EventSubscriber
    public void onMessage(MessageReceivedEvent e) {
        IMessage message = e.getMessage();
        String content = message.getContent();
        if (content.startsWith("!q ") || content.startsWith("!queue ")) {
            processCommand(() -> queueCommand(e));
        } else if (content.equals("!s") || content.equals("!skip")) {
            processCommand(() -> skipCommand(e));
        } else if (content.startsWith("!v ") || content.startsWith("!volume ")) {
            processCommand(() -> volumeCommand(e));
        }
    }

    private void queueCommand(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String content = message.getContent();
        IChannel channel = message.getChannel();
        if (channel.isPrivate()) {
            sendMessage(channel, "This command does not work with private messages");
            return;
        }
        String url = content.trim().split(" ", 2)[1].trim();
        if (url.isEmpty()) {
            sendMessage(channel, "You have to enter a URL");
            return;
        }

        try {
            AudioChannel audioChannel = message.getGuild().getAudioChannel();
            log.debug("Preparing to process URL into queue: {}", url);
            if (queueFromYouTube(audioChannel, url)) {
                IUser user = message.getAuthor();
                sendMessage(channel, user.getName() + "#" + user.getDiscriminator() + " added to the playlist: <" + url + ">");
                //deleteMessage(message);
            }
        } catch (DiscordException e) {
            log.warn("Could not get audio channel", e);
            sendMessage(channel, "Could not get the audio channel for this server");
        }
    }

    private boolean queueFromYouTube(final AudioChannel audioChannel, final String id) {
        String name = System.getProperty("os.name").contains("Windows") ? "youtube-dl.exe" : "youtube-dl";
        ProcessBuilder builder = new ProcessBuilder(name, "-q", "-f", "worstaudio",
            "--exec", "ffmpeg -hide_banner -nostats -loglevel panic -y -i {} -vn -q:a 5 -f mp3 pipe:1", "-o",
            "%(id)s", "--", id);
        try {
            Process process = builder.start();
            try {
                CompletableFuture.runAsync(() -> logStream(process.getErrorStream()));
                audioChannel.queue(AudioSystem.getAudioInputStream(process.getInputStream()));
                return true;
            } catch (UnsupportedAudioFileException e) {
                log.warn("Could not queue audio", e);
                process.destroyForcibly();
            }
        } catch (IOException e) {
            log.warn("Could not start process", e);
        }
        return false;
    }

    private BufferedReader newProcessReader(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
    }

    private void logStream(InputStream stream) {
        try (BufferedReader input = newProcessReader(stream)) {
            String line;
            while ((line = input.readLine()) != null) {
                log.info("[yt-dl] " + line);
            }
        } catch (IOException e) {
            log.warn("Could not read from stream", e);
        }
    }

    private void volumeCommand(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String content = message.getContent();
        IChannel channel = message.getChannel();
        if (message.getChannel().isPrivate()) {
            return;
        }
        try {
            int volume = Math.max(0, Math.min(100, parseOrDefault(content.split(" ", 2)[1], 20)));
            AudioChannel audioChannel = message.getGuild().getAudioChannel();
            log.debug("Setting volume to {}% ({})", volume, volume / 100f);
            audioChannel.setVolume(volume / 100f);
            sendMessage(channel, ":ok_hand:");
        } catch (DiscordException e) {
            log.warn("Could not get audio channel", e);
            sendMessage(channel, "Could not get audio channel for this server");
        }
    }

    private void skipCommand(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        if (!message.getChannel().isPrivate()) {
            try {
                message.getGuild().getAudioChannel().skip();
            } catch (DiscordException e) {
                log.warn("Could not get audio channel", e);
            }
        }
    }

    @EventSubscriber
    public void onAudioDequeue(AudioUnqueuedEvent event) {
        AudioInputStream stream = event.getStream();
        try {
            // Needed to work around an issue present with Discord4J v2.4.x (it's fixed in v2.5)
            // that left certain streams open
            stream.close();
            log.debug("Stream {} was closed", Integer.toHexString(stream.hashCode()));
        } catch (IOException e) {
            log.warn("Could not close audio stream", e);
        }
    }

    private int parseOrDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            log.warn("Attempted to parse non-numeric value: {}", e.toString());
            return defaultValue;
        }
    }

    public static RequestBuffer.RequestFuture<IMessage> sendMessage(final IChannel channel, final String content) {
        return RequestBuffer.request(() -> {
            try {
                return channel.sendMessage(content);
            } catch (MissingPermissionsException | DiscordException ex) {
                log.warn("Could not send message", ex);
            }
            return null;
        });
    }

    public static void deleteMessage(IMessage message) {
        RequestBuffer.request(() -> {
            try {
                message.delete();
            } catch (MissingPermissionsException | DiscordException e) {
                log.warn("Failed to delete message", e);
            }
            return null;
        });
    }

    public static CompletableFuture<Void> processCommand(Runnable runnable) {
        return CompletableFuture.runAsync(runnable)
            .exceptionally(t -> {
                log.warn("Could not complete command", t);
                return null;
            });
    }
}