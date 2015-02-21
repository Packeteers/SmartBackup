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

import java.util.Date;

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
    completionTime = new Date();
  }
}
