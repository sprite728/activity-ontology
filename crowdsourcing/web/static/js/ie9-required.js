$('#submit-form').submit(function() {
    var emptyPred = $('#predicate').val().length === 0;
    var emptyObj = $('#object').val().length === 0;
    var emptyDur = $('#duration').prop('selectedIndex') === -1;

    // Check "time" checkboxes are not empty
    var emptyTime = true;
    var i = 0;
    var checkboxes = document.getElementsByName("time");
    while (emptyTime && i < checkboxes.length) {
        if (checkboxes[i].checked) {
            emptyTime = false;
        }
        ++i;
    }

    // Check "more" radio field is not empty
    var emptyMore = true;
    i = 0;
    var radios = document.getElementsByName("more");
    while (emptyMore && i < radios.length) {
       if (radios[i].checked) {
          emptyMore = false;
       }
       ++i;
    }

    var isFormValid = !emptyObj && !emptyPred &&
          !emptyDur && !emptyTime && !emptyMore;

    if (!isFormValid) {
        alert("Please fill in all the fields.");
    }

    return isFormValid;
});
