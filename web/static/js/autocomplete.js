// Typeahead for predicates
$(document).ready(function() {
    $('#predicate').typeahead({
        minLength: 1,
        source: function(query, process) {
            var predicate_s = $('#predicate').val();
            if (predicate_s.length === 1) {
                fetchPredicates();
            }
            process(predicates);
        }
    }).blur(function() {
        if (predicates.binarySearch($(this).val()) === -1) {
            $('#predicate').val('');
        }
    });
});

// Typeahead for objects. Objects are dynamically loaded once the user has
// typed at least prefixLength characters.
var prefixLength = 3;
$(document).ready(function() {
    $('#object').typeahead({
        minLength: prefixLength,
        source: function(query, process) {
            var object_s = $('#object').val();
            if (object_s.length === prefixLength) {
                fetchObjects();
            }
            process(objects);
        }
    }).blur(function() {
        if ($.inArray($(this).val(), objects) === -1) {
            $('#object').val('');
        }
    });
});
