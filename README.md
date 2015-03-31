SyncTimeStamps
==============
[![Build Status](https://api.travis-ci.org/unix-junkie/synctimestamps.png?branch=master)](https://travis-ci.org/unix-junkie/synctimestamps)

_synctimestamps_ is a tool to organise your digital photos and videos according to their timestamps.

It synchronises file timestamps (those presented by _[EXIF](http://en.wikipedia.org/wiki/Exchangeable_image_file_format)_ metadata and/or contained in a file's name (certain digital cameras name the files they create like **1969-12-31_23-59-59_12345.jpg**) and/or file modification time (_MTime_)).

 * If the time a photo was shot is available (from either _EXIF_ or _MTime_) but not present in the file name, _synctimestamps_ renames the file accordingly (thus, **12345.jpg** gets renamed to **1969-12-31_23-59-59_12345.jpg**).
 * If file _MTime_ differs from the _EXIF_-provided timestamp, _synctimestamps_ adjusts file _MTime_ accordingly.

If you have a lot of arbitrarily named digital photos/videos in the same directory and want to view them in chronological order -- just run _synctimestamps_ and then arrange the files by name or modification time (the order will be the same in each case).
