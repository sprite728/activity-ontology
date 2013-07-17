// Fetch objects from file; will be extended to fetch them using a 
// query to Redis
var fetchObjects = function() {
    $.getJSON($SCRIPT_ROOT + '/_load_objects', {
        prefix: $('#object').val()
    }, function(data) {
        $.each(data, function(key, val) {
            objects.push(val);
        });
    });
};

// Clears the objects array. This is to prevent the user from seeing 
// duplicates
var objectField = $('#object')[0];
objectField.onkeyup = function() {
    if (objectField.value.length < 2) {
        objects.length = 0;
    }
};