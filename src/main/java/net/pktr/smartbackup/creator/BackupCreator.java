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

package net.pktr.smartbackup.creator;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.pktr.smartbackup.SmartBackup;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/** Stores a requester and logger for a derivative creator thread class. */
public abstract class BackupCreator extends Thread {
  /** Represents the status of the backup. */
  public static enum BackupStatus {
    /** The backup has not yet started. */
    PENDING,
    /** The backup is currently running. */
    INPROGRESS,
    /** The backup was successfully completed. */
    COMPLETED,
    /** The backup was interrupted. */
    INTERRUPTED,
    /** The backup failed with an error. */
    FAILED
  }

  /** SmartBackup's {@code Logger} */
  protected final Logger logger;

  /** The status of this backup. */
  protected BackupStatus status;

  /**
   * The error that caused this backup to fail.
   *
   * <p>This is {@code null} unless the backup is in the {@code FAILED} state.</p>
   */
  protected Throwable error = null;

  /** The {@code ICommandSender} that requested the backup. */
  protected final ICommandSender requester;

  /**
   * When this backup was requested.
   *
   * <p>This is set when the backup class is instantiated. While the backup is typically started
   * immediately after instantiating, it is possible that this isn't <i>exactly</i> the same as when
   * the backup process itself was run.</p>
   */
  protected final Date requestTime;

  /**
   * When this backup ended.
   *
   * <p>This can be because of successful completion, interruption, or error.</p>
   *
   * <p>Until this backup ends, this is {@code null}.</p>
   */
  protected Date endTime = null;

  /**
   * The {@code ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted by an {@code ICommandSender}, this is {@code null}.</p>
   */
  protected ICommandSender interrupter = null;

  public BackupCreator(ICommandSender sender) {
    requester = sender;
    logger = SmartBackup.getLogger();
    status = BackupStatus.PENDING;
    requestTime = new Date();
  }

  /**
   * Gets the {@code ICommandSender} that requested the backup.
   *
   * @return {@code ICommandSender} that requested the backup.
   */
  public ICommandSender getRequester() {
    return requester;
  }

  /**
   * Gets when the backup was requested.
   *
   * <p>This is set when the backup class is instantiated. While the backup is typically started
   * immediately after instantiating, it is possible that this isn't <i>exactly</i> the same as when
   * the backup process itself was run.</p>
   *
   * @return When the backup was requested.
   */
  public Date getRequestTime() {
    return requestTime;
  }

  /**
   * Gets when the backup ended.
   *
   * <p>The backup can end for multiple reasons, use {@link #getStatus} to check the backup's
   * status.</p>
   *
   * @return When this backup was completed.
   */
  public synchronized Date getEndTime() {
    return endTime;
  }

  /**
   * Get the status of this backup.
   *
   * @return {@code enum BackupStatus} representing this backup's status.
   */
  public BackupStatus getStatus() {
    return status;
  }

  /**
   * Get the error that caused this backup to fail.
   *
   * @return The error that caused this backup to fail.
   */
  public Throwable getError() {
    return error;
  }

  /**
   * Gets the {@code ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted or wasn't interrupted by an {@code ICommandSender}, this
   * is {@code null}.</p>
   *
   * @return {@code ICommandSender} that interrupted the backup.
   */
  public ICommandSender getInterrupter() {
    return interrupter;
  }

  /**
   * Sets the {@code ICommandSender} that interrupted the backup.
   *
   * @param interrupter {@code ICommandSender} that interrupted the backup.
   */
  public void setInterrupter(ICommandSender interrupter) {
    this.interrupter = interrupter;
  }

  /** Tell the server to save player data. */
  protected void savePlayerData() {
    ServerConfigurationManager confMgr = MinecraftServer.getServer().getConfigurationManager();
    if (confMgr != null) {
      confMgr.saveAllPlayerData();
    }
  }

  /**
   * Tell each world's WorldServer to save the world chunks.
   *
   * @throws MinecraftException Passed on from saveAllChunks on the WorldServer.
   */
  protected void saveWorldData() throws MinecraftException {
    MinecraftServer server = MinecraftServer.getServer();

    for (int i = 0; i < server.worldServers.length; i++) {
      WorldServer worldServer = server.worldServers[i];
      if (worldServer != null) {
        worldServer.saveAllChunks(true, null);
      }
    }
  }

  /**
   * Gets the current status of world saving.
   *
   * <p>Since world saving is specified on a per-world basis, it is possible for world saving to
   * be enabled on one world but be disabled on another. This method will return {@code true} if
   * <i>any world</i> on the server has saving enabled, only returning {@code false} if all worlds
   * have saving disabled.</p>
   *
   * @return {@code true} if any worlds on the server have saving enabled, false if none do.
   */
  protected boolean getWorldSaving() {
    MinecraftServer server = MinecraftServer.getServer();

    for (int i = 0; i < server.worldServers.length; i++) {
      WorldServer worldServer = server.worldServers[i];
      if (worldServer != null) {
        if (worldServer.levelSaving) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Changes the status of world saving on all worlds on this server.
   *
   * @param savingEnabled If saving should be enabled.
   */
  protected void changeWorldSaving(boolean savingEnabled) {
    MinecraftServer server = MinecraftServer.getServer();

    for (int i = 0; i < server.worldServers.length; i++) {
      WorldServer worldServer = server.worldServers[i];
      if (worldServer != null) {
        worldServer.levelSaving = savingEnabled;
      }
    }
  }
}
