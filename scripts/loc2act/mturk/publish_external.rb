#!/usr/bin/env ruby

require 'ruby-aws'

@mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Production

def createNewExternalQuestion
  title = "Location Based Activity Recognition"
  desc = "Inferring activities (e.g., eating food, going for a walk, etc.) from" +
         " the locations in which they might take place"
  keywords = "activity, recognition, location, mapping"
  numAssignments = 1

  numHits = 0
  batches = 1
  rewardAmount = 0.03
  base = 'http://crowd.fooshed.net/location/'

  # Update vars with command line arguments if any
  if ARGV.length >= 1
    numHits = ARGV[0].to_i unless ARGV[0] == 'ALL'
  end
  if ARGV.length >= 2
    batches = ARGV[1].to_i
  end
  if ARGV.length >= 3
    rewardAmount = ARGV[2].to_f
  end
  if ARGV.length == 4
    base = ARGV[3]
  end

  # Publish in batches
  batches.times do
    locations = IO.read(Dir.pwd + "/misc/locs.txt").split()
    locations.map! { |x| x.strip() }

    locations.sort_by! { rand }
    # Publish only numHits HITs
    locations = locations.slice(0..numHits-1)


    # Wrap HIT into an ExternalQuestion form and send it to MTurk
    locations.each do |location|
      link = base + location
      question = "<ExternalQuestion xmlns=\"http://mechanicalturk" +
        ".amazonaws.com/AWSMechanicalTurkDataSchemas/" +
        "2006-07-14/ExternalQuestion.xsd\">\n"
      question += "<ExternalURL>" + link + "</ExternalURL>\n"
      question += "<FrameHeight>600</FrameHeight>\n"
      question += "</ExternalQuestion>\n"

      result = @mturk.createHIT(:Title => title,
                                :Description => desc,
                                :MaxAssignment => numAssignments,
                                :Reward => { :Amount => rewardAmount,
                                  :CurrencyCode => 'USD' },
                                  :Question => question.strip(),
                                  :Keywords => keywords )

      puts "Created HIT: #{result[:HITId]}"
      puts "HIT Location: #{getHITUrl(result[:HITTypeId])}"
    end
  end

  puts "Published #{count} HITs (#{count / numHits} batches)"
end

def getHITUrl( hitTypeId )
  if @mturk.host =~ /sandbox/
    "http://workersandbox.mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Sandbox Url
  else
    "http://mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Production Url
  end
end

### Usage data
USAGE = 'USAGE: publish_external [NUM HITs] [BATCHES] [REWARD] [BASE_URL]'

if ARGV.length > 0 && ARGV[0] == '--help'
  puts USAGE
  exit(0)
end

createNewExternalQuestion
