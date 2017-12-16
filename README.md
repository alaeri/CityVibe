# Cityvibe

[Task origin](https://gist.github.com/matts2cant/78028940eed02f2ce00ae45710a489a1)

The idea of this exercice is to create a very simple music player app. The app will contain 3 different screens:

* A view that will display a list of the top 100 tracks in the device's country, with a org.alaeri.cityvibe.search bar on top.
* A view that will display a list of org.alaeri.cityvibe.search results
* A music player that will allow a user to listen to the music 30s previews


You can use any music provider api you want (spotify, soundcloud, itunes, etc...)

For reference, here is the documentation for itunes music lookup API: https://affiliate.itunes.apple.com/resources/documentation/itunes-store-web-service-org.alaeri.cityvibe.search-api/

A few rules:

* Code the app in Kotlin
* You can use any library you want
* You can upload the code on github

##

## TODO

* [x] Display a stubbed list
* [x] Fetch data with a basic data manager and retrofit
* [x] List of top 100 songs.
* [x] Find how to best implement a good org.alaeri.cityvibe.search system : https://developer.android.com/guide/topics/org.alaeri.cityvibe.search/org.alaeri.cityvibe.search-dialog.html
* [ ] Search view and results
* [ ] Map a song to a playable song by querying for previewUrl
* [ ] Basic player
* [ ] Currently playing notification

## BACKLOG

Make a real DI rather than having a dataManager singleton in CityVibeApp ?
Add a persistence layer ?
FlavorDimension for sound provider ? Maybe first spotify as I have a premium account
FlavorDimension for technicalTest and Cityvibe with geotagged sounds ?
Make a server to have a real API?



