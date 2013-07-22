#!/usr/bin/env ruby

# Approves and gets the results from all hits

rootdir = File.dirname $0
login = rootdir + "/login.rb"

require login

hits = RTurk::Hit.all_reviewable

unless hits.empty?
  puts "Reviewing and approving all assignments"

  File.open('results/results.txt', 'a') do |file|
    hits.each do |hit|
      hit.assignments.each do |assignment|
        file.write(assignment.answers.to_s + "\n")
        assignment.approve! if assignment.status == 'Submitted'
      end
    end
  end
end
