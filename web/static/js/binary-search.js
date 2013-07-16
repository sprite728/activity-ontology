Array.prototype.binarySearch = function(value) {
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