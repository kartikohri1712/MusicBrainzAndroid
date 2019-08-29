# MusicBrainz Android app

This project was part of  Google Summer of Code 2019.

The official android app for MusicBrainz. MusicBrainz for Android lets users search metadata all of the the metadata available on MusicBrainz. Users can scan barcode to search albums. Registered users can view, edit and modify their collections. Users can also tag their audio files.
# What's next
* The tagger is still in initial stages. The app needs to achieve complete usability with the desktop version of Picard. Many audio formats are not suppored yet. There is no support for Cover Art. In the supported formats, not all tags are currently be allowed. At many points, the JAudioTagger library being used underneath to interact with tags limits the usability. Either work has to be done at developing at a new library for JAVA which fulfills the needs or otherwise a suitable C/C++ can be used with the help of Andrid NDK and JNI.
* At this moment, users are unable to modify or create collections. This needs to be fixed.
* Also, the app is lacking a good and user friendly UI/UX.
* It would also be great if the codebase was moved to Kotlin to make it more maintainable.
