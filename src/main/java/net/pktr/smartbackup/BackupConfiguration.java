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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/** Handles configuration settings for SmartBackup. */
public class BackupConfiguration {
  private Logger logger;
  private Configuration config;
  private Property backupIncludes;
  private Property backupExcludes;
  private Property notifyOps;
  private Property notifyAll;

  /**
   * Loads a backup file and writes defaults if settings are missing.
   *
   * @param file {@link File} to load backup from (and write to).
   */
  public BackupConfiguration(File file) {
    logger = SmartBackup.getLogger();
    config = new Configuration(file);
    config.load();


    // Targets

    config.setCategoryComment(
        "targets",
        "Define files/folders included or excluded from backups. Paths relative to the server root."
    );

    config.setCategoryPropertyOrder("targets", Arrays.asList("include", "exclude"));

    backupIncludes = config.get(
        "targets",
        "include",
        new String[]{"world"},
        "List files/folders included in the backup.\n" +
            "Wildcards (eg *.json) are not yet supported.\n" +
            "(default: world)"
    );

    backupExcludes = config.get(
        "targets",
        "exclude",
        new String[0],
        "List files/folders excluded from files/folders included previously. If a\n" +
            "folder is excluded, all of its children are implicitly excluded as well.\n" +
            "Wildcards (eg *.lck) are not yet supported.\n" +
            "(default empty)"
    );


    // General Settings

    notifyOps = config.get(
        Configuration.CATEGORY_GENERAL,
        "notifyOps",
        true,
        "If set to true, will notify any server ops online when a backup is taken.\n" +
            "This setting is ignored if notifyAll is true.\n" +
            "(default: true)"
    );

    notifyAll = config.get(
        Configuration.CATEGORY_GENERAL,
        "notifyAll",
        false,
        "If set to true, will notify all online players when a backup is taken.\n" +
            "(default: false)"
    );

    if (config.hasChanged()) {
      logger.info("Configuration updated with (at least one) default(s). If this is the first " +
          "time SmartBackup has been run or if you have updated SmartBackup, this is expected.");
      config.save();
    }
  }

  /**
   * Gets whether to notify all online players of backups.
   *
   * @return Returns {@code true} if SmartBackup should notify everyone of status messages.
   */
  public boolean getNotifyAll() {
    return notifyAll.getBoolean();
  }

  /**
   * Sets whether to notify all online players of backups.
   *
   * @param setting {@code true} to enable sending status messages to everyone.
   */
  public void setNotifyAll(boolean setting) {
    notifyAll.set(setting);
    config.save();
  }

  /**
   * Gets whether to notify online server ops of backups.
   *
   * @return Returns {@code true} if SmartBackup should notify server ops with status messages.
   */
  public boolean getNotifyOps() {
    return notifyOps.getBoolean();
  }

  /**
   * Sets whether to notify online server ops of backups.
   *
   * @param setting {@code true} to enable sending status messages to server ops.
   */
  public void setNotifyOps(boolean setting) {
    notifyOps.set(setting);
    config.save();
  }

  /**
   * Gets the list of files/folders to backup.
   *
   * @return List of files/folders to backup.
   */
  public String[] getBackupIncludes() {
    return backupIncludes.getStringList();
  }

  /**
   * Sets the list of files/folders to include in backups.
   *
   * @param includes List to set as the include list.
   */
  public void setBackupIncludes(String[] includes) {
    backupIncludes.set(includes);
    config.save();
  }

  /**
   * Gets the list of files/folders to exclude from any includes.
   *
   * @return List of files/folders to exclude from includes.
   */
  public String[] getBackupExcludes() {
    return backupExcludes.getStringList();
  }

  /**
   * Sets the list of files/folders to exclude from the include list.
   *
   * @param excludes List to set as the exclude list.
   */
  public void setBackupExcludes(String[] excludes) {
    backupExcludes.set(excludes);
    config.save();
  }
}
