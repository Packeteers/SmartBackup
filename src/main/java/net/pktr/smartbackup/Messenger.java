/*
 * Copyright 2015 John "LuaMilkshake" Marion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pktr.smartbackup;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.Logger;

/** Used to send status output to configured recipients. */
public class Messenger {
  private Logger logger;
  private BackupConfiguration config;

  public Messenger() {
    logger = SmartBackup.getLogger();
    config = SmartBackup.getConfiguration();
  }

  /**
   * Show error output to only a specific recipient.
   *
   * @param sender Who to send the error message to.
   * @param message The error message to report.
   */
  public void unicastError(ICommandSender sender, ChatComponentText message) {
    unicastError(sender, message, null);
  }

  /**
   * Show error output to only a specific recipient.
   *
   * @param sender Who to send the error message to.
   * @param message The error message to report.
   * @param throwable {@link Throwable} error to output to console if the recipient is a
   * {@link MinecraftServer}.
   */
  public void unicastError(ICommandSender sender, ChatComponentText message, Throwable throwable) {
    if (sender instanceof MinecraftServer) {
      if (throwable == null) {
        logger.error(message.getUnformattedTextForChat());
      } else {
        logger.error(message.getUnformattedTextForChat(), throwable);
      }
      return;
    }

    sender.addChatMessage(message);
  }

  /**
   * Send info output to only a specific recipient.
   *
   * @param sender Who to send info output to.
   * @param message The info message to report.
   */
  public void unicastInfo(ICommandSender sender, String message) {
    if (sender instanceof MinecraftServer) {
      logger.info(message);
      return;
    }

    sender.addChatMessage(new ChatComponentText(message));
  }

  /**
   * Send info output to only a specific recipient.
   *
   * @param sender Who to send info output to.
   * @param message The info message to report.
   */
  public void unicastInfo(ICommandSender sender, ChatComponentText message) {
    if (sender instanceof MinecraftServer) {
      logger.info(message.getUnformattedTextForChat());
      return;
    }

    sender.addChatMessage(message);
  }

  /**
   * Provide error output to configured recipients.
   *
   * <p>Will also notify players or ops if configured in the configuration file.</p>
   *
   * @param message Error message to report.
   */
  public void error(String message) {
    error(null, message, null);
  }

  /**
   * Provide error output to configured recipients.
   *
   * <p>Will also notify players or ops if configured in the configuration file.</p>
   *
   * @param sender The {@link ICommandSender} related to this message. If the sender is not a
   * {@link MinecraftServer}, it will receive a formatted message.
   * @param message Error message to report.
   */
  public void error(ICommandSender sender, String message) {
    error(sender, message, null);
  }

  /**
   * Provide error output to configured recipients.
   *
   * <p>Will also notify players or ops if configured in the configuration file.</p>
   *
   * @param sender The {@link ICommandSender} related to this message. If the sender is not a
   * {@link MinecraftServer}, it will receive a formatted message.
   * @param message Error message to report.
   * @param throwable {@link Throwable} error to include in server log.
   */
  public void error(ICommandSender sender, String message, Throwable throwable) {
    if (sender != null && !(sender instanceof MinecraftServer)) {
      ChatComponentText chatComponent = new ChatComponentText(message);
      chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
      unicastError(sender, chatComponent, throwable);
    }

    ChatComponentText multicastMessage = new ChatComponentText("[SmartBackup] " + message);
    multicastMessage.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
    multicastMessage.getChatStyle().setItalic(true);

    MinecraftServer server = MinecraftServer.getServer();
    if (config.getNotifyAll()) {
      for (Object playerEntity : server.getConfigurationManager().playerEntityList) {
        EntityPlayer player = (EntityPlayer) playerEntity;
        if (player == sender) {
          continue;
        }
        player.addChatMessage(multicastMessage);
      }
    } else if (config.getNotifyOps()) {
      for (Object playerEntity : server.getConfigurationManager().playerEntityList) {
        EntityPlayer player = (EntityPlayer) playerEntity;
        /*
         * This mystery function seems to be a check for "is this player an op for using commands".
         * This is almost perfect for our use, though it won't return true if commands are disabled
         * in singleplayer.
         */
        if (player != sender &&
            server.getConfigurationManager().func_152596_g(player.getGameProfile())) {
          player.addChatMessage(multicastMessage);
        }
      }
    }

    if (throwable != null) {
      logger.error(message, throwable);
    } else {
      logger.error(message);
    }
  }

  /**
   * Provide information output to configured recipients.
   *
   * <p>Will also notify players or ops if configured in the configuration file.</p>
   *
   * @param message The message to report.
   */
  public void info(String message) {
    info(null, message);
  }

  /**
   * Provide information output to configured recipients.
   *
   * <p>Will also notify players or ops if configured in the configuration file.</p>
   *
   * @param sender The {@link ICommandSender} related to this message. If the sender is not a
   * {@link MinecraftServer}, it will receive a chat message.
   * @param message The message to report.
   */
  public void info(ICommandSender sender, String message) {
    if (sender != null && !(sender instanceof MinecraftServer)) {
      unicastInfo(sender, new ChatComponentText(message));
    }

    ChatComponentText multicastMessage = new ChatComponentText("[SmartBackup] " + message);
    multicastMessage.getChatStyle().setColor(EnumChatFormatting.GRAY);
    multicastMessage.getChatStyle().setItalic(true);

    MinecraftServer server = MinecraftServer.getServer();
    if (config.getNotifyAll()) {
      for (Object playerEntity : server.getConfigurationManager().playerEntityList) {
        EntityPlayer player = (EntityPlayer) playerEntity;
        if (player == sender) {
          continue;
        }
        player.addChatMessage(multicastMessage);
      }
    } else if (config.getNotifyOps()) {
      for (Object playerEntity : server.getConfigurationManager().playerEntityList) {
        EntityPlayer player = (EntityPlayer) playerEntity;
        /*
         * This mystery function seems to be a check for "is this player an op for using commands".
         * This is almost perfect for our use, though it won't return true if commands are disabled
         * in singleplayer.
         */
        if (player != sender &&
            server.getConfigurationManager().func_152596_g(player.getGameProfile())) {
          player.addChatMessage(multicastMessage);
        }
      }
    }

    logger.info(message);
  }
}
