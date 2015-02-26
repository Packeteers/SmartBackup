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

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

/**
 * Main SmartBackup mod class.
 */
@Mod(modid = SmartBackup.MOD_ID,
    name = SmartBackup.MOD_NAME,
    version = SmartBackup.VERSION,
    acceptableRemoteVersions = "*")
public class SmartBackup {
  public static final String MOD_ID = "SmartBackup";
  public static final String MOD_NAME = MOD_ID;

  // These are filled in by the build process.
  public static final String VERSION = "$VERSION$";
  public static final String SOURCE_REVISION = "$SOURCE_REVISION$";
  public static final String BUILD_TIMESTAMP = "$BUILD_TIMESTAMP$";

  private static Logger logger = null;
  private BackupManager manager;
  private static BackupConfiguration config = null;
  private static Messenger messenger = null;

  @EventHandler
  public void preInitializationEvent(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    config = new BackupConfiguration(event.getSuggestedConfigurationFile());
    messenger = new Messenger();
  }

  @EventHandler
  public void initializationEvent(FMLInitializationEvent event) {
    manager = new BackupManager();
  }

  @EventHandler
  public void serverStartingEvent(FMLServerStartingEvent event) {
    event.registerServerCommand(new BackupCommand(manager));
  }

  @EventHandler
  public void serverStoppingEvent(FMLServerStoppingEvent event) {
    manager.interruptBackups();
    try {
      manager.waitForBackups();
    } catch (InterruptedException e) {
      return;
    }
  }

  /**
   * Get SmartBackup's configuration.
   *
   * @return SmartBackup's configuration.
   */
  public static BackupConfiguration getConfiguration() {
    return config;
  }

  /**
   * Get SmartBackup's logger.
   *
   * @return SmartBackup's logger.
   */
  public static Logger getLogger() {
    return logger;
  }

  /**
   * Get SmartBackup's messenger.
   *
   * @return SmartBackup's messenger.
   */
  public static Messenger getMessenger() {
    return messenger;
  }
}
