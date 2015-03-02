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

  /** {@inheritDoc} */
  @Override
  public String getBackupType() {
    return "snapshot";
  }

  /** Creates a snapshot. */
  @Override
  protected void createBackup() throws InterruptedException {
    // TODO: Create snapshot
    // Fake work
    sleep(10000);
  }
}
