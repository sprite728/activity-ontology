/*
 * Autocomplete on two textfields using Bootstrap Typeahead.
 * 
 * Need to add support for many more objects.
 */

$(document).ready(function() {
    $('#predicate').typeahead({
        minLength: 3,
        source: function(query, process) {
            process(predicates);
        }
    }).blur(function() {
        if ($.inArray($(this).val(), predicates) === -1) {
            $('#predicate').val('');
        }
    });
});

$(document).ready(function() {
    $('#object').typeahead({
        minLength: 3,
        source: function(query, process) {
            process(objects);
        }
    }).blur(function() {
        if ($.inArray($(this).val(), objects) === -1) {
            $('#object').val('');
        }
    });
});