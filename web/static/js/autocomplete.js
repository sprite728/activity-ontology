/*
 * Autocomplete on two textfields using Bootstrap Typeahead.
 * 
 * Need to add support for many more objects.
 */

var min_len = 0

$(document).ready(function() {
    $('#predicate').typeahead({
        minLength: min_len,
        source: function(query, process) {
            process(predicates);
        }
    }).blur(function() {
        if (predicates.binarySearch($(this).val()) === -1) {
            $('#predicate').val('');
        }
    });
});

$(document).ready(function() {
    $('#object').typeahead({
        minLength: min_len,
        source: function(query, process) {
            process(objects);
        }
    }).blur(function() {
        if ($.inArray($(this).val(), objects) === -1) {
            $('#object').val('');
        }
    });
});