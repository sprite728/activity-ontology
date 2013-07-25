var predicates = [];

// Load predicates from a static file.
var fetchPredicates = function() {
    $.getJSON($SCRIPT_ROOT + '/_load_predicates', {
		object: $('#object').val()
	}, function(data) {
        $.each(data, function(key, val) {
            predicates.push(val);
        });
    });
};
