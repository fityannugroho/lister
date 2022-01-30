<div  align="center">
  <img src="assets/lister-logo.jpeg">
</div>

# **Lister**

Mobile app for management and sharing tasks.

This app is developed with programming language **Java** and using [**Firebase**](https://firebase.google.com/docs/android/setup#java) for data storage and authentication method.

## Features
- Create new account.
- Login with existing account.
- Change password if you forgot your password.
- Add, change, and delete your tasks.
- Share your task to another users.
- Modify the sharing access.

## Screenshoots

![Login](screenshoots/1.0-login.jpg)
![Register](screenshoots/2.0-register.jpg)
![Forgot Password](screenshoots/3.0-forgot-password.jpg)
![Main](screenshoots/4.0-main.jpg)
![Add Task](screenshoots/5.0-add-task.jpg)
![Detail Task](screenshoots/6.0-detail-task.jpg)
![Share Task](screenshoots/7.0-share-task.jpg)
![Edit Task](screenshoots/8.0-edit-task.jpg)
![Delete Task](screenshoots/9.0-delete-task.jpg)
![Shared List](screenshoots/10.0-shared-list.jpg)
![Manage Access](screenshoots/11.0-manage-access.jpg)

## Entity Relationship Diagram

![Entity Relationship Diagram](assets/erd-v1.0.0.png)

## Contribution

Everybody can contribute to this project.

You may need to add this project to your own [Firebase project](https://firebase.google.com/docs/android/setup#create-firebase-project). You also need to [configure the Firestore](https://firebase.google.com/docs/firestore/quickstart#java) and creating new collections for `User`, `Task`, and `SharedTask` as shown in [ERD](README.md#entity-relationship-diagram).

After that, you need to [configure the Fireauth](https://firebase.google.com/docs/auth/android/password-auth#before_you_begin) in order to enable email-password based authentication.

## Disclaimer

This project was originally created for college assignment by me (**@fityannugroho**) and **@arisandyk**. This project is open-source and can be used for educational purposes.
