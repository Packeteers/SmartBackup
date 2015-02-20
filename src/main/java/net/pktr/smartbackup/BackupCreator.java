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
