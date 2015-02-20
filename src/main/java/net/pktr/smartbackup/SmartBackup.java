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

@Mod(modid = SmartBackup.MOD_ID,
    name = SmartBackup.MOD_NAME,
    version = SmartBackup.VERSION,
    acceptableRemoteVersions = "*")
public class SmartBackup {
  public static final String MOD_ID = "smartbackup";
  public static final String MOD_NAME = "SmartBackup";

  // These are filled in by the build process.
  public static final String VERSION = "$VERSION$";
  public static final String SOURCE_REVISION = "$SOURCE_REVISION$";
  public static final String BUILD_TIMESTAMP = "$BUILD_TIMESTAMP$";

  private Logger logger;
  private BackupManager manager;

  @EventHandler
  public void preInitializationEvent(FMLPreInitializationEvent event) {
    logger = event.getModLog();
  }

  @EventHandler
  public void initializationEvent(FMLInitializationEvent event) {
    manager = new BackupManager(logger);
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
}
