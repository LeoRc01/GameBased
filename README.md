# <img src="screenshots/GameBased_Logo.png" alt="GameBased Logo" width="50"/> - GameBased
> An android application to get and search for videogames informations, developed in Kotlin and based on the MVVM architecture model.

![GameBased Showcase 1](screenshots/GameBased_Showcase_1.png?raw=true "Showcase of the application")

![GameBased Showcase 2](screenshots/GameBased_Showcase_2.png?raw=true "Showcase of the application")

GameBased is an application that allows users to view information details about videogames such as the genre, the platforms it is available on, a summary, and the release date.

## Table of Contents

- [Development team](#development-team)
- [Features offered](#features-offered)
- [Architecture](#architecture)
- [Classes](#classes)
- [Libraries](#libraries)
- [Design](#design)
	- [Registration, Login & Password recovery](#registration-login--password-recovery)
	- [Home page](#home-page)
	- [Game Details View](#game-details-view)
	- [Search page](#search-page)
	- [Settings page](#settings-page)
	- [Email/Username/Password change](#emailusernamepassword-change)
	- [Chronology](#chronology)
	- [Favourites page](#favourites-page)
- [Adapters](#adapters)
	- [FavoriteGridViewAdapter](#favoritegridviewadapter)
	- [GamesBigRecyclerViewAdapter](#gamesbigrecyclerviewadapter)
	- [GameScreenshotsRecyclerViewAdapter](#gamescreenshotsrecyclerviewadapter)
	- [GamesRecyclerViewAdapter](#gamesrecyclerviewadapter)
	- [SuggestionRecyclerViewAdapter](#suggestionrecyclerviewadapter)
- [Navigation](#navigation)


## Development team

**Team name**: CIP Studio

**Members**:
- 869039 Leonardo Valente (referent)  
- 869901 Davide Radaelli   
- 866147 Simone Biotto  
- 872491 Federico Pulcino

## Features offered
The GameBased application based on the IGDB API allows the user to do the following operations:

- View the details of a game
- Search for games and filter results
- View and consult certain rankings such as
	- Most rated
	- Top rated
	- Most hyped
	- Loved by critics
	- Most popular
	- Recently released
	- Coming soon
	- Worst rated
- Sharing of videogames via links
- Receive recommendations based on your searches and views via the category _"for you"_
- Register: registration unlocks the ability to access the _"for you"_, add games to your favorites and cloud sync for more recent searches and most recently visited games
- Possibility to choose between "Italian" and "English" as main language of the application
- Ability to choose between light theme and dark theme
- Ability to delete account from firebase servers


## Architecture
The architecture is based on the **MVVM model**, so there's a UI layer that is based on the data obtained from the ViewModels thanks to the repositories that query a remote service, be it IGDB, firebase and/or a local database, all optimized through a caching system.

<div align="center">
	<img src="doc_images/Architecture.png?raw=true" alt="Application MVVM Architecture" width="500"/> 
</div>

## Classes
- **User**: internal class that takes advantage of the _FirebaseRepository_, _HistoryRepository_ and classes _RecentSearchesRepository_ to manage consistency between the local database and the Remote database and User authentication. It also manages case studies of the user without a profile, thus avoiding managing the remote database but only the local one
- **IGDBRepository**: a class of type Repository that allows the application to interact with the IGDB API, exposing the APIs that can be used by each class of the project and independently managing the cache
- **FirebaseRepository**: a class of type Repository that manages the remote database of the known features, i.e. it manages your favorite games and the games you have visited or searched for recently. It also calls _FirebaseAuth_ to login
- **HistoryRepositoryLocal**: a repository-type class that manages the Room-local database regarding visit history.
- **RecentSearchesRepositoryLocal**: a class of type repository that deals with manage the local Room-database which pertains to the search history.
- **AISelector**: a class of type Repository that takes care of managing the list of genres that the user usually looks at and updates it based on new occurrences. Allows the implementation of the "For You" section.
- **Filter**: an internal class that allows the implementation of filters in an abstract way in the _GameList_ fragment and in the _Search_ fragment
- **AIModel**: a class of type model that contains the information managed by the class AISelector for the implementation of the list of genres visited. The class contains, in addition to the string of gender, the associated weight, i.e. "how important" for the user that type of gender.
- **GameDetails**: a class of type model that contains the incoming information from the IGDB API.
- **PlatformDetails**: a class of type model that contains the detailed information regarding a Platform, including also a reference to a PlatformHardwareDetails.
- **PlatformHardwareDetails**: a class of type Data-Class, it is only used to contain the hardware information of a platform, such as the PS4 for example
- **ItalianEnglishTranslator**: a utility-type class that allows you to translate a text from English to Italian using the translate method. It is implemented through usage of the MLKit library.
- **StateInstanceSaver**: this class is an internal class that serves to save the state of the various fragments, such as the search one, so that when you return to that fragment there is the search carried out with the previously selected filters. It uses a simple Hashmap, where the data necessary to recover the state of the fragment are saved.


## Libraries
- **Firebase**: service that allows the use of various libraries, those used are:
	- **Firebase Authentication**: library that manages the authentication service with all related services
	- **Realtime Database**: library with which it is possible to use the service Firebase Realtime Database. More specifically, the application saves inside the remote database all the ids of the favorite games, the ids of the last visited games and the ids of the last searches made.
	- **Firebase Auth**: library used to manage Auth, i.e. login, the registering a new user, lost password and changes of user data
- [**igdb-api-jvm**](https://github.com/husnjak/IGDB-API-JVM): library that allows the use of the _IGDBWrapper_ class that allows communication with the IGDB database through the use of queries based on _APICalypse_, with related JSON responses.
- **kotlinx-coroutines**: library used to take advantage of kotlin coroutines for deploy the app asynchronously. Used for example for requests from _IGDBRepository_ class.
- [**Picasso**](https://github.com/square/picasso): Library for managing images and multimedia contents. Used for uploading screenshots, game covers and profile pictures of the user. Cache management is entrusted directly to the library
- **Room-database**: library that allows the creation and interaction of databases internal to local storage.
- [**TouchImageView**](https://github.com/MikeOrtiz/TouchImageView): library that adds more functionality to ImageViews, such as the ability to zoom
- [**Kotlinized LRU Cache**](https://github.com/MayakaApps/KotlinizedLruCache): library that allows you to use the LRU Cache built-in in android, optimized for kotlin coroutines, used to maintain the cache for _IGDBRepository_ calls.
- [**Shimmer**](https://github.com/facebook/shimmer-android): Facebook library that adds the shimmer effect, used in loading for example games or while doing a search.
- [**MLKit:translate**](https://developers.google.com/ml-kit/language/translation): Google library that allows you to translate text, used to translate game description text.


## Design
### Registration, Login & Password recovery
When you first start the app, this section will open and a screen will be shown where it is possible to choose, using three buttons, between registering an account and logging in with an account previously created or using the app without an account and so log in as a guest.  
The Login screen is made up of 2 text inputs, where the email and password are entered respectively, and if the user forgets the password, he can recover it by clicking on _"forgot password?"_ on the login screen and, after entering the email with which you registered, firebase will send the user an email with a link to change the password.  


<div align="center">
	<img src="screenshots/GameBased_FirstPage.png?raw=true" alt="First Page" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/GameBased_Login.png?raw=true" alt="Login" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/GameBased_Register.png?raw=true" alt="Register" width="250"/>
</div>


It is possible to switch from the login screen to the registration screen and vice versa, by pressing on the words below "Register now" and "Login now" which will take him to the registration screen, which is made up of 4 textinputs where the user must enter the information required, i.e. email, a username, password and password confirmation. Also in this screen it is possible to go directly back to the login directly, in the same way as before.  

### Home page
This is the first screen that opens when logged in, whether as a guest or as a registered user, its functionality is to show the user a series of games divided into sections, i.e. the most voted, the best, the most awaited, the most loved by critics, the most popular, worst, newly released, and soon to be released, too if you are logged in with your account you will also have a section dedicated to games suggested based on your visits and your favourites.  
When you press on a game cover, that game's detail screen will open.  
The composition of the fragment is based on a ScrollView containing many within it RecyclerView, each for each category.  

<div align="center">
	<img src="screenshots/darkMode/GameBased_Home.png?raw=true" alt="Home" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_GameList.png?raw=true" alt="GameList" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_GameList_filters.png?raw=true" alt="GameList filters" width="250"/>
</div>

From the home screen you can view some games of that particular category scrolling horizontally on the RecyclerView, also to be able to view more items of that category, in addition to having better navigability between them, you can press on section title. Within it there will be the possibility to apply filters on the games shown according to the selected criteria.  

### Game Details View
This section offers the possibility to see all the details of a video game, such as thumbnails, description, release date, platforms the game is available on, rating, and more. Furthermore there is also the possibility to see up to 10 games whose characteristics are similar to the game that is being observed.  

<div align="center">
	<img src="screenshots/darkMode/GameBased_GameDetails_1.png?raw=true" alt="GameDetails 1" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_GameDetails_2.png?raw=true" alt="GameDetails 2" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_GameDetails_3.png?raw=true" alt="GameDetails 3" width="250"/>
</div>

Using the buttons at the top right of the page, you can share the game or add it to the user's favorites list.
There are also dialogs, accessible via the appropriate button (the underlined text to indicate that an activity will take place), which show further details about each platform and collection of a videogame.  

<div align="center">
	<img src="screenshots/darkMode/GameBased_GameDetails_collection.png?raw=true" alt="GameDetails collection" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_GameDetails_platform.png?raw=true" alt="GameDetails platform" width="250"/> 
</div>

### Search page
By clicking on the magnifying glass in the bottom navigation bar, you access the research section.  
When first opened, the screen consists of only the searchbar and a filter button.  
By clicking on the first, it is possible to search for the game you want by writing a string or a word that will be used by the search managed by the IGDB service itself.  
While writing your query in the searchbar, they may also appear in one RecyclerView various suggestions based on the history of old searches made and/or up to 3 games found by the API among the most reviewed and that contain what you are looking for within their name.  

<div align="center">
	<img src="screenshots/darkMode/GameBased_Search.png?raw=true" alt="Search" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_Search_Suggest.png?raw=true" alt="Search Suggestions" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_Search_filters1.png?raw=true" alt="Search filters" width="250"/>
</div>

If a search cronology is available, it will appear the first time you load the search screen. From there, you can manually delete single entries through the cross button they have on the right.  

Once you have carried out your search normally or after clicking on one of the suggestions, will appear, still inside a RecyclerView (after a short animation of loading), the list of results related to it.  
In case there are no results or suggestions, the application will warn the user through a screen.  

Results are obviously not loaded all together, but the less relevant ones do come instead loaded as scrolling reaches the current bottom of the RecyclerView (i.e. the end of those already loaded), taking advantage of the pagination system present in the API itself.  

By clicking on the Filters button instead, a side drawer will open from which it is possible to filter the results obtained from your search through various parameters, such as category, genre, platform, release date and others.  


### Settings page
From the bottom navigation bar, clicking on the user icon takes you to the screen profile management, in this situation two cases may occur.  

If the user has logged in from here it will be possible to modify all the data relating to the profile, including the image, the user will then be able to choose from certain options such as consult and delete the list of games displayed and delete the history of searches, set the dark mode/light mode, change the language (Italian, English), log out and delete your account from the servers.  

If the user is logged in as a guest he will only be able to consult and delete the displayed games, clear his search history and set dark/light mode, as well as of course the ability to log in.  

<div align="center">
	<img src="screenshots/darkMode/GameBased_User1.png?raw=true" alt="User 1" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_User2.png?raw=true" alt="User 2" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_User_Guest.png?raw=true" alt="User Guest" width="250"/>
</div>

### Email/Username/Password change
Fragments allow the user to modify all data relating to their account.  
The data entered is compared with the data on firebase before making the change information.  

<div align="center">
	<img src="screenshots/darkMode/GameBased_ChangeEmail.png?raw=true" alt="Change Email" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_ChangeUsername.png?raw=true" alt="Change Username" width="250"/> 
	&nbsp;&nbsp;&nbsp; 
	<img src="screenshots/darkMode/GameBased_ChangePassword.png?raw=true" alt="Change Password" width="250"/>
</div>

### Chronology

<img src="screenshots/darkMode/GameBased_History.png?raw=true" align="right" alt="History" width="250"/>

The fragment History contains the list of visited games recently, from here it is possible to consult the details of each game by clicking on the desired item, for each of them some details such as the name, genre, rating and release date are already readily shown.
  
It is also possible to delete each item of this list by selecting the trashcan icon at the top right.
  
The list is saved in a hybrid way: the last 10 games visited are saved in the cloud via firebase (feature available only if the user has registered) in such a way that persist, even if only partially, following a change of device.

<br><br><br><br><br><br><br><br><br><br>

### Favourites page

<img src="screenshots/darkMode/GameBased_Favourite.png?raw=true" align="right" alt="Favourites" width="250"/>

The favourites page is accessible via the button with the heart icon in the bottom navigation bar. 
  
References to the ID of the games in this list are saved on the database, this means that if the user needs to change phones, games still remain saved on the account and therefore accessible from any device.

<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
	
### Adapters

#### FavoriteGridViewAdapter

<img src="doc_images/FavouriteGridViewAdapter.png?raw=true" align="right" alt="FavouriteGridViewAdapter" width="120"/>

Used to list favorite games in the _favoriteFragment_ fragment, is structured in such a way as to show the cover of the video game with underlying its title and with the possibility of going to see the details of a game by pressing on the cover.

<br><br><br>

#### GamesBigRecyclerViewAdapter

<img src="doc_images/GamesBigRecyclerViewAdapter.png?raw=true" align="right" alt="GamesBigRecyclerViewAdapter" width="300"/>

Used to list the games in the fragments _SearchFragment_ and _HistoryFragment_, where the cover of the video game is shown alongside the title and superimposed on the cover, in the lower left corner the rating of the video game, all on a horizontal rectangle with the background cover with blur filter. In this case there is the possibility to go directly to see the details of a game by pressing the cover, too.

<br>

#### GameScreenshotsRecyclerViewAdapter

<img src="doc_images/GameScreenshotsRecyclerViewAdapter.png?raw=true" align="right" alt="GameScreenshotsRecyclerViewAdapter" width="200"/>

Used to display game images in the _GameDetailsFragment_, it just shows the screenshot in a recyclerview and if you press it, it shows the screenshot in full screen.

<br><br><br><br>

#### GamesRecyclerViewAdapter

<img src="doc_images/GamesRecyclerViewAdapter.png?raw=true" align="right" alt="GamesRecyclerViewAdapter" width="120"/>

Used to display the game in a recyclerview in fragments _MainPageFragment_ and _GameDetailsFragment_, like for _FavoriteGridViewAdapters_, it simply displays the cover and its title and the possibility of going to see the details of a game by pressing on its cover.

<br><br><br><br>

#### SuggestionRecyclerViewAdapter

<img src="doc_images/SuggestionRecyclerViewAdapter.png?raw=true" align="right" alt="GamesRecyclerViewAdapter" width="300"/>

Used to show fragment hints in the fragment _SearchFragment_, it's simply composed of the query that have been made with the ability to search for that query if you click on it.

<br><br><br><br><br><br><br>

## Navigation

![Navigation](doc_images/Navigation.png?raw=true "Navigation")