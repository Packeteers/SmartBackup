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
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * The /smartbackup command.
 */
public class BackupCommand extends CommandBase {
  private BackupManager manager;

  public BackupCommand(BackupManager man) {
    manager = man;
  }

  @Override
  public String getCommandName() {
    return "smartbackup";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args.length == 0) {
      throw new WrongUsageException(this.getCommandUsage(sender));
    }

    switch (args[0]) {
      case "cancel":
        if (!manager.backupInProgress()) {
          throw new CommandException("No backups are running");
        }

        manager.interruptBackups();

        try {
          manager.waitForBackups();
        } catch (InterruptedException e) {
          return;
        }

        sender.addChatMessage(new ChatComponentText("Backup cancelled"));

        break;
      case "help":
        String[] helpText = {
            "SmartBackup subcommands:",
            "  cancel - Cancel the currently-running backup (if any)",
            "  help - List subcommands",
            "  status - Show the status of the backup system",
            "  take-archive - Start the creation of an archive",
            "  take-snapshot - Start the creation of a snapshot",
            "  version - Show information about SmartBackup's version"
        };

        for (String line : helpText) {
          sender.addChatMessage(new ChatComponentText(line));
        }

        break;
      case "status":
        if (!manager.backupInProgress()) {
          sender.addChatMessage(new ChatComponentText("Ready to take a backup"));
          break;
        }

        sender.addChatMessage(new ChatComponentText("A backup is currently in-progress"));

        break;
      case "take-archive":
      case "take-snapshot":
        if (manager.backupInProgress()) {
          throw new CommandException("There is currently a backup in progress.");
        }

        if (args[0].equals("take-archive")) {
          manager.startArchive(sender);
        } else {
          manager.startSnapshot(sender);
        }

        break;
      case "version":
        sender.addChatMessage(new ChatComponentText("SmartBackup " + SmartBackup.VERSION));

        String buildInfo = "Built " + SmartBackup.BUILD_TIMESTAMP;

        // The source revision is "UNKNOWN" if build.gradle can't use git or there
        // is an issue getting the revision string.
        if (SmartBackup.SOURCE_REVISION.equals("UNKNOWN")) {
          buildInfo += " from an unknown source revision";
        } else {
          buildInfo += " from " + SmartBackup.SOURCE_REVISION;
        }

        sender.addChatMessage(new ChatComponentText(buildInfo));

        break;
      default:
        throw new WrongUsageException(this.getCommandUsage(sender));
    }
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/" + this.getCommandName() + " <cancel|help|status|take-archive|take-snapshot|version>";
  }

  @Override
  public List addTabCompletionOptions(ICommandSender sender, String[] command) {
    if (command.length > 1) {
      return null;
    }

    return getListOfStringsMatchingLastWord(
        command,
        "cancel", "help", "status", "take-archive", "take-snapshot", "version"
    );
  }
}
