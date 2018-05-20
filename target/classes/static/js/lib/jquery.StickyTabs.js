/** jQuery plugin to allow for saving and loading the state of Bootstrap tabs. */
;(function($) {

    $.fn.StickyTabs = function(options) {
        var settings = $.extend({
            scroll: true // Scroll to the top of the page on tab change
        }, options);

        context = this;

        // Load the tab by hash value
        var showTabFromHash = function() {
            var hash = window.location.hash;
            // Assumes the first tab is the default
            var selector = hash ? "a[href=\"" + hash + "\"]" : "li:first a";
            $(selector, context).tab("show");
            if (settings.scroll) {
                window.scrollTo(0, 0);
            }
        };
        showTabFromHash(context);

        // Update the tab on history change
        window.addEventListener("hashchange", showTabFromHash, false);

        // Update the history when a tab is changed
        $("a", context).on("click", function(e) {
            history.pushState(null, null, this.href);
        });

        return this;
    };

}(jQuery));