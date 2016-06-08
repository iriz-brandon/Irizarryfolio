self.sendEmail = function(){
      var editorTxt = basicEditor.getHTML();
      var userEmail =  $('#single-user-email').val()
    	console.log("Email Text: " + editorTxt);
        console.log("User Email: " + userEmail);
    
    	$.ajax({
    		 type: "POST",
    		 url: "https://mandrillapp.com/api/1.0/messages/send.json",
    		 data: {
			    key: 'key',
			    message: {
			       from_email: "support@tool.me",
			       from_name: "web tool",
		           subject:"Testing Web App",
			       to: [{
			             email: userEmail,
			             name: "Brandon",
			       }],
			       autotext: true,
			       html: editorTxt,
			    }
    		 }
    	}).done(function(response) {
    		console.log(response);
        }).fail(function(response){
    	    console.log("Could not send email!");
    	    console.log(response);
        }).success(function(response){
        	alert("Sent email to " + userEmail + " successfully!");
        	console.log(response);
        });  
    }

    self.sendEmailToAll = function(){
        var editorTxt = basicEditor.getHTML();
        console.log("Email Text: " + editorTxt);

        var totalUserCount = 0;
        var emailSent = 0;
        var emailNotSent = 0;

        var userQuery = Parse.Object.extend("User");
        var query = new Parse.Query(userQuery);
        query.limit(1000);
       
        query.find().then(function(users) {

           console.log("queried " + users.length + " in sendEmailToAll");
           totalUserCount += users.length;

           for(var i = 0; i < totalUserCount; i++){
                var userEmail = users[i].get("email");
                var userName = users[i].get("username");
                
                console.log(userEmail + " " + userName);

                $.ajax({
                     type: "POST",
                     url: "https://mandrillapp.com/api/1.0/messages/send.json",
                     data: {
                        key: 'key',
                        message: {
                           from_email: "support@tool.me",
                           from_name: "tool",
                           subject:"Testing Web App",
                           to: [{
                                 email: userEmail,
                                 name: userName,
                           }],
                           autotext: true,
                           html: editorTxt,
                        }
                     }
                }).done(function(response) {
                    console.log("Attempting to send email: " + i);
                }).fail(function(response){
                    emailNotSent++;
                }).success(function(response){
                    emailSent++;
                });  
           }

           alert("Sent Email To All.");

           query.limit(1000);
           query.skip(totalUserCount);
           return query.find();
        }).then(function(users) {
            //A dummy then to advance the chain
        }, function(error){
            console.log("Could not query any more users.");
        }).then(function() {
            console.log("Sent " + emailSent + " emails.");
            console.log("Could not send " + emailNotSent + " emails");
        });
    }
}