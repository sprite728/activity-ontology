#!/usr/bin/env ruby

# Deletes all HITs (even those that have not been reviewed)

rootdir = File.dirname $0
login = rootdir + "/login.rb"

require login

hits = RTurk::Hit.all

unless hits.empty?
  puts "Removing all HITs"

  hits.each do |hit|
    hit.disable!
  end
end
