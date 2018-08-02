WatchPresenter is an application that let's you control Google Slides from either your SmartWatch or your SmartPhone.

> Note: This is not an official Google product and is not supported by Google in any way.

## How it works

The application uses two components to perform its task:
- A Chrome extension
- A mobile app

Users can signal a slide change by several means:

- Tapping on the phone screen
- Clicking on the phone's volume buttons
- Tapping on their Android Wear SmartWatch

The latter two methods do not require the phone screen to be on, hence saving battery and preventing unintentional slide changes.

Whenever the user signals a slide change, the mobile app sends a message to the Chrome extension by using Google Cloud Messaging system. An identifier is used, so that messages from the app are sent to the appropriate destination (the Chrome browser of the user). The identifier is based on the Google Account name, so the user must use the same Google Account on both the browser and the phone.

## Modules and file structure

On the project root you will find the following folders:

- 'android': Contains the android App source code. It is an Android Studio project (Gradle build).
- 'chrome': Contains the source code for the Chrome extension. See README file in that folder for more info.
- 'resources': Contains other resources such as icons, etc. Both in source and binary forms.

## Licensing and Contributing

For licensing info, please refer to LICENSE file in this folder.

If you want to contribute to the project, please read the CONTRIBUTING file.
