#!/usr/bin/python

"""Publish HITs.

Usage:
    publish_hits.py [--hits=<h>] [--batches=<b>] [(-p | --production)]
    publish_hits.py (-h | --help)
    publish_hits.py --version

Options:
    -h --help           Print this screen.
    --version           Prints the version.

    --hits=<h>          Number of hits to publish (0 means all HITs). [default: 0]
    --batches=<b>       Number of batches to publish. [default: 1]

    -p --production     Publish in production.
"""
from activity import Activity
from random import shuffle
from utils import split_every

from boto.mturk.connection import MTurkConnection
from docopt import docopt

import boto.mturk.question as boto
import boto.mturk.qualification as qual
import ConfigParser
import datetime

def create_question(locations, activities, hits):
    title = "Location Based Activity Recognition"
    desc = "Each individual question will refer to a particular activity. Select all locations in which the activity cannot take place."
    keywords = "activity, location, activity recognition, location mapping"

    def write_checkbox_question(activity):
        question = boto.QuestionContent()
        question.append(boto.FormattedContent('For the following activity, select the locations in which it <b>cannot</b> take place.' +
                                              ' Note that you should only leave a location unchecked if you have a strong belief that the activity' +
                                              ' is possible there. As an example, the activity <strong>Removed Braces</strong> is strongly' +
                                              ' connected to either <i>Dentist</i>, or <i>Hospital</i>. While it is possible to remove braces' +
                                              ' in a different location, one would not normally do so.'))
        question.append(boto.FormattedContent('If you believe that the activity can regularly occur in each location on the list, do not' +
                                              ' select any of the options.'))
        question.append(boto.FormattedContent('<strong>' + activity.label + '</strong>: ' + activity.description))
        answer = boto.SelectionAnswer(min=0, max=len(locations),
                                      style='checkbox',
                                      selections = zip(locations, locations),
                                      type='text',
                                      other=False)

        return boto.Question(identifier=activity.label,
                             content=question,
                             answer_spec=boto.AnswerSpecification(answer),
                             is_required=False)

    def write_honeypot_categories(activity, categories):
        shuffle(categories)
        categories = [""] + categories

        question = boto.QuestionContent()
        question.append_field('Text', 'What is the best classification of this activity?')
        question.append(boto.FormattedContent('As an example, the activity <b>Overcame Illness</b> is best classified as <i>Health &amp; Wellbeing</i>, while <b>Study Abroad</b> is best classified as <i>Work &amp; Education</i>.'))
        answer = boto.SelectionAnswer(min=1, max=1,
                                      style='dropdown',
                                      selections=zip(categories, categories),
                                      type='text',
                                      other=False)

        return boto.Question(identifier='honeypot_category-' + activity.label,
                             content=question,
                             answer_spec=boto.AnswerSpecification(answer),
                             is_required=False)

    def write_honeypot_activities(activity, prev1, prev2):
        h_activities = [(prev1.label, prev1.label), (prev2.label, prev2.label),
                        (activity.label, activity.label)]

        question = boto.QuestionContent()
        question.append_field('Text', 'What activity is the question about?')
        answer = boto.SelectionAnswer(min=1, max=1,
                                      style='dropdown',
                                      selections=h_activities,
                                      type='text',
                                      other=False)

        return boto.Question(identifier='honeypot_activity-' + activity.label,
                             content=question,
                             answer_spec=boto.AnswerSpecification(answer),
                             is_required=False)

    categories = ['Travel &amp; Experiences', 'Health &amp; Wellbeing',
                  'Home &amp; Living', 'Family &amp; Relationships',
                  'Work &amp; Education']
    for i, activity in enumerate(activities):
        if i >= hits:
            break

        qualifications = qual.Qualifications()
        qualifications.add(qual.LocaleRequirement('EqualTo', 'US'))
        qualifications.add(qual.PercentAssignmentsApprovedRequirement('GreaterThanOrEqualTo', '95'))
        qualifications.add(qual.NumberHitsApprovedRequirement('GreaterThanOrEqualTo', '100'))

        question_form = boto.QuestionForm()
        question_form.append(write_checkbox_question(activity))
        question_form.append(write_honeypot_categories(activity, categories))
        question_form.append(write_honeypot_activities(activity,
                                                       activities[i-1],
                                                       activities[i-2]))

        mtc.create_hit(questions=question_form,
                       qualifications=qualifications,
                       max_assignments=1,
                       title=title,
                       description=desc,
                       keywords=keywords,
                       approval_delay=datetime.timedelta(seconds=24*60*60),
                       duration=datetime.timedelta(seconds=3600),
                       reward=0.02)

def create_questions(args):
    def parse_string(loc):
        __special = {'rv_park': 'RV park', 'atm': 'ATM'}
        if loc in __special:
            loc = __special[loc]
        else:
            loc = loc.replace('_', ' ').capitalize()

        return loc

    batches = int(args['--batches'])
    hits = int(args['--hits'])

    for _ in xrange(batches):
        activities = []
        with open('../etc/label_description.txt', 'r') as f:
            lines = f.readlines()
            label_desc = [x.strip() for x in lines]

            for i in range(0, len(label_desc), 3):
                activities.append(Activity(label_desc[i], label_desc[i+1], label_desc[i+2]))

        with open('../etc/locs.txt') as f:
            lines = f.readlines()
            locations = [parse_string(line.strip()) for line in lines]

        if hits == 0:
            hits = len(activities)

        shuffle(locations)
        shuffle(activities)
        for piece in split_every(12, locations):
            create_question(list(piece), activities, hits)


if __name__ == '__main__':
    arguments = docopt(__doc__, version='1.0-SNAPSHOT')
    if arguments['--production']:
        HOST = "mechanicalturk.amazonaws.com"
    else:
        HOST = "mechanicalturk.sandbox.amazonaws.com"

    config = ConfigParser.ConfigParser()
    config.read('login.ini')
    access_key = config.get('DEFAULT', 'access_key')
    secret_key = config.get('DEFAULT', 'secret_key')

    mtc = MTurkConnection(aws_access_key_id=access_key,
                          aws_secret_access_key=secret_key,
                          host=HOST)

    create_questions(arguments)
