const functions = require('firebase-functions');
const firebase = require('firebase-admin');


var config = {
    apiKey: "AIzaSyDMPdtiD4PIniODZnbXlsihddrTlInWHTM",
    authDomain: "quiz-new-version.firebaseapp.com",
    databaseURL: "https://quiz-new-version.firebaseio.com/",
    storageBucket: "gs://quiz-new-version.appspot.com"
  };
  firebase.initializeApp(config);


exports.create=functions.https.onRequest((req,res)=>{
 
  var data=JSON.parse(req.body);
  var user_id=data.user_id;

var language_id=data.language_id;
  firebase.database().ref("game_room/").orderByChild("availability").equalTo(1).limitToFirst(1).once('value', ((snapshot)=> {
     console.log("user_id  ",user_id);

    
    if (snapshot.val() !== null) {
            console.log("available");
            var childKey = Object.keys(snapshot.val());
            console.log("gameroom   ", childKey)
            
        
            const userQuery = firebase.database().ref(`game_room/${childKey}`).once('value');
            return userQuery.then(userResult => {
                const language_id1 = userResult.val().language_id;
             
                if (language_id === language_id1) {
				 firebase.database().ref(`game_room/${childKey}/${user_id}`).update({
                        "status": true,
                        "right": 0,
                        "wrong": 0,
                        "que_no": 0,
                        "sel_ans": "",
                    });
                  firebase.database().ref(`game_room/${childKey}`).update({
                        "availability": 2,
					
                    });
                   
                }else{
					createGameRoom();
				}
            });
            console.log(snapshot.val());
        }
    else
    {
	
		createGameRoom();
	
	
    }
    
    }));
	  

  function createGameRoom() {
    console.log("not available");
				var ref=firebase.database().ref('game_room/');
			ref.push({
					"availability":1,  
					"language_id":language_id,
									
  
					}).then(function(ref){
						console.log(ref.key);
						var key=ref.key;
							console.log(key);
						 var ref= firebase.database().ref(`game_room/${key}/${user_id}`).update({//.push({
									//ref.set({
								"status":true,
							"right":0,
							"wrong":0,
							"que_no":0,
							"sel_ans":"",
			
								});
		
		
						});
}
 res.send({"code":user_id});
});