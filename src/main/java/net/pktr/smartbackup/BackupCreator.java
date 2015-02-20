package net.pktr.smartbackup;

import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.Logger;

public abstract class BackupCreator extends Thread {
	protected final ICommandSender requester;
	protected final Logger logger;

	public BackupCreator(ICommandSender sender, Logger log) {
		requester = sender;
		logger = log;
	}

	public ICommandSender getRequester() {
		return requester;
	}
}
