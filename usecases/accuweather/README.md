# AccuWeather API integration

This module contains a sample on how to leverage AccuWeather API to run targeted content in a Screens project.

# Installation

This module requires the _HowTo project_ and is part of the install process. Follow the [setup instructions](../../README.md). 

If you still want to install the module individually, you can run:

```
mvn clean install content-package:install
```

# Entry points

+ [Video recording](https://www.dropbox.com/s/pkbaoqbijwhqoeq/Data_triggers_howto_part1.mov?dl=0) showcasing the use case
+ Content
    + [Content based on weather channel in DCC](http://localhost:4502/screens.html/content/screens/screens-howto/channels/data-trigger-accuweather)
    + [Content based on weather - edition](http://localhost:4502/editor.html/content/screens/screens-howto/channels/data-trigger-accuweather/channel.edit.html)
+ Rules / Segments (access: Navigation > Personalization > Audiences)
    + [Rainy weather](http://localhost:4502/editor.html/etc/segmentation/contexthub/screens-howto/screens-howto-rainy.html)
    + [Sunny weather](http://localhost:4502/editor.html/etc/segmentation/contexthub/screens-howto/screens-howto-sunny.html)
+ ContextHub (access: Tools > Sites > ContextHub > screens-howto > ContextHub Configuration)
    + [AccuWeather store](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub.html)
    + [AccuWeather store config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/accuweather.edit.html)
    + [AccuWeather module](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui.html)
    + [AccuWeather module config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui/accuweather.edit.html)  
