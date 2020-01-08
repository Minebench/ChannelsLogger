package de.minebench.channelslogger;

import java.io.IOException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.events.ChannelsChatEvent;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.PrivateMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChannelsLogger extends Plugin implements Listener {

    private FileConfiguration config;

    private Logger logger = LogManager.getLogger("ChannelsLogger.ChatLogger");

    private boolean enabled = false;
    private boolean logPrivate = false;
    private boolean logPrefixes = false;

    public void onEnable() {
        try {
            config = new FileConfiguration(this, "config.yml");
            enabled = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logPrivate = config.getBoolean("logPrivate");
        logPrefixes = config.getBoolean("logPrefixes");

        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onChannelsMessage(ChannelsChatEvent event) {
        if (!enabled)
            return;

        if (event.getMessage() instanceof ChannelMessage){
            ChannelMessage cm = (ChannelMessage) event.getMessage();
            logger.info("[" + cm.getChannel().getTag() + "] " + (logPrefixes ? ChatColor.stripColor(cm.getChatter().getPrefix()) : "") + cm.getChatter().getName() + ": " + cm.getRawMessage());
        } else if (event.getMessage() instanceof PrivateMessage) {
            if (logPrivate) {
                PrivateMessage pm = (PrivateMessage) event.getMessage();
                logger.info(pm.getChatter().getName() + " -> " + pm.getReceiver().getName() + ": " + pm.getRawMessage());
            }
        } else if (event.getMessage() instanceof ConsoleMessage){
            ConsoleMessage cm = (ConsoleMessage) event.getMessage();
            logger.info("[" + cm.getChannel().getTag() + "] " + cm.getSender().getName() + ": " + cm.getRawMessage());
        }
    }

}
