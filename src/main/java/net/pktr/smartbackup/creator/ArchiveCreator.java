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

package net.pktr.smartbackup.creator;

import net.minecraft.command.ICommandSender;

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
   */
  public ArchiveCreator(ICommandSender sender) {
    super(sender);
    this.setName("Archive Thread");
  }

  /** {@inheritDoc} */
  @Override
  public String getBackupType() {
    return "archive";
  }

  @Override
  protected void createBackup(){
    // TODO: Create archive
  }
}
