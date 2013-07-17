/**
 * Binary search implementation. Takes a value and returns an index in the
 * array at which the value is found, or -1 if the element is not in the 
 * array.
 *
 * @param value Value to search for in the array.
 * @param {Function} comparison Comparison function for values.
 * @return {number} Index of the element if found, or -1.
 */
Array.prototype.binarySearch = function(value, comparison) {
    comparison = typeof comparison != 'undefined' ? 
                        comparison : 
                        function(a, b) {
                            return a < b;
                        };

    var lo = 0, hi = this.length - 1;

    while (lo < hi) {
        var mid = Math.floor(lo + (hi - lo) / 2);
        if (this[mid] < value) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }

    if (lo == hi && this[lo] == value)
        return lo;

    return -1;
};