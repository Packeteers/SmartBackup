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

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.TimeZone;

/**
 * Handles creation of snapshots.
 *
 * <p>A snapshot is a folder of hardlinks to the world data files. Snapshots provide a
 * restorable state of the world without copying all of the world data every time you take a
 * backup.</p>
 */
public class SnapshotCreator extends BackupCreator {
  /** ArrayList loaded from config file of files/folders to leave out */
  private ArrayList<String> backupExcludes;
  /** Where to output the snapshot to */
  private Path snapshotOutput;

  /**
   * Sets up a snapshot creation thread.
   *
   * @param sender The {@link ICommandSender} that requested this snapshot. Used for status
   * messages.
   */
  public SnapshotCreator(ICommandSender sender) {
    super(sender);
    this.setName("Snapshot Thread");
  }

  /** Provides methods to use with {@link Files#walkFileTree} for snapshots */
  private class SnapshotVisitor implements FileVisitor<Path> {
    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attr)
        throws IOException {
      // Ignore the whole directory if it's in the ignore list
      if (backupExcludes.contains(path.toString())) {
        return FileVisitResult.SKIP_SUBTREE;
      }

      Files.createDirectories(snapshotOutput.resolve(path));

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {
      // Continue without doing anything with the file if it's in the ignore list
      if (backupExcludes.contains(path.toString())) {
        return FileVisitResult.CONTINUE;
      }

      // TODO Fancy stuff with dedupe/links

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException exception) throws IOException {
      throw exception;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path path, IOException exception) throws IOException {
      return FileVisitResult.CONTINUE;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getBackupType() {
    return "snapshot";
  }

  /** {@inheritDoc} */
  @Override
  protected void createBackup() throws InterruptedException, IOException {
    SimpleDateFormat rfc8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss'Z'");
    rfc8601Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    snapshotOutput = Paths.get(config.getBackupOutputDir(), rfc8601Formatter.format(new Date()));

    if (Files.exists(snapshotOutput)) {
      throw new FileAlreadyExistsException("Backup output directory already exists: " +
          snapshotOutput.toString());
    }

    Files.createDirectories(snapshotOutput);

    backupExcludes = new ArrayList<>(Arrays.asList(config.getBackupExcludes()));

    for (String file : config.getBackupIncludes()) {
      Files.walkFileTree(Paths.get(file), EnumSet.allOf(FileVisitOption.class),
          Integer.MAX_VALUE, new SnapshotVisitor());
    }
  }
}
