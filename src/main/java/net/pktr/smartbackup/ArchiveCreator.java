package net.pktr.smartbackup;

import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.Logger;

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
   * @param logger The Logger to report messages to.
   */
  public ArchiveCreator(ICommandSender sender, Logger logger) {
    super(sender, logger);
    this.setName("Archive Thread");
  }

  @Override
  public void run() {

  }
}
