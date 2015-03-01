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

import net.pktr.smartbackup.creator.BackupCreator;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * The /smartbackup command.
 */
public class BackupCommand extends CommandBase {
  private BackupManager manager;
  private Messenger messenger;

  public BackupCommand(BackupManager man) {
    manager = man;
    messenger = SmartBackup.getMessenger();
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

        messenger.unicastInfo(sender, "Backup cancelled");

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
          messenger.unicastInfo(sender, line);
        }

        break;
      case "status":
        if (manager.backupInProgress()) {
          messenger.unicastInfo(sender, "A backup is in progress:");
        } else {
          messenger.unicastInfo(sender, "Ready to take a backup!");
          if (manager.getCurrentBackup() == null) {
            break;
          } else {
            messenger.unicastInfo(sender, "Last backup requested:");
          }
        }

        BackupCreator currentBackup = manager.getCurrentBackup();

        messenger.unicastInfo(sender, "  Type: " + currentBackup.getBackupType());

        messenger.unicastInfo(
            sender,
            "  Requested by: " + currentBackup.getRequester().getCommandSenderName()
        );

        SimpleDateFormat rfc8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        rfc8601Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        messenger.unicastInfo(
            sender,
            "  Requested at: " + rfc8601Formatter.format(currentBackup.getRequestTime())
        );

        switch (currentBackup.getStatus()) {
          case PENDING:
            messenger.unicastInfo(sender, "  Status: Pending");

            break;
          case INPROGRESS:
            messenger.unicastInfo(sender, "  Status: In-Progress");

            break;
          case COMPLETED:
            messenger.unicastInfo(sender, "  Status: Completed Successfully");
            messenger.unicastInfo(
                sender,
                "  Completed at: " + rfc8601Formatter.format(currentBackup.getEndTime())
            );

            break;
          case INTERRUPTED:
            messenger.unicastInfo(sender, "  Status: Interrupted");

            messenger.unicastInfo(
                sender,
                "  Interrupted at: " + rfc8601Formatter.format(currentBackup.getEndTime())
            );

            if (currentBackup.getInterrupter() != null) {
              messenger.unicastInfo(
                  sender,
                  "  Interrupted by: " + currentBackup.getInterrupter().getCommandSenderName()
              );
            } else {
              messenger.unicastInfo(
                  sender,
                  "  Interrupted by an unknown force (probably the JVM)"
              );
            }

            break;
          case FAILED:
            messenger.unicastInfo(sender, "  Status: Failed");
            messenger.unicastInfo(
                sender,
                "  Failed at: " + rfc8601Formatter.format(currentBackup.getEndTime())
            );
            messenger.unicastInfo(
                sender,
                "  Error: " + currentBackup.getError().getMessage()
            );

            break;
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
        messenger.unicastInfo(sender, "SmartBackup " + SmartBackup.VERSION);

        String buildInfo = "Built " + SmartBackup.BUILD_TIMESTAMP;

        // The source revision is "UNKNOWN" if build.gradle can't use git or there
        // is an issue getting the revision string.
        if (SmartBackup.SOURCE_REVISION.equals("UNKNOWN")) {
          buildInfo += " from an unknown source revision";
        } else {
          buildInfo += " from " + SmartBackup.SOURCE_REVISION;
        }

        messenger.unicastInfo(sender, buildInfo);

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
