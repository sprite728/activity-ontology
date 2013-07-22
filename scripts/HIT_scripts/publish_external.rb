#!/usr/bin/env ruby

require 'ruby-aws'
@mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Production

def createNewExternalQuestion
  title = "Location Based Activity Recognition"
  desc = "Inferring activities (e.g., eating food, going for a walk, etc.) from" +
         " the locations in which they might take place"
  keywords = "activity, recognition, location, mapping"
  numAssignments = 1
  numHits = 50
  rewardAmount = 0.05 

  base = 'http://crowd.fooshed.net/location/'
  locations = IO.read("misc/locs.txt").split()
  locations.map! { |x| x.strip() }

  # Change next line to publish all
  locations.sort_by! { rand }
  locations.slice!(numHits..-1)

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

createNewExternalQuestion
