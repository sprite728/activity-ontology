#!/usr/bin/env ruby

# Publish a HIT for each activity + 10 locations

require 'ruby-aws'
@mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Sandbox

# Use this line instead if you want the production website.
#@mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Production


def createNewHIT
  title = "Location Based Activity Recognition"
  desc = "Each individual question will refer to a particular activity. Select all locations in which the activity CANNOT take place."
  keywords = "activity, recognition, location, mapping"
  numAssignments = 1
  rewardAmount = 0.02 # 2 cents

  # Define the location of the externalized question (QuestionForm) file.
  rootdir = File.dirname $0
  questionFile = rootdir + "/questions.question"

  # Load the question (QuestionForm) file
  question = File.read( questionFile )
  questions = question.split(%r{^=+})[1..3]

  questions.each do |q|
    result = @mturk.createHIT( :Title => title,
                              :Description => desc,
                              :MaxAssignments => numAssignments,
                              :Reward => { :Amount => rewardAmount,
                                           :CurrencyCode => 'USD' },
                              :Question => q.strip(),
                              :Keywords => keywords )

    puts "Created HIT: #{result[:HITId]}"
    puts "HIT Location: #{getHITUrl( result[:HITTypeId] )}"
  end
end

def getHITUrl( hitTypeId )
  if @mturk.host =~ /sandbox/
    "http://workersandbox.mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Sandbox Url
  else
    "http://mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Production Url
  end
end

createNewHIT
