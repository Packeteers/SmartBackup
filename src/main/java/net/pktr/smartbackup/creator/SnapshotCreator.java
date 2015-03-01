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
import net.minecraft.world.MinecraftException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Handles creation of snapshots.
 *
 * <p>A snapshot is a folder of hardlinks to the world data files. Snapshots provide a
 * restorable state of the world without copying all of the world data every time you take a
 * backup.</p>
 */
public class SnapshotCreator extends BackupCreator {
  /**
   * Sets up a snapshot creation thread.
   *
   * @param sender The ICommandSender that requested this snapshot. Used for status messages.
   */
  public SnapshotCreator(ICommandSender sender) {
    super(sender);
    this.setName("Snapshot Thread");
  }

  @Override
  public void run() {
    Messenger messenger = SmartBackup.getMessenger();
    BackupConfiguration config = SmartBackup.getConfiguration();

    status = BackupStatus.INPROGRESS;

    messenger.info(requester, "Starting snapshot");

    boolean savingWasEnabled = this.getWorldSaving();
    if (savingWasEnabled) {
      this.changeWorldSaving(false);
    }

    this.savePlayerData();
    try {
      this.saveWorldData();
    } catch (MinecraftException exception) {
      this.error = exception;
      this.status = BackupStatus.FAILED;
      endTime = new Date();

      messenger.error(
          requester,
          "Unable to save world data for a snapshot. No data has been backed up.",
          exception
      );

      return;
    }

    String[] backupIncludes = config.getBackupIncludes();
    String[] backupExcludes = config.getBackupExcludes();

    if (backupIncludes.length == 0) {
      // I might want to use a better error class here
      this.error = new Throwable("There are no configured targets for backups in the config file!");
      this.status = BackupStatus.FAILED;
      endTime = new Date();
      messenger.error(requester, this.error.getMessage());
      return;
    }

    // TODO: Create snapshot
    try {
      // Fake work
      sleep(10000);
    } catch (InterruptedException e) {
      endTime = new Date();
      status = BackupStatus.INTERRUPTED;

      messenger.info(
          requester,
          "The snapshot was interrupted. Any temporary backup files will be deleted."
      );

      return;
    }

    if (savingWasEnabled) {
      this.changeWorldSaving(true);
    }

    endTime = new Date();
    status = BackupStatus.COMPLETED;

    SimpleDateFormat rfc8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    rfc8601Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    messenger.info(requester, "Snapshot completed at " + rfc8601Formatter.format(endTime));
  }
}
