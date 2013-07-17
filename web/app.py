from flask import Flask, render_template, request, session, g, redirect
from flask import url_for, abort, flash, _app_ctx_stack, jsonify

import complete
import json
import redis
import sys

app = Flask(__name__)
app.config.from_object('config')


@app.route('/about')
def about():
    return render_template('about.html')


@app.route('/location/<location>')
def location(location):
    # Process query for a location; deals with all the MTurk arguments. 
    locations = []
    descriptions = []
    with app.open_resource('static/data/label_desc.txt') as f:
        lines = f.readlines()
        locations = [lines[i].strip() for i in range(0, len(lines), 2)]
        descriptions = [lines[i].strip() for i in range(1, len(lines), 2)]

    description = ""
    if location in locations:
        description = descriptions[locations.index(location)]
    else:
        # Abort if the location is unknown
        abort(404)

    # Process arguments; namely set values for needed ids
    auxHitId = request.args.get('hitId')
    auxId = request.args.get('assignmentId')
    auxWorkId = request.args.get('workerId')
    auxSubmit = request.args.get('turkSubmitTo')

    hitId = auxHitId if auxHitId else 'HIT_ID_NOT_AVAILABLE'
    assignmentId = auxId if auxId else 'ASSIGNMENT_ID_NOT_AVAILABLE'
    workerId = auxWorkId if auxWorkId else 'WORKER_ID_NOT_AVAILABLE'

    turkSubmitTo = auxSubmit if auxSubmit else 'https://www.mturk.com'
    turkSubmitTo += '/mturk/externalSubmit'

    return render_template('location.html', location=location, 
        description=description, hitId=hitId, assignmentId=assignmentId,
        workerId=workerId, turkSubmitTo=turkSubmitTo)


@app.route('/_load_objects')
def load_objects():
    # Open Redis connection; change/replace/remove this line for production
    r = redis.StrictRedis(host='localhost', port=6379, db=0)
    # Preprocess Redis data
    complete.preprocess(r, open('static/data/instances.txt'), 'dbpedia')

    # Query using the prefix; return the list as JSON.
    prefix = request.args.get('prefix')
    data = complete.complete(r, prefix, db='dbpedia')

    return json.dumps(data)


if __name__ == '__main__':
    # Uncomment the next line before deployment
    # app.host = '0.0.0.0'
    app.run(debug=True)