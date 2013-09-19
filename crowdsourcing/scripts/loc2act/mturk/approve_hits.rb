#!/usr/bin/env ruby

require 'docopt'
require 'yaml'

require_relative "rturk_hit"

doc = <<DOCOPT
Approve HITs.

Usage:
  #{__FILE__} [-p | --production]
  #{__FILE__} -h | --help
  #{__FILE__} --version

Options:
  -h --help         Show this screen.
  --version         Show the version.

  -p --production   Approve HITs from production.

DOCOPT

# Approve all HITs that are correct
def approve
  puts "Reviewing and approving assignments"
  approved = 0
  rejected = 0

  File.open('results.yaml', 'w') do |file|
    RTurk::Hit.each do |hit|
      hit.assignments.each do |assignment|
        # Write result to YAML file
        file.write(YAML::dump(assignment.answers))

        location = assignment.answers['location']
        honeypot = assignment.answers['honeypot']

        # Check if honeypot answer is correct. If not, reject HIT
        if location != honeypot
          assignment.reject!("Failed to answer last question correctly.") if assignment.status == 'Submitted'
          rejected += 1
        else
          assignment.approve! if assignment.status == 'Submitted'
          approved += 1
        end
      end
    end
  end

  puts "Approved: #{approved}"
  puts "Rejected: #{rejected}"
end

# Parse arguments and call approve function
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

  approve
end
