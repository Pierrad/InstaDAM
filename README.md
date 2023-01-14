# InstaDAM

## Geocoding API

When you upload an image to InstaDAM, you must provide a latitude and longitude. This is used to retrieve the address of the image.
To do this, we use the Google Maps Geocoding API. You can find more information about this API here: https://developers.google.com/maps/documentation/geocoding/intro
To be sure not to share the API key or give access to anyone, we decided to manage the GPS coordinates translation process on the server side.
This allows us to set a limitation on the IP address that is allowed to make requests with this API key. 