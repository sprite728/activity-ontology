// Load predicates from a static file.
var fetchPredicates = function() {
    $.getJSON($SCRIPT_ROOT + '/_load_predicates', {
        object: $('#object').val()
    }, function(data) {
        predicates.length = 0;
        $.each(data, function(key, val) {
            predicates.push(val);
        });
    });
};
