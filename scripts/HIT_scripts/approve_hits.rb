#!/usr/bin/env ruby

rootdir = File.dirname $0
login = rootdir + "/login.rb"

require login
require "yaml"

# Modify Hit class to allow iterating over more than 100 results
module RTurk
  class Hit
    def self.each(page_number=1, &block)
      results = RTurk::SearchHITs.create(page_number:page_number, page_size:100)
      num_results = results.xpath("//TotalNumResults").text.to_i

      results.hits.map do |hit|
        yield new(hit.id, hit)
      end
      each(page_number + 1, &block) if num_results > page_number * 100
    end
  end
end

# Approve all HITs that are correct
def approve
  puts "Reviewing and approving assignments"

  File.open('results/results.yaml', 'w') do |file|
    RTurk::Hit.each do |hit|
      hit.assignments.each do |assignment|
        # Write result to YAML file
        file.write(YAML::dump(assignment.answers))

        location = assignment.answers['location']
        honeypot = assignment.answers['honeypot']

        # Check if honeypot answer is correct. If not, reject HIT
        if location != honeypot
          if location == 'rv_park' || location == 'atm'
            assignment.approve! if assignment.status == 'Submitted'
          else
            assignment.reject! if assignment.status == 'Submitted'
          end
        else
          assignment.approve! if assignment.status == 'Submitted'
        end
      end
    end
  end
end
