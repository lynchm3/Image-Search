# Flickr Search
Android App to search for and display images from Flickr

## Getting Started
The app and tests can be built and run using Android Studio
I have included the debug apk of the app [here](app-debug.apk) for convenience
I've also included the API key in version control for convenience

## The App
There's a single activity - [MainActivty](app/src/main/java/com/marklynch/currencyfair/MainActivity.java)

A single single Fragment responsible for dislaying images - [ImagesFragment.java](app/src/main/java/com/marklynch/currencyfair/ui/main/ImagesFragment.java)

A package for interactions with the Flickr Server - [io/flickr](app/src/main/java/com/marklynch/currencyfair/io/flickr)

A View Model for linking the back and front ends of the app - [MainViewModel.java](app/src/main/java/com/marklynch/currencyfair/ui/main/MainViewModel.java)

#### Gallery with Search Bar Visible
![](gallery_with_search_bar.png)

#### Gallery with Search Bar Hidden
![](gallery_without_search_bar.png)

#### Expanded Image
![](expanded_image_1.png)

#### Expanded Image
![](expanded_image_2.png)
