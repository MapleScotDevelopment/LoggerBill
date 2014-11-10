LoggerBill
==========

Do you ever fancy youself as a woodcutter? A Lumberjack? A Timberman?  

You too can live out your fantasy as a big burly muscular manly man that all the women adore... Without leaving your living room. Behind your desk at work, on the train, or just relaxing at home, chopping wood has never been so easy and fun... In fact, you don't even have to lift an axe.

LoggerBill is the second release from MapleScot development and our first to use the LibGDX game development library. We wrote this game as a quick and simple test to evaluate LibGDX as a possible framework for our future games. You may notice that it's similar to a certain other wood-cutting game on the market.. Our intention was to demonstrate how a simple, but popular game can be written in LibGDX for Android. In particular it was important to us that the user experience be smooth. The Back-button often doesn't work on games ported over from Apple devices and this vexes us greatly, so we've take care to make sure that everything works as one would expect for a quality game.

We are still learning though, this is only our second game so we need your help to bring our many (more original) ideas to the world. So, we've released this game as Open Source. We would like to invite everyone to view our code. If you use our ideas, designs, or have learned anything from our code we would love to hear from you and we would love to have your feedback and suggestions on how to improve the game or how to improve our code.

As a demo of the capabilities of LibGDX this game demonstrates:
- Use of Google Play Games
  - Achievements
  - Leaderboards
  - Cloud Save
  - Admob Ads
- Smooth transition from screens, menus, popup dialogs. Back button works logically.
- OpenGL Shaders for night-time and ghost effects
- Loading of Assets while a loading animation plays
- Use of JSON Skins and deserialization
- Particle effects for wood chips
- Sound and Music
- Scene2d layouts

*Adding GameCircle support*
For obvious reasons I have not added the GameCircleSDK to the Logger Bill source tree. If you want to try it out you will need to do these steps after downloading the tree.

Go here https://developer.amazon.com/appsandservices/apis/engage/gamecircle/docs/initialize-android and download the SDK from the link on that page
Copy the Android/GameCircle/GameCircleSDK contents to the GameCircleSDK directory in the Logger Bill tree
Copy the Android/Ads/lib/amazon-ads-5.4.146.jar to GameCircleSDK/libs in the Logger Bill tree
Add fire and GameCircleSDK to the settings.gradle file
Uncomment the fire and GameCircleSDK modules from the root build.gradle
Modify the GameCircleSDK/AndroidManifest.xml file and change the package to "com.amazon.ags.lib"
The reason for the last step is that the gradle android plugin will attempt to merge the manifests and there is a conflct with the one in the GameCircleSDK and the one in the game circle jar.

It isn't used since ours overrides it but we still need to remove the conflict for the build to complete.

Replacing the Google Play Games Services, Cloud Save and AdMob Ads with Amazon GameCircle, Whispersync and Amazon Ads was fairly straightforward as they are all pretty similar.
