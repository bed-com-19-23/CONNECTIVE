const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotificationPush = functions.firestore
  .document("users/{uid}/notifications/{notificationId}")
  .onCreate(async (snapshot, context) => {
    const uid = context.params.uid;
    const notifData = snapshot.data();

    if (!notifData) return null;

    const userDoc = await admin.firestore().collection("users").doc(uid).get();
    if (!userDoc.exists) return null;

    const fcmToken = userDoc.data().fcmToken;
    if (!fcmToken) return null;

    const message = {
      token: fcmToken,
      notification: {
        title: notifData.title || "Connective Notification",
        body: notifData.body || ""
      },
      data: {
        relatedId: notifData.relatedId || "",
        type: notifData.type || "general"
      }
    };

    try {
      await admin.messaging().send(message);
      console.log(`Push notification successfully delivered to UID: ${uid}`);
    } catch (error) {
      console.error("Error delivering FCM push notification:", error);
    }
    return null;
  });
