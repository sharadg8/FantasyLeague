let functions = require('firebase-functions');
let admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Notify/Messages/{pushId}')
    .onWrite(event => {
        const message = event.data.val();
        const senderUid = message.sid;
        const promises = [];

        const getInstanceIdPromise = admin.database().ref(`/Users/${senderUid}/instanceId`).once('value');
        const getSenderUidPromise = admin.auth().getUser(senderUid);

        return Promise.all([getInstanceIdPromise, getSenderUidPromise]).then(results => {
            const instanceId = results[0].val();
            const sender = results[1];
            console.log('notifying ' + senderUid + ' about ' + message.msg + ' from ' + sender.displayName);

            const payload = {
                notification: {
                    title: sender.displayName,
                    body: message.msg
                }
            };

            admin.messaging().sendToDevice(instanceId, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
		
		event.data.ref.remove()
    });
