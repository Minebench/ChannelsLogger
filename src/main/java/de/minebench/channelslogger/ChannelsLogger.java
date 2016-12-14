package de.minebench.channelslogger;

import java.io.IOException;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.ChannelsChatEvent;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.PrivateMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class ChannelsLogger extends Plugin implements Listener {

	private FileConfiguration config;

    private Logger logger = Logger.getLogger("chat");

	private boolean enabled = false;
	private boolean logPrivate = false;

	public void onEnable() {
		try {
			config = new FileConfiguration(this, "config.yml");
			enabled = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		logPrivate = config.getBoolean("logPrivate");
        DOMConfigurator.configure(getClass().getResource("/logger.xml"));

		getProxy().getPluginManager().registerListener(this, this);
	}

	@EventHandler
    public void onChannelsMessage(ChannelsChatEvent event) {
        if (!enabled)
            return;

        if (event.getMessage() instanceof ChannelMessage){
            ChannelMessage cm = (ChannelMessage) event.getMessage();
            logger.info("[" + cm.getChannel().getTag() + "] " + cm.getChatter().getPrefix() + cm.getChatter().getName() + ": " + cm.getRawMessage());
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
