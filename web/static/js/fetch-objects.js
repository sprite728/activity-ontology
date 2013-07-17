/*
 * Fetch objects from by executing an AJAX query
 */

var fetch_objects = function() {
    $.getJSON($SCRIPT_ROOT + '/_load_objects', {
        prefix: $('#object').val(),
        filename: 'objects.json'
    }, function(data) {
        $.each(data, function(key, val) {
            objects.push(val);
        });
    });
};

var object_field = $('#object')[0];
object_field.onkeyup = function() {
    if (object_field.value.length == 2) {
        $(function() {
            fetch_objects();
        });
    } else if (object_field.value.length < 2) {
        objects.length = 0;
    }
};