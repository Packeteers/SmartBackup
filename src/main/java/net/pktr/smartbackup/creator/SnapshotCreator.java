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
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Logger;

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
   * @param logger The Logger to report messages to.
   */
  public SnapshotCreator(ICommandSender sender, Logger logger) {
    super(sender, logger);
    this.setName("Snapshot Thread");
  }

  @Override
  public void run() {
    logger.info("Starting snapshot");

    requester.addChatMessage(new ChatComponentText("Starting snapshot..."));

    try {
      // Fake work
      sleep(10000);
    } catch (InterruptedException e) {
      interruptedTime = new Date();

      logger.warn(
          "A snapshot was interrupted while in-progress. " +
              "Any partially-created data will be deleted."
      );

      requester.addChatMessage(
          new ChatComponentText(
              "Your snapshot was interrupted. Any partially-created data will be deleted."
          )
      );

      return;
    }

    completionTime = new Date();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    String completionMessage = "Snapshot completed at " + formatter.format(completionTime);

    logger.info(completionMessage);
    requester.addChatMessage(new ChatComponentText(completionMessage));
  }
}
