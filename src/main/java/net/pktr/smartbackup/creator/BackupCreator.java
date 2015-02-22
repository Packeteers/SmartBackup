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
import org.apache.logging.log4j.Logger;

import java.util.Date;

/** Stores a requester and logger for a derivative creator thread class. */
public abstract class BackupCreator extends Thread {
  /** Represents the status of the backup. */
  public static enum BackupStatus {
    /** The backup has not yet started. */
    PENDING,
    /** The backup is currently running. */
    INPROGRESS,
    /** The backup was successfully completed. */
    COMPLETED,
    /** The backup was interrupted. */
    INTERRUPTED,
    /** The backup failed with an error. */
    FAILED
  }

  /** SmartBackup's {@code Logger} */
  protected final Logger logger;

  /** The status of this backup. */
  protected BackupStatus status;

  /**
   * The error that caused this backup to fail.
   *
   * <p>This is {@code null} unless the backup is in the {@code FAILED} state.</p>
   */
  protected Throwable error = null;

  /** The {@code ICommandSender} that requested the backup. */
  protected final ICommandSender requester;

  /**
   * When this backup was requested.
   *
   * <p>This is set when the backup class is instantiated. While the backup is typically started
   * immediately after instantiating, it is possible that this isn't <i>exactly</i> the same as when
   * the backup process itself was run.</p>
   */
  protected final Date requestTime;

  /**
   * When this backup ended.
   *
   * <p>This can be because of successful completion, interruption, or error.</p>
   *
   * <p>Until this backup ends, this is {@code null}.</p>
   */
  protected Date endTime = null;

  /**
   * The {@code ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted by an {@code ICommandSender}, this is {@code null}.</p>
   */
  protected ICommandSender interrupter = null;

  public BackupCreator(ICommandSender sender, Logger log) {
    requester = sender;
    logger = log;
    status = BackupStatus.PENDING;
    requestTime = new Date();
  }

  /**
   * Gets the {@code ICommandSender} that requested the backup.
   *
   * @return {@code ICommandSender} that requested the backup.
   */
  public ICommandSender getRequester() {
    return requester;
  }

  /**
   * Gets when the backup was requested.
   *
   * <p>This is set when the backup class is instantiated. While the backup is typically started
   * immediately after instantiating, it is possible that this isn't <i>exactly</i> the same as when
   * the backup process itself was run.</p>
   *
   * @return When the backup was requested.
   */
  public Date getRequestTime() {
    return requestTime;
  }

  /**
   * Gets when the backup ended.
   *
   * <p>The backup can end for multiple reasons, use {@link #getStatus} to check the backup's
   * status.</p>
   *
   * @return When this backup was completed.
   */
  public synchronized Date getEndTime() {
    return endTime;
  }

  /**
   * Get the status of this backup.
   *
   * @return {@code enum BackupStatus} representing this backup's status.
   */
  public BackupStatus getStatus() {
    return status;
  }

  /**
   * Get the error that caused this backup to fail.
   *
   * @return The error that caused this backup to fail.
   */
  public Throwable getError() {
    return error;
  }

  /**
   * Gets the {@code ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted or wasn't interrupted by an {@code ICommandSender}, this
   * is {@code null}.</p>
   *
   * @return {@code ICommandSender} that interrupted the backup.
   */
  public ICommandSender getInterrupter() {
    return interrupter;
  }

  /**
   * Sets the {@code ICommandSender} that interrupted the backup.
   *
   * @param interrupter {@code ICommandSender} that interrupted the backup.
   */
  public void setInterrupter(ICommandSender interrupter) {
    this.interrupter = interrupter;
  }
}
