const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendOrderNotification = functions.database
    .ref("/Users/{userId}/orders/{orderId}")
    .onCreate(async (snapshot, context) => {
        const userId = context.params.userId;
        const orderData = snapshot.val();

        // Получаем FCM токен пользователя
        const userRef = admin.database().ref(`/Users/${userId}/fcmToken`);
        const tokenSnapshot = await userRef.once("value");
        const fcmToken = tokenSnapshot.val();

        if (!fcmToken) {
            console.log("FCM токен не найден для пользователя:", userId);
            return null;
        }

        // Создаем сообщение
        const payload = {
            notification: {
                title: "Новый заказ!",
                body: `Ваш заказ #${orderData.orderId} успешно создан.`,
                click_action: "OPEN_ACTIVITY_ORDERS" // Должно совпадать с intent-фильтром в Android
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
