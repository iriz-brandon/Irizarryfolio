var _ = require('underscore');//implements the underscore library

//This file defines two Parse methods
Parse.Cloud.define("deletePost", function(request, response) {	
	console.log(request.params.newsPostID);
	Parse.Cloud.useMasterKey();
	var newsFeedID =  request.params.newsPostID;
	console.log("The newsFeedObj is " + newsFeedID);
	deleteEverythingOnPostThenPost(request, response, newsFeedID);    
}); 

Parse.Cloud.define("deleteCommentWithNotifications", function(request, response) {
	console.log(request.params.commentID);
	Parse.Cloud.useMasterKey();
	var commentID =  request.params.commentID;
	console.log("The commentObj is " + commentID);
	singleCommentDeletion(request, response, commentID);    
});

/* The functions from exports.deleteEverythingOnPost until updateCheckIncount is 
 * the method for deleting the news-feed post, called by the function deletePost */
function deleteEverythingOnPostThenPost(request, response, newsFeedID) {	

	/* start with deleting the flags */
    var Flags = Parse.Object.extend("GlobalFlags");
    var  flagsQuery = new Parse.Query(Flags);
    
    flagsQuery.equalTo("flaggedNewsObjectId", newsFeedID);
    
    flagsQuery.find().then(function(flagsObjects)	{
    	var promise = deleteObjectSelection(flagsObjects);
		return Parse.Promise.when(promise);
		
	//Delete the news feed notifications (old first)	
	}).then(function() {
		var Notification = Parse.Object.extend("GlobalNotifications");
		var notificationQueryOld = new Parse.Query(Notification);
		notificationQueryOld.equalTo("NewsFeedObject", newsFeedID);
		return notificationQueryOld.find();
    }).then(function (notificationObjectsOld) {
 		var promise = deleteObjectSelection(notificationObjectsOld);
		return Parse.Promise.when(promise);
		
	//Now the newer version of news feed notifications
	}).then(function() {
   		var NewsFeed = Parse.Object.extend("GlobalNewsFeed");
       	var newsFeedQuery = new Parse.Query(NewsFeed);
       	return newsFeedQuery.get(newsFeedID);
    }).then(function (newsFeedObj) {
    	var Notification = Parse.Object.extend("GlobalNotifications");
    	var notificationQuery = new Parse.Query(Notification);
    	notificationQuery.equalTo("newsFeedPointer", newsFeedObj);
    	return notificationQuery.find();
    }).then(function (notificationObjects) {
    	var promise = deleteObjectSelection(notificationObjects);
		return Parse.Promise.when(promise);
		
	//Delete the news feed comments	
	}).then(function() {
		var Comment = Parse.Object.extend("UserComment");
        var commentQuery = new Parse.Query(Comment);
        commentQuery.equalTo("GlobalNewsFeed", newsFeedID);
        return commentQuery.find();
	}).then(function(commentObjects) {
		var promise = Parse.Promise.as();
		_.each(commentObjects, function(commentObj) {
			promise = promise.then(function() {
				return Parse.Cloud.run('deleteCommentWithNotifications', {commentID: commentObj.id});
		  	});
		});
		return Parse.Promise.when(promise);
		
	//Delete the news feed itself
	}).then(function() {
		var NewsFeed = Parse.Object.extend("GlobalNewsFeed");
		var newsFeedObjectQuery = new Parse.Query(NewsFeed);
		return newsFeedObjectQuery.get(newsFeedID);
    }).then(function(newsFeedObj) {
		var promise = Parse.Promise.as();
		promise = promise.then(function() {
		   	return newsFeedObj.destroy();
		});
		console.log("Deleted the newsFeed post with ID: " + newsFeedID);
		return promise;
		
	//Success/Error	
	}).then(function() {
	    response.success("deletePost was a success in delete.js!");
	    updateCheckInCount(request, response);
    },function(error){
    	response.error("deletePost was unsuccessful in delete.js!");
    });    
}

