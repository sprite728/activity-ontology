var predicates = [];

// Load predicates from a static file.
$(function() {
    $.getJSON($SCRIPT_ROOT + '/static/data/predicates.json', function(data) {
        $.each(data, function(key, val) {
            predicates.push(val);
        });
    });
});