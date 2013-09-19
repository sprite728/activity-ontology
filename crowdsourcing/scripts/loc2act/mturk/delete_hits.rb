#!/usr/bin/env ruby

require 'docopt'

require_relative "rturk_hit"

doc = <<DOCOPT
Delete HITs.

Usage:
  #{__FILE__} [--all] [-p | --production]
  #{__FILE__} -h | --help
  #{__FILE__} --version

Options:
  -h --help         Show this screen.
  --version         Show the version.

  --all             Delete all HITs.
  -p --production   Delete HITs from production.

DOCOPT

# Delete all approved/rejected hits
def delete
  puts "Deleting all reviewed assignments"

  count = 0
  RTurk::Hit.each do |hit|
    hit.assignments.each do |assignment|
      if assignment.status == 'Approved' ||
         assignment.status == 'Rejected'
        assignment.dispose!
        count += 1

        hit.disable!
      end
    end
  end

  puts "Disposed of #{count} reviewed assignments"
end

def delete_all
  puts "Deleting all assignments"

  RTurk::Hit.each do |hit|
    hit.disable!
  end
end

# Parse arguments and call delete function
begin
  args = Docopt::docopt(doc, version: '0.2')
rescue Docopt::Exit => e
  puts e.message
end

if args
  if args['--production']
    RTurk.setup(AWSAccessKey, AWSSecretKey, :sandbox => false)
  else
    RTurk.setup(AWSAccessKey, AWSSecretKey, :sandbox => true)
  end

  if args['--all']
    delete_all
  else
    delete
  end
end
