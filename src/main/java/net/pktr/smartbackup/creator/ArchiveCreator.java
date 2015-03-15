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
import java.net.URI;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Handles creation of archives.
 *
 * <p>An archive is a complete file archive (eg tarball or zip file) of the data being backed
 * up.</p>
 */
public class ArchiveCreator extends BackupCreator {
  /** ArrayList loaded from config file of files/folders to leave out */
  private ArrayList<String> backupExcludes;
  /** File to write the archive into */
  private Path archiveOutput;
  /** FileSystem representing the zip file */
  private FileSystem zip;

  /**
   * Sets up an archive creation thread.
   *
   * @param sender The {@link ICommandSender} that requested this archive. Used for status messages.
   */
  public ArchiveCreator(ICommandSender sender) {
    super(sender);
    this.setName("Archive Thread");
  }

  /** Provides methods to use with {@link Files#walkFileTree} for archives */
  private class ArchiveVisitor implements FileVisitor<Path> {
    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attr)
        throws IOException {
      // Ignore the whole directory if it's in the ignore list
      if (backupExcludes.contains(path.toString())) {
        return FileVisitResult.SKIP_SUBTREE;
      }

      Files.createDirectories(zip.getPath(path.toString()));

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {
      // Continue without doing anything with the file if it's in the ignore list
      if (backupExcludes.contains(path.toString())) {
        return FileVisitResult.CONTINUE;
      }

      Files.copy(path, zip.getPath(path.toString()));

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
    return "archive";
  }

  /** {@inheritDoc} */
  @Override
  protected void createBackup() throws InterruptedException, IOException {
    SimpleDateFormat rfc8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss'Z'");
    rfc8601Formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    archiveOutput = Paths.get(config.getBackupOutputDir(),
        rfc8601Formatter.format(new Date()) + ".zip");

    if (Files.exists(archiveOutput)) {
      throw new FileAlreadyExistsException("Archive output file already exists: " +
          archiveOutput.toString());
    }

    Map<String, String> fsEnv = new HashMap<>();
    fsEnv.put("create", "true");

    if (!Files.exists(archiveOutput.getParent())) {
      Files.createDirectories(archiveOutput.getParent());
    }

    zip = FileSystems.newFileSystem(
        URI.create("jar:file:" + archiveOutput.toAbsolutePath().toString()),
        fsEnv
    );

    backupExcludes = new ArrayList<>(Arrays.asList(config.getBackupExcludes()));

    for (String file : config.getBackupIncludes()) {
      Files.walkFileTree(Paths.get(file), EnumSet.allOf(FileVisitOption.class),
          Integer.MAX_VALUE, new ArchiveVisitor());
    }

    zip.close();
  }
}
