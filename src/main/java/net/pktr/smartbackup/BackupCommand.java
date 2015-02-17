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

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class BackupCommand extends CommandBase {
	/**
	 * Complain to the sender that they haven't entered the command correctly.
	 * @param sender ICommandSender to complain to.
	 */
	private void sendUsage(ICommandSender sender) {
		throw new WrongUsageException(this.getCommandUsage(sender));
	}

	@Override
	public String getCommandName() {
		return "smartbackup";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {

		if (args.length == 0) {
			this.sendUsage(sender);
			return;
		}

		switch (args[0]) {
		case "help":
			String[] helpText = {"SmartBackup commands:",
			    "  help - Show a list of valid subcommands",
			    "  version - Show version information"
			};
			for (String line : helpText)
				sender.addChatMessage(new ChatComponentText(line));
			break;
		case "version":
			sender.addChatMessage(new ChatComponentText(
			    "SmartBackup " + SmartBackup.VERSION
			));

			String buildInfo = "Built " + SmartBackup.BUILD_TIMESTAMP;

			// The source revision is "UNKNOWN" if build.gradle can't use git or there
			// is an issue getting the revision string.
			if (SmartBackup.SOURCE_REVISION.equals("UNKNOWN"))
				buildInfo += " from an unknown source revision";
			else
				buildInfo += " from source revision " + SmartBackup.SOURCE_REVISION;

			sender.addChatMessage(new ChatComponentText(buildInfo));
			break;
		default:
			this.sendUsage(sender);
		}
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <help|version>";
	}
}
