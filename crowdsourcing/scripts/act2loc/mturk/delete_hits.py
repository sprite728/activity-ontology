#!/usr/bin/env python

"""Delete HITs.

Usage:
    delete_hits.py [(-p | --production)]
    delete_hits.py (-h | --help)
    delete_hits.py --version

Options:
    -h --help           Show this screen.
    --version           Show version.

    -p --production     Publish in production.
"""
from boto.mturk.connection import MTurkConnection
from docopt import docopt

import ConfigParser

def delete_hits(hits):
    for hit in hits:
        mtc.disable_hit(hit.HITId)

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
    delete_hits(mtc.get_all_hits())
