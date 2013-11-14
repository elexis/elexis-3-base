#!/bin/bash
# Copyright 2012 by Niklaus Giger niklaus.giger@member.fsf.org
# Distributed under the Eclipse Public License 1.0
# wait (using lsof) to see whether the file passed to lowriter is being used
# lsof is a CPU hog under Linux, inotify would be several magnitudes faster
# but needs the (optional) inotify-tools
# inotifywait -e modify /var/log/messages
# On MacOSX lsof is less a CPU hog
# fuser is an alternative, too. Faster on Linux

$* &

# our logfile
logFile="$HOME/elexis/logs/opendocument.log"
mkdir -p `dirname $logFile`

# log message our log file
function log2file {
  msg="`date +"%Y:%m:%d %H:%M:%S"` $1"
  echo $msg 
  echo $msg >> $logFile
}

# Find out which parameter is the file to watch
for j in $*
do
  if [ -f $j ] && [ ! -x $j ]
  then
    watchFile=$j
    break
  fi
done

# wait max 30 seconds for the watchFile to be opened
maxWait=100
nrWaits=0
log2file "Waiting for $watchFile"
while [ 1 ]
do
  sleep 0.3
  nrWaits=$[ $nrWaits + 1 ]
  timeout 1 fuser --silent $watchFile 
  res=$?
  if [ "$nrWaits" -gt "$maxWait" ] || [ $res -eq 0 ] ; then break ; fi
done
log2file "After $nrWaits sleeps $watchFile is open"

# wait for the lockfile to disappear
while true
do
  timeout 1 fuser --silent $watchFile
  if [ $? -gt 0 ] ; then break ; fi
  sleep 1
done
log2file "$watchFile went away"

exit 0
# should we use --norestore and/or -o