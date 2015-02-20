package net.pktr.smartbackup;

import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.Logger;

/**
 * Handles creation of archives.
 * <p>An archive is a complete file archive (eg tarball or zip file) of the data being
 * backed up.</p>
 */
public class ArchiveCreator extends BackupCreator {
	public ArchiveCreator(ICommandSender sender, Logger logger) {
		super(sender, logger);
		this.setName("Archive Thread");
	}

	public void run() {

	}
}
