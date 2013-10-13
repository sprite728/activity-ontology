// Fetch objects given a prefix
var fetchObjects = function() {
    $.getJSON($SCRIPT_ROOT + '/_load_objects', {
        prefix: $('#object').val(),
        predicate: $('#predicate').val()
    }, function(data) {
        objects.length = 0;
        $.each(data, function(key, val) {
            objects.push(val);
        });
    });
};