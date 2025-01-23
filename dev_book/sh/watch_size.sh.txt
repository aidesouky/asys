#! /bin/bash

# run df command for disk part /dev/nvme0n1p3 
# and use awk to select column 5 USE% from line 2 (first data line in df results) 
# and use sed to replace % with null
use_percent=$( df -h /dev/nvme0n1p3 | awk 'NR==2 { print $5 }' | sed 's/%//' )
#echo $use_percent

# #same as previous instructions
# #run df command for disk part /dev/nvme0n1p3 
# #and use tail to select line 2 (first data line in df results) 
# #and use awk to select column 5 USE%
# #and use sed to replace % with null
# use_percent=$( df -h /dev/nvme0n1p3 | tail -n 2 | awk '{ print $5 }' | sed 's/%//' ) 
# #echo $use_percent

# check disk usage percent and take a decision
# if value from 10% to 79% take no action
# if value from 80% to 99% clear apt package cache 
# and find in directory /lod-src files has name include log- and last change date before 3 day
# and move it to directory /log-dist
case $use_percent in
[1-7]*)
echo $use_percent % usage safe point
;;
[8-9]*)
echo $use_percent % usage critical point
apt autoremove && apt autoclean
find /log-src -name "*log-*" -mtime +3 -exec mv -f {} /log-dist \;

#find /log-src -name "*log-*" -mtime +30 -exec cp {} /log-dist \;
#find /log-src -name "*log-*" -mtime +30 -exec rm -f {} \;
;;
*)
echo nothing
esac

# and find in directory /lod-dist files has name include log- and last change date before 90 day
# and remove it
find /log-dist -name "*log-*" -mtime +90 -exec rm -f {} \;
