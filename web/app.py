from flask import Flask, render_template, request, session, g, redirect
from flask import url_for, abort, flash, _app_ctx_stack, jsonify
import json

app = Flask(__name__)
app.config.from_object('config')


@app.route('/about')
def about():
    return render_template('about.html')


@app.route('/location/<location>')
def location(location):
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
        """ Abort if the location is unknown """
        abort(404)

    try:
        flash('HitId: ' + request.args.get('hitId'))
        flash('AssignmentId: ' + request.args.get('assignmentId'))
    except:
        pass

    return render_template('location.html', location=location, description=description)


@app.route('/submit', methods=['POST'])
def submit():
    """ Placeholder submission form until we run MTurk """
    pred = request.form['predicate']
    obj = request.form['object']
    loc = request.form['location']
    
    if not pred or not obj:
        flash('Fields cannot be empty')
        return redirect(url_for('location', location=loc))

    flash('Predicate: ' + pred)
    flash('Object: ' + obj)
    flash('Location: ' + loc)
    return render_template('submit.html')


@app.route('/_load_objects')
def load_objects():
    prefix = request.args.get('prefix')
    filename = request.args.get('filename')

    """ Do a query using the prefix, and jsonify the results.
    Then send the data."""
    with app.open_resource('static/data/' + filename) as f:
        data = json.loads(f.read());

    return json.dumps(data)


if __name__ == '__main__':
    app.run(debug=True)