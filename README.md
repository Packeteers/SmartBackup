SmartBackup
===========

SmartBackup is a backup mod for Minecraft that backs up the game's world data.


Backup Methods
--------------

There are two different methods of backing up data using SmartBackup. You can
choose between these methods when manually running a backup and the backup
scheduler can be instructed to use different methods on different intervals.

### Snapshots

Snapshots are a restorable state of the world at a given time. Snapshots use
hard links instead of copying all of the world data each time you take a backup.
This means that the disk space used by each snapshot is -- barring a complete
re-write of the data -- less than it would be if you were to perform a typical
copy-based backup. It also means that the time required to do a backup is less
than a copy-based backup since less data is written to disk.

While this is typically better than copying the whole world for each backup,
there are a couple of reasons why you might want to use a copy-based backup.

* Your filesystem does not support hard links.
* On-disk corruption of world data (while unlikely, still possible unless the
  underlying filesystem is smart/resilient enough).
* Ease of moving a single file around (ie to an off-site storage system) instead
  of a folder.

### Archives

Archives are the traditional method of taking world backups. They're things like
a tarball or zip file of the world data. The world data is copied in its
entirety into an archive file and is optionally compressed.


Backup Scheduling
-----------------

Not yet documented.


The /smartbackup Command
------------------------

Not yet documented.


License
-------

SmartBackup is licensed with the Apache License, version 2.0:

    Copyright 2015 John "LuaMilkshake" Marion

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    	http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
