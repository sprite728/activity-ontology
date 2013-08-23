#!/usr/bin/env ruby

require 'docopt'
require 'ruby-aws'

doc = <<DOCOPT
Publish HITs.

Usage:
  #{__FILE__} [--hits=<h>] [--batches=<b>] [--reward=<r>] [--url=<u>] [-p | --production]
  #{__FILE__} -h | --help
  #{__FILE__} --version

Options:
  -h --help         Show this screen.
  --version         Show the version.

  --hits=<h>        Number of HITs to publish [default: 0]
  --batches=<b>     Number of batches [default: 1]
  --reward=<r>      Reward in dollars [default: 0.03]
  --url=<u>         Base url of HIT [default: http://crowd.fooshed.net/location2/]

  -p --production   Publish HITs in production.

DOCOPT

def createNewExternalQuestion(args)
  title = "Location Based Activity Recognition"
  desc = "Inferring activities (e.g., eating food, going for a walk, etc.) from" +
         " the locations in which they might take place"
  keywords = "activity, recognition, location, mapping"

  hits = args['--hits'].to_i
  batches = args['--batches'].to_i
  reward = args['--reward'].to_f

  # Publish in batches
  batches.times do
    locations = IO.read("../data/locs.txt").split()
    locations.map! { |x| x.strip() }

    locations.sort_by! { rand }
    # Publish only numHits HITs
    locations = locations.slice(0..hits-1)


    # Wrap HIT into an ExternalQuestion form and send it to MTurk
    locations.each do |location|
      link = args['--url'] + location
      question = "<ExternalQuestion xmlns=\"http://mechanicalturk" +
        ".amazonaws.com/AWSMechanicalTurkDataSchemas/" +
        "2006-07-14/ExternalQuestion.xsd\">\n"
      question += "<ExternalURL>" + link + "</ExternalURL>\n"
      question += "<FrameHeight>600</FrameHeight>\n"
      question += "</ExternalQuestion>\n"

      result = @mturk.createHIT(:Title => title,
                                :Description => desc,
                                :MaxAssignment => 1,
                                :Reward => { :Amount => reward,
                                             :CurrencyCode => 'USD' },
                                :Question => question.strip(),
                                :Keywords => keywords )

      puts "Created HIT: #{result[:HITId]}"
      puts "HIT Location: #{getHITUrl(result[:HITTypeId])}"
    end
  end
end

def getHITUrl( hitTypeId )
  if @mturk.host =~ /sandbox/
    "http://workersandbox.mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Sandbox Url
  else
    "http://mturk.com/mturk/preview?groupId=#{hitTypeId}"   # Production Url
  end
end

# Parse positional arguments and call create function
begin
  args = Docopt::docopt(doc, version: '0.2')
rescue Docopt::Exit => e
  puts e.message
end

if args
  if args['--production']
    @mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Production
  else
    @mturk = Amazon::WebServices::MechanicalTurkRequester.new :Host => :Sandbox
  end

  createNewExternalQuestion(args)
end
