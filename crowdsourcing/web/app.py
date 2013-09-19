from flask import Flask, render_template, request, session, g, redirect
from flask import url_for, abort, flash, _app_ctx_stack, jsonify, make_response
from werkzeug.contrib.fixers import ProxyFix

import codecs
import complete
import collections
import gzip
import json
import redis
import random
import StringIO
import sys
import yaml

app = Flask(__name__)
app.config.from_object('config')

illegal_predicates = collections.defaultdict(set)
illegal_objects = collections.defaultdict(set)


@app.route('/location/<location>')
def location_first_experiment(location):
    locations, descriptions = get_locations()

    if location in locations:
        description = descriptions[locations.index(location)]
    else:
        # Abort if the location is unknown
        abort(404)
    # Create honeypot for this location
    honeypot = make_honeypot(locations, location)

    hitId, assignmentId, workerId, turkSubmitTo = get_arguments()

    return render_template('location.html', location=location,
        description=description, honeypot=honeypot, hitId=hitId,
        assignmentId=assignmentId, workerId=workerId, turkSubmitTo=turkSubmitTo)


@app.route('/location2/<location>')
def location_second_experiment(location):
    locations, descriptions = get_locations()

    if location in locations:
        description = descriptions[locations.index(location)]
    else:
        # Abort if the location is unknown
        abort(404)
    # Create honeypot for this location
    honeypot = make_honeypot(locations, location)
    responses = get_previous_responses(location)

    hitId, assignmentId, workerId, turkSubmitTo = get_arguments()

    return render_template('location2.html', location=location,
        description=description, honeypot=honeypot, responses=responses,
        hitId=hitId, assignmentId=assignmentId, workerId=workerId, turkSubmitTo=turkSubmitTo)


@app.route('/_load_objects')
def load_objects():
    r = redis.StrictRedis(host='localhost', port=6379, db=0)

    predicate = request.args.get('predicate').lower()
    # Query using the prefix; return the list as JSON.
    prefix = request.args.get('prefix').lower()
    data = complete.complete(r, prefix, db='dbpedia')
    data = [d for d in data if d not in illegal_objects[predicate]]

    data.sort(key=lambda item: (len(item), item))

    gzip_buffer = StringIO.StringIO()
    gzip_file = gzip.GzipFile(mode='wb', compresslevel=4, fileobj=gzip_buffer)
    gzip_file.write(json.dumps(data))
    gzip_file.close()

    response = make_response()
    response.data = gzip_buffer.getvalue()
    response.headers['Content-Encoding'] = 'gzip'
    response.headers['Content-Length'] = len(response.data)

    return response


@app.route('/_load_objects2')
def load_objects2():
    # Get associated predicate
    predicate = request.args.get('predicate').lower()

    with app.open_resource('static/data/objects.json') as f:
        data = json.loads(f.read())
        data = [d for d in data if d not in illegal_objects[predicate]]

        data.sort(key=lambda item: (len(item), item))

        gzip_buffer = StringIO.StringIO()
        gzip_file = gzip.GzipFile(mode='wb', compresslevel=4, fileobj=gzip_buffer)
        gzip_file.write(json.dumps(data))
        gzip_file.close()

        response = make_response()
        response.data = gzip_buffer.getvalue()
        response.headers['Content-Encoding'] = 'gzip'
        response.headers['Content-Length'] = len(response.data)

        return response


@app.route('/_load_predicates')
def load_predicates():
    # Get associated object
    obj = request.args.get('object').lower()

    with app.open_resource('static/data/predicates.json') as f:
        data = json.loads(f.read())
        data = [d for d in data if d not in illegal_predicates[obj]]

        return json.dumps(data)

# Get locations for honeypot question
def make_honeypot(locations, location):
    random.shuffle(locations)
    i = locations.index(location)

    return [locations[i-2], locations[i-1], locations[i]]


def get_previous_responses(location):
    """Returns a list of responses given by other MTurkers for this location.

    Expects a CSV file where the first line represents the column names.

    Args:
        location: Location of current HIT, used to filter the previous
        responses

    Returns:
        A list of all the previous responses given by MTurk users.
    """
    with app.open_resource('static/data/previous.csv') as f:
        lines = f.readlines()
        keys = lines[0].split(',')

        data = []
        for line in lines[1:]:
            vals = line.split(',')
            d = {}
            for (k, v) in zip(keys, vals):
                d[k] = v

            if d['location'] == location:
                predicate = d['predicate'].strip().replace('_', ' ')
                obj = d['object'].strip().replace('_', ' ')

                data.append((predicate, obj))

        for datum in data:
            illegal_objects[datum[0]].add(datum[1])
            illegal_predicates[datum[1]].add(datum[0])

        return sorted(set(data))


def get_locations():
    """Returns the Google Places location types and descriptions.

    Returns:
        Two lists, the first with all the location labels, and the second
        with the descriptions for each location
    """
    charset='utf-8'
    with app.open_resource('static/data/label_desc.txt') as f:
        lines = f.readlines()
        locations = [lines[i].decode(charset).strip() for i in range(0, len(lines), 2)]
        descriptions = [lines[i].decode(charset).strip() for i in range(1, len(lines), 2)]

    return locations, descriptions


def get_arguments():
    """Returns the request arguments for a typical call from MTurk

    Returns:
        The parameters required for a HIT submission in MTurk: hitId,
            assignmentId, workerId, turkSubmitTo
    """
    # Process arguments for MTurk submission
    auxHitId = request.args.get('hitId')
    auxId = request.args.get('assignmentId')
    auxWorkId = request.args.get('workerId')
    auxSubmit = request.args.get('turkSubmitTo')

    hitId = auxHitId if auxHitId else 'HIT_ID_NOT_AVAILABLE'
    assignmentId = auxId if auxId else 'ASSIGNMENT_ID_NOT_AVAILABLE'
    workerId = auxWorkId if auxWorkId else 'WORKER_ID_NOT_AVAILABLE'

    turkSubmitTo = auxSubmit if auxSubmit else 'https://www.mturk.com'
    turkSubmitTo += '/mturk/externalSubmit'

    return hitId, assignmentId, workerId, turkSubmitTo


if __name__ == '__main__':
    app.wsgi_app = ProxyFix(app.wsgi_app)
    app.run(debug=True)
