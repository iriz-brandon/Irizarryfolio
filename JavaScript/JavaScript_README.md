#JavaScript ReadME


#ParseDB actions

This is pretty straightforward. All of these files correspond to an action done in an application that would effect the cloud database(Parse in this case).

Unforunately Parse is being shut down, but this was done at the height of its popularity for an iOS application. It's still upsetting Parse is being shut down.

None of the information represented here corresponds to any active database information, and none of the names used are actually query-able. All keys have been
removed as well.

**my_deleteAccount.js

This file would delete an account and all related parts of the database related to that account when a user chooses to delete their account in app.

**my_deletePost.js

This file would delete a newsFeed post and all related parts of the database, such as comments and notifications, when a user chooses to delete that
news feed post in app.

**my_unfriend.js

This file would remove a friend from the a users friend collection and update all related parts of the database. 


#quillEmailPage

   This was an attempt at adding functionality to an already existing web application tool
   to make emailing bulk users easier. 
   
   The purpose of this was to create a page within the webapp that could be used to email 
   all users(or a single user), thus making it easy for the non-developer members of the team
   to communicate events and info to users of the app that the webapp is associated with.
   
   I know this appears basic, but this was coded as a preliminary field test as something
   that could be potentially used. Ultimately this idea was not used. 
   
   ** In the associated knockout.js code, this excerpt was pulled from a much larger knockout.js file
      that calls these functions all within a master function thus the "self." initialization.
      Also assume the necessary ko.observables are hooked up. 
 