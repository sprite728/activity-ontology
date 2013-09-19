#!/usr/bin/env python

"""Approve HITs.

Usage:
    approve_hits.py [(-p | --production)]
    approve_hits.py (-h | --help)
    approve_hits.py --version

Options:
    -h --help           Show this screen.
    --version           Show version.

    -p --production     Publish in production.
"""
from boto.mturk.connection import MTurkConnection
from docopt import docopt

import ConfigParser
import datetime
import logging

def approve_hits(hits):
    category = {}
    with open('../etc/label_description.txt', 'r') as f:
        lines = [line.strip() for line in f.readlines()]
        for i in xrange(0, len(lines), 3):
            category[lines[i]] = lines[i + 2].replace('&amp;', '&')

    incorrect = 0
    category_correct = 0
    activity_correct = 0
    total = 0

    f = open('results5.csv', 'w')
    for hit in hits:
        assignments = mtc.get_assignments(hit.HITId)

        for assignment in assignments:
            if assignment.AssignmentStatus == 'Approved':
                continue
            if assignment.AssignmentStatus == 'Rejected':
                continue

            answer_dict = {}
            for answer in assignment.answers[0]:
                answer_dict[answer.qid] = answer.fields
                if 'honeypot' not in answer.qid:
                    activity = answer.qid

            honeypot_activity_key = 'honeypot_activity-' + activity
            honeypot_category_key = 'honeypot_category-' + activity

            try:
                if answer_dict[honeypot_category_key][0] == category[activity]:
                    category_correct += 1
                    mtc.approve_assignment(assignment.AssignmentId)
                    f.write(activity + ',' + ','.join(answer_dict[activity]) + ',category_correct,' + assignment.AssignmentId + '\n')
                elif answer_dict[honeypot_activity_key][0] == activity:
                    activity_correct += 1
                    mtc.approve_assignment(assignment.AssignmentId)
                    f.write(activity + ',' + ','.join(answer_dict[activity]) + ',activity_correct,' + assignment.AssignmentId + '\n')
                else:
                    incorrect += 1
                    mtc.reject_assignment(assignment.AssignmentId, feedback='Did not answer either of the last two questions correctly.')
                    f.write(activity + ',' + ','.join(answer_dict[activity]) + ',incorrect,' + assignment.AssignmentId + '\n')
                total += 1
            except:
                pass
    f.close()

    logging.basicConfig(filename='log/' + str(datetime.datetime.now()) + '.log', level=logging.INFO)
    logging.info('Category correct: {0}'.format(category_correct))
    logging.info('Activity correct: {0}'.format(activity_correct))
    logging.info('Incorrect: {0}'.format(incorrect))
    logging.info('Total: {0}'.format(total))

if __name__ == '__main__':
    arguments = docopt(__doc__, version='1.0-SNAPSHOT')
    if arguments['--production']:
        HOST = 'mechanicalturk.amazonaws.com'
    else:
        HOST = 'mechanicalturk.sandbox.amazonaws.com'

    config = ConfigParser.ConfigParser()
    config.read('login.ini')
    access_key = config.get('DEFAULT', 'access_key')
    secret_key = config.get('DEFAULT', 'secret_key')

    mtc = MTurkConnection(aws_access_key_id=access_key,
                          aws_secret_access_key=secret_key,
                          host=HOST)

    approve_hits(mtc.get_all_hits())
