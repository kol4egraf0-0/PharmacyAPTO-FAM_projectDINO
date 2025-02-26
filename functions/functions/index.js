const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendOrderNotification = functions.database
    .ref("/Users/{userId}/orders/{orderId}")
    .onCreate(async (snapshot, context) => {
        const userId = context.params.userId;
        const orderData = snapshot.val();


        const userRef = admin.database().ref(`/Users/${userId}/fcmToken`);
        const tokenSnapshot = await userRef.once("value");
        const fcmToken = tokenSnapshot.val();

        if (!fcmToken) {
            return null;
        }

        const payload = {
            notification: {
                title: "Новый заказ!",
                body: `Ваш заказ #${orderData.orderId} успешно создан.`,
                click_action: "OPEN_ACTIVITY_ORDERS"
            },
            token: fcmToken
        };

        try {
            await admin.messaging().send(payload);
            console.log("Уведомление отправлено пользователю:", userId);
        } catch (error) {
            console.error("Ошибка при отправке уведомления:", error);
        }

        return null;
    });
