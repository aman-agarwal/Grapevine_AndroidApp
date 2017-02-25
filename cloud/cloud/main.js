// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("fetch", function(request, response) {
    var num_feed;
    if (!request.params.num_feed) {
        num_feed = 100;
    } else {
        num_feed = request.params.num_feed;
    }
    var query = new Parse.Query("PhotoObj");
    query.limit(num_feed);
    query.ascending("createdAt");
    query.find({
        success: function(results) {
            response.success(results);
        },
        error: function(error) {
            response.error(error);
        }
    });
});


// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("fetch_grid", function(request, response) {
    var user = new Parse.User();
    if (!request.params.user)
        response.error("No User");
    user.id = request.params.user;
    var query = new Parse.Query("PhotoObj");
    query.EqualTo("author", user);
    query.descending("createdAt");
    query.find({
        success: function(results) {
            response.success(results);
        },
        error: function(error) {
            response.error(error);
        }
    });
});