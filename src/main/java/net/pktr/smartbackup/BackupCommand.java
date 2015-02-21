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
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

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

        manager.getCurrentBackup().setInterrupter(sender);

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
        if (manager.backupInProgress()) {
          sender.addChatMessage(new ChatComponentText("A backup is in progress:"));
        } else {
          sender.addChatMessage(new ChatComponentText("Ready to take a backup!"));
          if (manager.getCurrentBackup() == null) {
            break;
          } else {
            sender.addChatMessage(new ChatComponentText("Last backup requested:"));
          }
        }

        BackupCreator currentBackup = manager.getCurrentBackup();

        // Request information

        if (currentBackup instanceof SnapshotCreator) {
          sender.addChatMessage(new ChatComponentText("  Type: Snapshot"));
        } else if (currentBackup instanceof ArchiveCreator) {
          sender.addChatMessage(new ChatComponentText("  Type: Archive"));
        }

        sender.addChatMessage(
            new ChatComponentText(
                "  Requested by: " + currentBackup.getRequester().getCommandSenderName()
            )
        );

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        sender.addChatMessage(
            new ChatComponentText(
                "  Requested at: " + formatter.format(currentBackup.getRequestTime())
            )
        );

        // Interruption information (if interrupted)

        if (currentBackup.getInterruptedTime() != null) {
          sender.addChatMessage(
              new ChatComponentText(
                  "  Interrupted at: " + formatter.format(currentBackup.getInterruptedTime())
              )
          );
          if (currentBackup.getInterrupter() != null) {
            sender.addChatMessage(
                new ChatComponentText(
                    "  Interrupted by: " + currentBackup.getInterrupter().getCommandSenderName()
                )
            );
          } else {
            sender.addChatMessage(
                new ChatComponentText("  Interrupted by an unknown force (probably the JVM)")
            );
          }
        }

        // Completion information (if completed)

        if (currentBackup.getCompletionTime() != null) {
          sender.addChatMessage(
              new ChatComponentText(
                  "  Completed at: " + formatter.format(currentBackup.getCompletionTime())
              )
          );
        }

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
