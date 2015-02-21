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
 * Stores a requester and logger for a derivative creator thread class.
 */
public abstract class BackupCreator extends Thread {
  /**
   * The {@code ICommandSender} that requested the backup.
   */
  protected final ICommandSender requester;

  /**
   * The {@code ICommandSender} that interrupted the backup.
   *
   * <p>If this backup was not interrupted or wasn't interrupted by an {@code ICommandSender}, this
   * is {@code null}.</p>
   */
  protected ICommandSender interrupter = null;

  /**
   * SmartBackup's {@code Logger}
   */
  protected final Logger logger;

  /**
   * When this backup was requested.
   *
   * <p>This is set when the backup class is instantiated. While the backup is typically started
   * immediately after instantiating, it is possible that this isn't <i>exactly</i> the same as when
   * the backup process itself was run.</p>
   */
  protected final Date requestTime;

  /**
   * When this backup was interrupted.
   *
   * <p>If this backup is not (yet) interrupted, this is {@code null}.</p>
   */
  protected Date interruptedTime = null;

  /**
   * When this backup was completed.
   *
   * <p>Until completion, this is {@code null}.</p>
   */
  protected Date completionTime = null;

  public BackupCreator(ICommandSender sender, Logger log) {
    requester = sender;
    logger = log;
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

  /**
   * Gets when this backup was interrupted.
   *
   * <p>If this backup has not (yet) been interrupted, this is {@code null}.</p>
   *
   * @return When this backup was interrupted.
   */
  public synchronized Date getInterruptedTime() {
    return interruptedTime;
  }

  /**
   * Gets when the backup was completed.
   *
   * <p>If this backup is still running or was interrupted, this is {@code null}.</p>
   *
   * <p>For interruption, see {@link #getInterruptedTime}</p>
   *
   * @return When this backup was completed.
   */
  public synchronized Date getCompletionTime() {
    return completionTime;
  }
}
