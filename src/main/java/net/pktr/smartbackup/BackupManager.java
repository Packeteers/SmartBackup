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

import net.pktr.smartbackup.creator.ArchiveCreator;
import net.pktr.smartbackup.creator.BackupCreator;
import net.pktr.smartbackup.creator.SnapshotCreator;

import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.Logger;

/**
 * Manages (start, interrupt, get status of) snapshot and archive creator threads.
 */
public class BackupManager {
  private Logger logger;
  private BackupCreator currentBackup;

  public BackupManager() {
    logger = SmartBackup.getLogger();
  }

  /**
   * Checks if there is currently a backup running.
   *
   * @return {@code true} if a backup is running.
   */
  public boolean backupInProgress() {
    return currentBackup != null &&
        (currentBackup.getStatus() == BackupCreator.BackupStatus.PENDING ||
            currentBackup.getStatus() == BackupCreator.BackupStatus.INPROGRESS);
  }

  /**
   * Get the current backup object.
   *
   * <p>Useful for getting information regarding the currently-running or last-requested backup.</p>
   *
   * @return The {@link BackupCreator} instance of the current backup. May also be {@code null} if
   * no backups have been run.
   */
  public BackupCreator getCurrentBackup() {
    return currentBackup;
  }

  /**
   * Spawns creation process for a new snapshot.
   *
   * @param requester The {@link ICommandSender} that requested the snapshot.
   */
  public void startSnapshot(ICommandSender requester) {
    currentBackup = new SnapshotCreator(requester);
    currentBackup.start();
  }

  /**
   * Spawns creation process for a new archive.
   *
   * @param requester The {@link ICommandSender} that requested the archive.
   */
  public void startArchive(ICommandSender requester) {
    currentBackup = new ArchiveCreator(requester);
    currentBackup.start();
  }

  /**
   * Interrupts any current backups in progress.
   *
   * <p>Doing this will remove any temporary files related to the in-progress backup.</p>
   */
  public void interruptBackups() {
    if (currentBackup != null && currentBackup.isAlive()) {
      currentBackup.interrupt();
    }
  }

  /**
   * Blocks until any child backup threads have exited, or the current thread is interrupted.
   *
   * @throws InterruptedException from {@link Thread#join} method on the creator thread.
   */
  public void waitForBackups() throws InterruptedException {
    if (currentBackup != null && currentBackup.isAlive()) {
      currentBackup.join();
    }
  }
}
