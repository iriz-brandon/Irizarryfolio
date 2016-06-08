var _ = require('underscore'); //implements the underscore library

//This file defines one Parse Method
Parse.Cloud.define("deleteAccount", function(request, response) {
    Parse.Cloud.useMasterKey();
    
    var deviceToken =  request.params.deviceToken;
    console.log("deviceToken in main: " + deviceToken);
        
    startDeletePromiseChain(request, response);
});

function startDeletePromiseChain (request, response) {
	//attempt at creating variable for use throughout scope of promise chain
	var currentUser = Parse.User.current();
	var currentUserID = currentUser.id;
	
	//start with deleting the user installations
	var Installation = Parse.Object.extend("CurrentInstallation");
	var installationObjectQuery = new Parse.Query(Installation);
	installationObjectQuery.equalTo("user", currentUser);

	installationObjectQuery.find().then(function(installObjects){
		var promise = deleteObjectSelection(installObjects);
		return Parse.Promise.when(promise);
		
	}).then(function() {
		//var userFriends = currentUser.get("friends"); // These are just user ids, not actual User Objects
		var query = new Parse.Query(Parse.User);
		query.equalTo("friends", currentUser.id); // Finds users that have currentUser.id on their friends array
		return query.find();
	// delete the user from all their friends' friend-lists	
	}).then(function(userFriends) { 
		//var userFriends = currentUser.get("friends"); // These are just user ids, not actual User Objects
		var promise = Parse.Promise.as();
		_.each(userFriends, function(friendOfUser) {
			promise = promise.then(function() {
				var friendOfUser_friendList = friendOfUser.get("friends").slice(0);
				var currentUserPositionInFriendList = friendOfUser_friendList.indexOf(currentUser.id);
				
				friendOfUser_friendList.splice(currentUserPositionInFriendList, 1);
				
				friendOfUser.set("friends", friendOfUser_friendList);
				return friendOfUser.save();
			});
		});
		return promise;
	//delete all the friend objects made to the user from others	
	}).then(function() {
		var Friendship = Parse.Object.extend("UserFriendships");
		var friendshipQuery = new Parse.Query(Friendship);

		friendshipQuery.equalTo("toUser", currentUser);
		return friendshipQuery.find();
	}).then(function(friendshipObjects){
		var promise = deleteObjectSelection(friendshipObjects);
		return Parse.Promise.when(promise);
		
	//delete all the friend objects made from the user to others	
	}).then(function() {
		var Friendship = Parse.Object.extend("UserFriendships");
		var friendshipQuery = new Parse.Query(Friendship);

		friendshipQuery.equalTo("fromUser", currentUser);
		return friendshipQuery.find();
	}).then(function(friendshipObjects){
		var promise = deleteObjectSelection(friendshipObjects);
		return Parse.Promise.when(promise);	
		
	//Delete The user's NewFeed Posts (calls delete.js)	
	}).then(function(){
		console.log("1: newsfeed");
		var NewsFeed = Parse.Object.extend("GlobalNewsFeed");
		var newsFeedQuery = new Parse.Query(NewsFeed);
		
		newsFeedQuery.equalTo("authorPointer", currentUser);
		return newsFeedQuery.find();
	}).then(function(newsFeedObjects){
		var promise = Parse.Promise.as();
		console.log("1: before the newsfeed");
		_.each(newsFeedObjects, function(nfobj) {
			promise = promise.then(function() {
				console.log("1: " + nfobj.id);
				return Parse.Cloud.run('deletePost', {newsPostID: nfobj.id, userID: currentUserID});
		  	});
		});
		return Parse.Promise.when(promise);
		
	//Deletes the Flags the User is the Author of	
	}).then(function(){
		var Flags = Parse.Object.extend("GeneratedFlags");
		var flagQuery = new Parse.Query(Flags);

		flagQuery.equalTo("Author", currentUser);
		return flagQuery.find();
	}).then(function(flagsObjects){
		var promise = deleteObjectSelection(flagsObjects);
		return Parse.Promise.when(promise);
		
	//Deletes the Flags the User is the Owner of	
	}).then(function() {
		console.log("1: flags");
		var Flags = Parse.Object.extend("GeneratedFlags");
		var flagQuery = new Parse.Query(Flags);

		flagQuery.equalTo("Owner", currentUser);
		return flagQuery.find();
	}).then(function(flagsObjs){
		var promise = deleteObjectSelection(flagsObjs);
		return Parse.Promise.when(promise);
		
	//Deletes all of the users comments(calls delete.js)
	}).then(function() {
		console.log("1: comments");
		var Comment = Parse.Object.extend("Comment");
		var commentsQuery = new Parse.Query(Comment);

		commentsQuery.equalTo("Author", currentUser);
		return commentsQuery.find();
	}).then(function(commentObjs) {
		var promise = Parse.Promise.as();
		_.each(commentObjs, function(commentObj) {
			promise = promise.then(function() {
				 return Parse.Cloud.run('deleteCommentWithNotifications', {commentID: commentObj.id});
		  	});
		});
		return Parse.Promise.when(promise);
		
	//Deletes the notifications the user is the author of	
	}).then(function(){
		console.log("1: notifications");
		var Notification = Parse.Object.extend("UserNotification");
		var notificationQuery = new Parse.Query(Notification);

		notificationQuery.equalTo("Author", currentUser);
		return notificationQuery.find();
	}).then(function(notificationObjects){
		var promise = deleteObjectSelection(notificationObjects);
		return Parse.Promise.when(promise);
		
	//Deletes the notifications the user is the owner of	
	}).then(function() {
		var Notification = Parse.Object.extend("UserNotification");
		var notificationQuery = new Parse.Query(Notification);

		notificationQuery.equalTo("Owner", currentUser);
		notificationQuery.find();
	}).then(function(notificationObjs) {
		var promise = deleteObjectSelection(notificationObjs);
		return Parse.Promise.when(promise);
		
	//Delete the Users Photo/s	
	}).then(function(){
		var UserProfilePhoto = Parse.Object.extend("GlobalProfilePhoto");
		var photoQuery = new Parse.Query(UserProfilePhoto);

		photoQuery.equalTo("user", currentUser);
		return photoQuery.find();
	}).then(function(photoObjects){
		var promise = deleteObjectSelection(photoObjects);
		return Parse.Promise.when(promise);
	
	//Now delete the user	
	}).then(function(){
		var promise = Parse.Promise.as();
		promise = promise.then(function() {
			return currentUser.destroy();
		});
		
		return promise;
		
	//Success/Error	
	}).then(function(){
		response.success("Delete Account Ran Succesfully for user:" + currentUserID);
	},function(error){
		response.error("Delete Account Ran Unsuccessfull for user:" + currentUserID);
	});
}
	
//The Global method for deleting sets of objects and returning a pass/fail promise	
function deleteObjectSelection(objectsToDelete) {
	var promise = Parse.Promise.as();
	_.each(objectsToDelete, function(obj) {
		promise = promise.then(function() {
	    	return obj.destroy();
	  	});
	});
	return promise;
}