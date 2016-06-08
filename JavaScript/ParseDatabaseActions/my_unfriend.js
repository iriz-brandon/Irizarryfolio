Parse.Cloud.define("unfriend", function(request, response) {
 
    //var unFriend = require('cloud/unfriend.js');
 
    console.log("unfriend");
 
    Parse.Cloud.useMasterKey();
    // Fetch all the users, then move on
    var friendsToUnfriendIDArray = request.params.friends.slice(0); // Clone this array
    var PFUsersToUnfriendArray = [];
    var counter = 0;
 
    for (var i=0; i<friendsToUnfriendIDArray.length; i++){
 
        var query = new Parse.Query(Parse.User);
 
        query.get(friendsToUnfriendIDArray[i], {
            success: function(PFUser) {
                // The object was retrieved successfully.
                console.log("Successfully retrieve this user:");
                console.log(PFUser);
                PFUsersToUnfriendArray.push(PFUser);
 
                if(PFUsersToUnfriendArray.length === friendsToUnfriendIDArray.length){
                    /*unFriend.*/unFriendPart1(request, response, PFUsersToUnfriendArray);
                }
            },
            error: function(PFUser, error) {
                // The object was not retrieved successfully.
                // error is a Parse.Error with an error code and description.
                console.log("unfriend - Something went wrong querying the user");
                response.error("unfriend - Something went wrong querying the user");
            }
        });
    }
});

//these functions are for unfriending a person
/*exports.unFriendPart1 =  */function unFriendPart1 (request, response, PFUsersToUnfriendArray){
    console.log("\n\n");
    console.log("unFriendPart1");
 
    //  ⁃   Query all the Friendship objects that have the current user in the fromUser if any
    var stuffToUpdateArray = [];    // This will hold all the stuff to update at the last step
    var currentUser = Parse.User.current();
    var Friendship = Parse.Object.extend("GlobalUserFriendship");
    var friendshipQuery = new Parse.Query(Friendship);
     
    friendshipQuery.include('toUser');
    friendshipQuery.equalTo('fromUser', currentUser);
 
    friendshipQuery.find().then(function(friendships) {
 
            for(var i = 0; i < friendships.length; i++) {
 
                // Get toUser field from the friendship
                var friendship = friendships[i];
                var friendshipToUser = friendship.get('toUser');
                var friendshipToUserIDString = friendshipToUser.id;
 
                // Go through the PFUsersToUnfriendArray
                for(var j=0; j < PFUsersToUnfriendArray.length; j++){
 
                    var friendToUnfriend   = PFUsersToUnfriendArray[j];
                    var friendToUnfriendID = PFUsersToUnfriendArray[j].id;
 
                    if (friendToUnfriendID.localeCompare(friendshipToUserIDString) === 0) {
 
                        // Change the friendship status to "Unfriended"
                        friendship.set("state", "Unfriended");
                        stuffToUpdateArray.push(friendship);
                        // remove friendship from friendships?
 
 
                        var friendToUnfriendFriendsList = friendToUnfriend.get("friends").slice(0); // Clone this
                        // Find "fromUser" on toUser's friendslist, then remove
 
                        var index = friendToUnfriendFriendsList.indexOf(currentUser.id);
 
                        // Remove "fromUser" from toUser's friendsList
                        friendToUnfriendFriendsList.splice(index, 1);
                        friendToUnfriend.set("friends", friendToUnfriendFriendsList);
 
                        // Save on stuffToUpdateArray
                        stuffToUpdateArray.push(friendToUnfriend);
 
                        // Remove from PFUsersToUnfriendArray?
                        PFUsersToUnfriendArray.splice(j, 1);
 
                    }
 
                }
 
            }
            return Parse.Promise.as();
    }, function(error) {
        response.error("unFriendPart1 - Error retrieving Friendships Part 2");
    
    //unFriendPart2
    }).then(function() {
        console.log("\n\n");
        console.log("unFriendPart2");
     
        //  ⁃   Query all the Friendship objects that have the current user in the fromUser if any
        var currentUser = Parse.User.current();
        var Friendship = Parse.Object.extend("GlobalUserFriendship");
        var friendshipQuery = new Parse.Query(Friendship);
         
        friendshipQuery.include('fromUser');    //toUser
        friendshipQuery.equalTo('toUser', currentUser); //fromUser
     
        return friendshipQuery.find();
    }function(error) {
        response.error("unFriendPart2 - Error retrieving Friendships Part 2");
    }).then(function(friendships){
            for(var i = 0; i < friendships.length; i++) {

            // Get toUser field from the friendship
            var friendship = friendships[i];
            var friendshipFromUser = friendship.get('fromUser');    //toUser
            var friendshipFromUserIDString = friendshipFromUser.id;

            // Go through the PFUsersToUnfriendArray
            for(var j=0; j < PFUsersToUnfriendArray.length; j++){

                var friendToUnfriend   = PFUsersToUnfriendArray[j];
                var friendToUnfriendID = PFUsersToUnfriendArray[j].id;

                if (friendToUnfriendID.localeCompare(friendshipFromUserIDString) === 0) {

                    // Change the friendship status to "Unfriended"
                    friendship.set("state", "Unfriended");
                    stuffToUpdateArray.push(friendship);
                    // remove friendship from friendships?


                    var friendToUnfriendFriendsList = friendToUnfriend.get("friends").slice(0); // Clone this
                    // Find "toUser" on fromUser's friendslist, then remove

                    var index = friendToUnfriendFriendsList.indexOf(currentUser.id);

                    // Remove "toUser" from fromUser's friendsList
                    friendToUnfriendFriendsList.splice(index, 1);
                    friendToUnfriend.set("friends", friendToUnfriendFriendsList);

                    // Save on stuffToUpdateArray
                    stuffToUpdateArray.push(friendToUnfriend);

                    // Remove from PFUsersToUnfriendArray?
                    PFUsersToUnfriendArray.splice(j, 1);
                }
            }
        }
        return Parse.Promise.as();

    //unFriendPart3
    }).then(function(){
        console.log("\n\n");
        console.log("unFriendPart3");
     
        var currentUser = Parse.User.current();
        var currentUserFriends = currentUser.get("friends").slice(0);
        var friendsToUnfriendIDArray = request.params.friends.slice(0);
     
        for(var i=0; i<friendsToUnfriendIDArray.length; i++){
             
            var friendToUnfriend = friendsToUnfriendIDArray[i];
            var index = currentUserFriends.indexOf(friendToUnfriend);
     
            currentUserFriends.splice(index, 1);
     
        }
     
        currentUser.set("friends", currentUserFriends);
        stuffToUpdateArray.push(currentUser);

        return Parse.Object.saveAll(stuffToUpdateArray);
    }, function(error){
       // an error occurred...
        response.error("unFriendPart3 - Error: \n" + error);
    }).then(function(){
         response.success("You are no longer following this user");
    })
}