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
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Logger;

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

    if (requester != null) {
      requester.addChatMessage(new ChatComponentText("Starting snapshot..."));
    }

    try {
      // Fake work
      sleep(5000);
    } catch (InterruptedException e) {
      logger.warn(
          "A snapshot was interrupted while in-progress. " +
              "Any partially-created data will be deleted."
      );

      if (requester != null) {
        requester.addChatMessage(
            new ChatComponentText(
                "Your snapshot was interrupted. Any partially-created data will be deleted."
            )
        );
      }

      return;
    }

    logger.info("Snapshot completed.");

    if (requester != null) {
      requester.addChatMessage(new ChatComponentText("Snapshot complete!"));
    }
  }
}
