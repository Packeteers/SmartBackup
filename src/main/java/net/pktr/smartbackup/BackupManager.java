package net.pktr.smartbackup;

import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.Logger;

public class BackupManager {
  private Logger logger;
  private BackupCreator currentBackup;

  public BackupManager(Logger log) {
    logger = log;
  }

  /**
   * Used to check if there is currently a backup running.
   *
   * @return true if there is a backup running.
   */
  public boolean backupInProgress() {
    return currentBackup != null && currentBackup.isAlive();
  }

  /**
   * Spawns a new backup thread to create a snapshot.
   */
  public void startSnapshot(ICommandSender requester) {
    currentBackup = new SnapshotCreator(requester, logger);
    currentBackup.start();
  }

  /**
   * Spawns a new backup thread to create an archive.
   */
  public void startArchive(ICommandSender requester) {
    currentBackup = new ArchiveCreator(requester, logger);
    currentBackup.start();
  }

  /**
   * Interrupts any current backups in progress. Doing this will remove any partially-created
   * snapshots or archives.
   */
  public void interruptBackups() {
    if (currentBackup != null && currentBackup.isAlive()) {
      currentBackup.interrupt();
    }
  }

  /**
   * Blocks until any child backup threads have exited, or the current thread is interrupted
   * (throwing InterruptedException).
   */
  public void waitForBackups() throws InterruptedException {
    if (currentBackup != null && currentBackup.isAlive()) {
      currentBackup.join();
    }
  }
}
