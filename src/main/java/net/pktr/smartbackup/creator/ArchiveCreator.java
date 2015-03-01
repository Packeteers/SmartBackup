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

import net.pktr.smartbackup.Messenger;
import net.pktr.smartbackup.SmartBackup;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.MinecraftException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Handles creation of archives.
 *
 * <p>An archive is a complete file archive (eg tarball or zip file) of the data being backed
 * up.</p>
 */
public class ArchiveCreator extends BackupCreator {
  /**
   * Sets up an archive creation thread.
   *
   * @param sender The ICommandSender that requested this archive. Used for status messages.
   */
  public ArchiveCreator(ICommandSender sender) {
    super(sender);
    this.setName("Archive Thread");
  }

  @Override
  public void run() {
    Messenger messenger = SmartBackup.getMessenger();

    status = BackupStatus.INPROGRESS;

    messenger.info(requester, "Starting archive");

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
          "Unable to save world data for an archive. No data has been backed up.",
          exception
      );

      return;
    }

    // TODO: Create archive

    if (savingWasEnabled) {
      this.changeWorldSaving(true);
    }

    endTime = new Date();
    status = BackupStatus.COMPLETED;

    SimpleDateFormat rfc8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    rfc8601Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    messenger.info(requester, "Archive completed at " + rfc8601Formatter.format(endTime));
  }
}
