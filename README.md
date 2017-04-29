# Troubadour 
A modern day problem that many people face is having to endure a party host's terrible taste in music. Troubadour seeks to give the world a comprehensive solution to this problem by generating an optimized playlist based off the preferences of party attendees. Powered by Spotify's API, we can instantly generate a playlist that everyone will enjoy.

Troubadour is an Android application backed by a NodeJS server and a PostgreSQL database. We provide an interface for entering music preferences as well as an interface for generating a party playlist. Using location data and PostGIS, we are able to aggregate the preferences of all users within a closed area and generate a playlist.


## Quick-Start Guide

Troubadour will have two types of users: 'host' users and 'non-host' users.

+ The 'host' user has the ability to gather preferences from other nearby Troubadour users. With the click of a button, a playlist will be generated based off what was collected.

	+ Quick-Start Playlist Generation - Click 'Generate Playlist'. Troubadour will collect all nearby preferences, determine which preferences best fit the crowd, and generate a playlist.

	+ Advanced Playlist Generation - The same workflow as above, however the host can delete preferences or even forever blacklist them.

+ The 'non-host' user simply enters their favorite preferences and allows them to be collected at a party. They can browse Spotify's music library inside our app and add artists, tracks, albums, and genres. If you never plan on hosting a party, you can set your preferences and never have to open Troubadour again.

