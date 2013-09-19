#!/usr/bin/python

"""Show rejected HITs.

Usage:
    get_rejected.py [(-p | --production)]
    get_rejected.py (-h | --help)
    get_rejected.py --version

Options:
    -h --help           Print this screen.
    --version           Prints the version.

    -p --production     Read from production.
"""
from boto.mturk.connection import MTurkConnection
from docopt import docopt

import ConfigParser

def get_rejected(mtc, filename):
    with open(filename) as f:
        ids = f.readlines()

    for assignmentId in ids:
        assignment = mtc.get_assignment(assignmentId)
        print '-------------------------------------------------'
        print 'Worker Id: {0}'.format(assignment[0].WorkerId)
        print 'HIT Id: {0}'.format(assignment[0].HITId)
        for answer in assignment[0].answers[0]:
            print answer.qid, answer.fields

if __name__ == '__main__':
    arguments = docopt(__doc__, version='1.0-SNAPSHOT')
    if arguments['--production']:
        HOST = "mechanicalturk.amazonaws.com"
        filename = 'rejected.csv'
    else:
        HOST = "mechanicalturk.sandbox.amazonaws.com"
        filename = 'rejected.csv'

    config = ConfigParser.ConfigParser()
    config.read('login.ini')
    access_key = config.get('DEFAULT', 'access_key')
    secret_key = config.get('DEFAULT', 'secret_key')

    mtc = MTurkConnection(aws_access_key_id=access_key,
                          aws_secret_access_key=secret_key,
                          host=HOST)

    get_rejected(mtc, filename)

