package net.pktr.smartbackup;

import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.Logger;

/**
 * Stores a requester and logger for a derivative creator thread class.
 */
public abstract class BackupCreator extends Thread {
  /**
   * The {@code ICommandSender} that requested the backup.
   */
  protected final ICommandSender requester;
  /**
   * SmartBackup's {@code Logger}
   */
  protected final Logger logger;

  public BackupCreator(ICommandSender sender, Logger log) {
    requester = sender;
    logger = log;
  }

  /**
   * Gets the {@code ICommandSender} that requested the backup.
   *
   * @return {@code ICommandSender} that requested the backup.
   */
  public ICommandSender getRequester() {
    return requester;
  }
}
