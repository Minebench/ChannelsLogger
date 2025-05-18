package de.minebench.channelslogger;

/*
 * Copyright (C) 2025 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.zaiyers.Channels.events.ChannelsChatEvent;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.PrivateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class ChannelsLogger {

    private final ProxyServer proxy;
    private final Logger logger;
    private FileConfiguration config;

    private final Logger chatLogger = LoggerFactory.getLogger("ChannelsLogger.ChatLogger");

    private boolean enabled = false;
    private boolean logPrivate = false;
    private boolean logPrefixes = false;

    @Inject
    public ChannelsLogger(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
        this.proxy = proxy;
        this.logger = logger;
        config = new FileConfiguration(this, dataFolder.resolve("config.yml"));
    }

    /**
     * executed on startup
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            config.createDefaultConfig();
        } catch (IOException e) {
            log(Level.ERROR, "Failed to create default config", e);
        }
        config.load();
        enabled = true;
        logPrivate = config.getBoolean("logPrivate");
        logPrefixes = config.getBoolean("logPrefixes");

        proxy.getEventManager().register(this, this);
    }

    @Subscribe
    public void onChannelsMessage(ChannelsChatEvent event) {
        if (!enabled)
            return;

        if (event.getMessage() instanceof ChannelMessage) {
            ChannelMessage cm = (ChannelMessage) event.getMessage();
            chatLogger.info("[" + cm.getChannel().getTag() + "] " + (logPrefixes ? cm.getChatter().getPrefix() : "") + cm.getChatter().getName() + ": " + cm.getRawMessage());
        } else if (event.getMessage() instanceof PrivateMessage) {
            if (logPrivate) {
                PrivateMessage pm = (PrivateMessage) event.getMessage();
                chatLogger.info(pm.getChatter().getName() + " -> " + pm.getReceiver().getName() + ": " + pm.getRawMessage());
            }
        } else if (event.getMessage() instanceof ConsoleMessage) {
            ConsoleMessage cm = (ConsoleMessage) event.getMessage();
            chatLogger.info("[" + cm.getChannel().getTag() + "] " + cm.getSender() + ": " + cm.getRawMessage());
        }
    }

    public void log(Level level, String message) {
        logger.atLevel(level).log(message);
    }

    public void log(Level level, String message, Throwable throwable) {
        logger.atLevel(level).setCause(throwable).log(message);
    }
}
