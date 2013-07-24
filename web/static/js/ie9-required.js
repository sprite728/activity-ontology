$('#submit-form').submit(function() {
    var emptyPred = $('#predicate').val().length === 0;
    var emptyObj = $('#object').val().length === 0; 
    var emptyDur = $('#duration').prop('selectedIndex') === -1;
    var emptyTime = $('#time').val() === null;

    var emptyMore = true;
    var i = 0;
    var radios = document.getElementsByName("more");
    while (emptyMore && i < radios.length) {
       if (radios[i].checked)
          emptyMore = false;
       ++i;
    }

    var isFormValid = !emptyObj && !emptyPred && 
          !emptyDur && !emptyTime && !emptyMore;

    if (!isFormValid) {
        alert("Please fill in all the fields!");
    }

    return isFormValid;
});