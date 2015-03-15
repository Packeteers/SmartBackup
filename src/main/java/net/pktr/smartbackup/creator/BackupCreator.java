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

import net.pktr.smartbackup.BackupConfiguration;
import net.pktr.smartbackup.Messenger;
import net.pktr.smartbackup.SmartBackup;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/** Defines the common parts of a backup creator. */
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

  /** The status of this backup. */
  protected BackupStatus status;

  /**
   * The error that caused this backup to fail.
   *
   * <p>This is {@code null} unless the backup is in the {@link BackupStatus#FAILED} state.</p>
   */
  protected Throwable error = null;

  /** The {@link ICommandSender} that requested the backup. */
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
   * The {@link ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted by an {@link ICommandSender}, this is {@code null}.</p>
   */
  protected ICommandSender interrupter = null;

  /** Messenger from SmartBackup mod class, used to send messages to players and the console. */
  protected final Messenger messenger = SmartBackup.getMessenger();

  /** Configuration from SmartBackup. */
  protected final BackupConfiguration config = SmartBackup.getConfiguration();

  /** Logger used for output that can't go through the messenger. */
  protected final Logger logger = SmartBackup.getLogger();

  public BackupCreator(ICommandSender sender) {
    requester = sender;
    setStatus(BackupStatus.PENDING);
    requestTime = new Date();
  }

  /**
   * The backup process employed by this backup creator, this is called from the backup thread to
   * run the backup.
   *
   * @throws InterruptedException The backup was interrupted.
   */
  protected abstract void createBackup() throws InterruptedException, IOException;

  /**
   * Returns a string name for the type of backup being created.
   *
   * <p>This is returned in all-lowercase.</p>
   *
   * @return The type of backup this is (eg "snapshot" or "archive").
   */
  public abstract String getBackupType();

  /**
   * Gets the {@link ICommandSender} that requested the backup.
   *
   * @return {@link ICommandSender} that requested the backup.
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
   * @return {@link BackupStatus} representing this backup's status.
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
   * Gets the {@link ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted or wasn't interrupted by an {@link ICommandSender}, this
   * is {@code null}.</p>
   *
   * @return {@link ICommandSender} that interrupted the backup.
   */
  public ICommandSender getInterrupter() {
    return interrupter;
  }

  /**
   * Sets the {@link ICommandSender} that interrupted the backup.
   *
   * @param inter {@link ICommandSender} that interrupted the backup.
   */
  public void setInterrupter(ICommandSender inter) {
    interrupter = inter;
  }

  /**
   * Sets the world saving setting for all worlds on the server.
   *
   * @param worldSaving Whether to enable saving.
   */
  private void setWorldSaving(boolean worldSaving) {
    MinecraftServer server = MinecraftServer.getServer();
    for (WorldServer world : server.worldServers) {
      if (world != null) {
        world.levelSaving = worldSaving;
      }
    }
  }

  /**
   * Update this backup's status.
   *
   * <p>Updates the end time for {@link #getEndTime}.</p>
   *
   * <p>If you're setting the state to {@link BackupStatus#FAILED}, you need to set an error first
   * or this will throw a RuntimeException. Because of this, make sure that you make any calls
   * needed to {@link #setWorldSaving} before running this method to make sure that you don't
   * disable world saving until it's re-enabled.</p>
   *
   * @param newStatus Status to set to.
   */
  private void setStatus(BackupStatus newStatus) {
    RuntimeException exception = null;

    status = newStatus;
    switch (newStatus) {
      case FAILED:
        if (error == null) {
          // Make sure the error that caused the failure is saved.
          exception = new RuntimeException("State set to FAILED without setting an error.");
        }
        // Fall through
      case INTERRUPTED:
      case COMPLETED:
        endTime = new Date();
        break;
      default:
        // Others don't have special state-change code
    }

    if (exception != null) {
      throw exception;
    }
  }

  /**
   * The generic method for taking backups. This does setup for backups, calls the
   * {@link #createBackup} method of the derivative class to create the backup, then does does the
   * completion code for backups.
   */
  @Override
  public void run() {
    MinecraftServer server = MinecraftServer.getServer();

    setStatus(BackupStatus.INPROGRESS);

    messenger.info(requester, "Starting " + getBackupType());

    // Save player data (I guess we call this and hope it works?)
    MinecraftServer.getServer().getConfigurationManager().saveAllPlayerData();

    // Take note of whether saving was enabled in the first place
    boolean savingWasEnabled = false;
    // If any of the worlds have saving enabled, assume saving is enabled for all of them.
    for (WorldServer world : server.worldServers) {
      if (world != null) {
        if (world.levelSaving) {
          savingWasEnabled = true;
          break;
        }
      }
    }

    setWorldSaving(false);

    // Save world data
    try {
      for (WorldServer world : server.worldServers) {
        if (world != null) {
          world.saveAllChunks(true, null);
        }
      }
    } catch (MinecraftException exception) {
      setWorldSaving(savingWasEnabled);

      error = exception;
      setStatus(BackupStatus.FAILED);

      messenger.error(
          requester,
          "Unable to save world for the " + getBackupType() + ". No data has been backed up.",
          exception
      );

      return;
    }

    if (config.getBackupIncludes().length == 0) {
      setWorldSaving(savingWasEnabled);

      // I might want to use a better error class here
      error = new Throwable("There are no configured targets for backups in the config file!");
      setStatus(BackupStatus.FAILED);

      messenger.error(requester, error.getMessage());

      return;
    }

    try {
      createBackup();
    } catch (InterruptedException | ClosedByInterruptException e) {
      setWorldSaving(savingWasEnabled);

      setStatus(BackupStatus.INTERRUPTED);

      messenger.info(
          requester,
          "The " + getBackupType() + " was interrupted. Any temporary backup files will be deleted."
      );

      return;
    } catch (IOException exception) {
      setWorldSaving(savingWasEnabled);

      error = exception;
      setStatus(BackupStatus.FAILED);

      messenger.error(
          requester,
          "The " + getBackupType() + " failed with an IO error: " + exception.getMessage(),
          exception
      );

      return;
    }

    setWorldSaving(savingWasEnabled);

    setStatus(BackupStatus.COMPLETED);

    SimpleDateFormat rfc8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    rfc8601Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    messenger.info(requester, "Completed " + getBackupType() + " at " +
        rfc8601Formatter.format(endTime));
  }
}
