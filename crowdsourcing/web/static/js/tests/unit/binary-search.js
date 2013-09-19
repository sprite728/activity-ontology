$(function() {

    module("binary-search")

    test("should return -1", function() {
        var arr = [1, 2, 3, 4, 5, 7, 8, 9, 10, 100];
        equal(arr.binarySearch(6), -1);
        equal(arr.binarySearch(101), -1);
        equal(arr.binarySearch(11), -1);
    });

    test("should return same as $.inArray", function() {
        var arr = [1, 2, 3, 4, 5, 6, 7, 8, 100, 2132, 2132, 4444];
        equal(arr.binarySearch(3), $.inArray(3, arr));
        equal(arr.binarySearch(6), $.inArray(6, arr));
        equal(arr.binarySearch(2132), $.inArray(2132, arr));
    });

    test("test on random arrays", function() {
        var arr = [];
        for (var i = 0; i < 10000; ++i) {
            arr.push(Math.floor(1000000 * Math.random()));
        }
        arr.sort(function(a, b) {
            return a - b;
        });

        for (var i = 0; i < 50; ++i) {
            id = Math.floor(Math.random() * 10000);
            equal(arr.binarySearch(arr[id]), $.inArray(arr[id], arr));
        }
    });
})