// Updates the Check-in count when a post is deleted
function updateCheckInCount(request, response){
	var NewsFeed = Parse.Object.extend("GlobalNewsFeed");
	var newsFeedObjectQuery = new Parse.Query(NewsFeed);
	var currentUser = Parse.User.current();
	var currentUserID;
	if(!currentUser){
		currentUserID = request.params.userID;
	}

	newsFeedObjectQuery.equalTo("CurrentAuthor", currentUserID);
	newsFeedObjectQuery.equalTo("WineType", "Wine");

	newsFeedObjectQuery.count({
		success: function(count){
			currentUser.set("checkIns", count);

			currentUser.save(null, {
				success: function(User){
					response.success("Deleted Post Successfully and updated user checkin count in delete.js!");
				}, error: function(user, error){
					response.error("Could not update the checkin Count in delete.js !");
				}
			});
		},
		error: function(error){
			response.error("Post could not be found.");
		}
	});
}
/* End News Feed Deletion. Now starting single comment deletion with connected new and old notifications*/

/* Start Single Comment Deletion */
function singleCommentDeletion (request, response, commentID) {
	var commentObjct; //declares a holder for the comment
	var newsFeedObjct; //declares a holder for the news feed
	
	var Comment = Parse.Object.extend("UserComment");
	var commentQuery = new Parse.Query(Comment);

	//Starts a promise chain to delete all comment objects connected, then the comment
	
	//Starts By deleting the new version of the comments notifications
	commentQuery.get(commentID).then(function(commentObj) {
		commentObjct = commentObj; //assigns the out of scope comment holder
		var Notification = Parse.Object.extend("UserNotification");
		var newNotificationQuery = new Parse.Query(Notification);
		newNotificationQuery.equalTo("commentPointer", commentObj);
		return newNotificationQuery.find()
	}).then(function(notificationObjects) {
		var promise = deleteObjectSelection(notificationObjects);
		return Parse.Promise.when(promise);
			
	//Now The Old Comment Notifications
	}).then(function() {
			console.log("2: old notifications");
		    var NewsFeed = Parse.Object.extend("GlobalNewsFeed");
			var newsFeedObjectQuery = new Parse.Query(NewsFeed);
			var commentNewsFeedID = commentObjct.get("GlobalNewsFeed");
			console.log("2: " + commentNewsFeedID);
			return newsFeedObjectQuery.get(commentNewsFeedID);
	}).then(function(commentNewsFeedObj) {
			console.log("2: query newsfeed for notification delete")
			newsFeedObjct = commentNewsFeedObj;
			var Notification = Parse.Object.extend("UserNotification");
			var oldNotificationQuery = new Parse.Query(Notification);
			var commentAuthorObj = commentObjct.get("Author");
			var commentTimeCreated = commentObjct.get("createdAt");

			oldNotificationQuery.equalTo("newsFeedPointer", commentNewsFeedObj);
			oldNotificationQuery.equalTo("Author", commentAuthorObj);
			oldNotificationQuery.equalTo("createdAt", commentTimeCreated);
			oldNotificationQuery.equalTo("isComment", "true");
			return oldNotificationQuery.find();
	}).then(function(oldNotificationObjects) {
		var promise = deleteObjectSelection(oldNotificationObjects);
		return Parse.Promise.when(promise);
			
	//Now update the news feed comment count containing the deleted comment	
	}).then(function() {
		console.log("2: notifications count update");
		var newsFeedCommentCount = newsFeedObjct.get("UserComments");
		var newsFeedCommentCountUpdated = newsFeedCommentCount - 1;

		newsFeedObjct.set("Comments", newsFeedCommentCountUpdated);
		var promise = Parse.Promise.as();
			promise = promise.then(function() {
				return newsFeedObjct.save();
			});
		return promise;

	//Now delete the comment itself
	}).then(function() {
		console.log("2: delete")
		var promise = Parse.Promise.as();
		promise = promise.then(function() {
			return commentObjct.destroy();
		});
		return promise;
		
	//success/error
	}).then(function() {
		response.success("Single Comment Deletion reached success in delete.js!");
	},function(error) {
		response.error("Could not delete single comment in delete.js!");
	});
}
/* End Single Comment delete */

/* The Global utility function for deleting sets of objects and returning a pass/fail promise */	
function deleteObjectSelection(objectsToDelete) {
	var promise = Parse.Promise.as();
	_.each(objectsToDelete, function(obj) {
		promise = promise.then(function() {
	    	return obj.destroy();
	  	});
	});
	return promise;
}
