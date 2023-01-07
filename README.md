# InstaDAM

## Resources

https://medium.com/geekculture/implementing-the-perfect-splash-screen-in-android-295de045a8dc

Possible way to handle Image retrieved as Bitmap Buffer from API
```
// Get the image data from the response object
byte[] imageData = response.get("image");

// Create a ByteArrayInputStream from the image data
InputStream is = new ByteArrayInputStream(imageData);

// Use the BitmapFactory to decode the input stream into a Bitmap
Bitmap bitmap = BitmapFactory.decodeStream(is);

// Set the Bitmap as the source for an ImageView
ImageView imageView = findViewById(R.id.image_view);
imageView.setImageBitmap(bitmap);
```

## TODO 

- Créer une API REST permettant de - PA 
  - S’inscrire 
  - Se connecter 
  - Récupérer une liste de photos autour d’une localisation 
  - Uploader des photos liées à un utilisateur avec la géolocalisation de la photo 
- Créer une boot animation avec le logo et le nom de l’application - PA 
- Créer une page d’inscription - Colin 
- Créer une page de connexion - Colin 
- Créer une page de profil - Colin 
- Créer une page contenant le fil de photo autour de la localisation actuelle de l’utilisateur - PA 
- Permettre de prendre des photos, de les uploader et de les télécharger - Yohann 
- Envoyer des notifications pour proposer de voir de nouvelles photos - Yohann 