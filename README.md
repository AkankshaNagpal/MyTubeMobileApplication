# MyTubeAndroidApp
This app provides an interface for users to search for the videos and also view their favorite playlist. It uses Youtube Data API to search the results. 

# How to build the app
This project uses gradle to download all the dependencies required by project.
To run the project,unzip the file and import it to IDE. Main Activity is the launcher
activity containing google signin.

MyTube uses OAuth2 based authorization for using the you tube data API's

Access token is being generated and passed with every subsequent requests.

Async Task are used to display the search results and show the favorites list.

# How to use the App
User will go to application,Login Screen will appear.
It will ask the user to sign in using his/her google account.

It will ask the user to allow youtube to access his/her data once the authentication is
successfully done.

Next Screen is Home Screen of Application which will contain two tabs:

#Search Tab - To display the search results on the basis of query entered by user.

#My Favorites Tab - It will display the items added to playlist "SJSU-CMPE-277"

On clicking Logout, user will be logged out of application and will be redirected to login screen!
