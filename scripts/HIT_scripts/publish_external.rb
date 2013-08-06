#!/usr/bin/env ruby

require 'ruby-aws'

@mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Production

def createNewExternalQuestion
  title = "Location Based Activity Recognition"
  desc = "Inferring activities (e.g., eating food, going for a walk, etc.) from" +
         " the locations in which they might take place"
  keywords = "activity, recognition, location, mapping"
  numAssignments = 1

  numHits = -1
  rewardAmount = 0.03
  base = 'http://crowd.fooshed.net/location/'

  if ARGV.length == 1
    numHits = ARGV[0].to_i
  elsif ARGV.length == 2
    numHits = ARGV[0].to_i
    rewardAmount = ARGV[1].to_f
  elsif ARGV.length == 3
    numHits = ARGV[0].to_i
    rewardAmount = ARGV[1].to_f
    base = ARGV[2]
  end

  p numHits
  p rewardAmount
  p base

  locations = IO.read(Dir.pwd + "/misc/locs.txt").split()
  locations.map! { |x| x.strip() }

  locations.sort_by! { rand }
  # Publish only numHits HITs
  locations = locations.slice(0..numHits-1)


  # Wrap HIT into an ExternalQuestion form and send it to MTurk
  locations.each do |location|
    link = base + location
    p link
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

def getHITUrl( hitTypeId )
  if @mturk.host =~ /sandbox/
    "http://workersandbox.mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Sandbox Url
  else
    "http://mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Production Url
  end
end

USAGE = 'USAGE: publish_external [NUM HITs] [REWARD] [BASE_URL]'

if ARGV.length > 0 && ARGV[0] == '--help'
  puts USAGE
  exit(0)
end

createNewExternalQuestion
