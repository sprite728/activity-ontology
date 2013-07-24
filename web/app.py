from flask import Flask, render_template, request, session, g, redirect
from flask import url_for, abort, flash, _app_ctx_stack, jsonify, make_response
from werkzeug.contrib.fixers import ProxyFix

import complete
import gzip
import json
import redis
import StringIO
import sys
import yaml

app = Flask(__name__)
app.config.from_object('config')


@app.route('/location/<location>')
def location_first_experiment(location):
    locations, descriptions = get_locations()

    if location in locations:
        description = descriptions[locations.index(location)]
    else:
        # Abort if the location is unknown
        abort(404)

    hitId, assignmentId, workerId, turkSubmitTo = get_arguments()

    return render_template('location.html', location=location, 
        description=description, hitId=hitId, assignmentId=assignmentId,
        workerId=workerId, turkSubmitTo=turkSubmitTo)


@app.route('/location2/<location>')
def location_second_experiment(location):
    locations, descriptions = get_locations()

    if location in locations:
        description = descriptions[locations.index(location)]
    else:
        # Abort if the location is unknown
        abort(404)

    responses = get_previous_responses(location)

    hitId, assignmentId, workerId, turkSubmitTo = get_arguments()

    return render_template('location2.html', location=location, 
        description=description, hitId=hitId, assignmentId=assignmentId,
        workerId=workerId, turkSubmitTo=turkSubmitTo, responses=responses)


@app.route('/_load_objects')
def load_objects():
    r = redis.StrictRedis(host='localhost', port=6379, db=0)

    # Query using the prefix; return the list as JSON.
    prefix = request.args.get('prefix').lower()
    data = complete.complete(r, prefix, db='dbpedia')
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


def get_previous_responses(location):
    """Returns a list of responses given by other MTurkers for this location

    """
    with app.open_resource('static/data/previous_responses.yaml') as f:
        data = yaml.load_all(f)

        return list(data)


def get_locations():
    """Returns the Google Places location types and descriptions.
    
    Returns:
        Two lists, the first with all the location labels, and the second
        with the descriptions for each location
    """
    with app.open_resource('static/data/label_desc.txt') as f:
        lines = f.readlines()
        locations = [lines[i].strip() for i in range(0, len(lines), 2)]
        descriptions = [lines[i].strip() for i in range(1, len(lines), 2)]

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
    app.run(debug=False)
