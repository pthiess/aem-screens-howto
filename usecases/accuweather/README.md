AccuWeather API integration
================================

Use Case
--------
Acme Corp is a multinational fast-food franchise. The company delivers content which is personalized based on the current weather in each location: if the weather is sunny, it might promote ice creams while when it is rainy or cold, it might promote hot coffee.

This how-to project walks you through how to achieve personalized content based on weather (provided by [www.accuweather.com](https://www.accuweather.com)).

The project leverages AEM [personalization](https://helpx.adobe.com/experience-manager/6-4/sites/administering/using/personalization.html) which includes the [ContextHub](https://helpx.adobe.com/experience-manager/6-4/sites/administering/using/contexthub-config.html), the [Segmentation engine](https://helpx.adobe.com/experience-manager/6-4/sites/administering/using/segmentation.html) and the [Content Targeting UI](https://helpx.adobe.com/experience-manager/6-4/sites/authoring/using/content-targeting-touch.html).

### Architecture Diagram

![AccuWeather AI integration Architecture Diagram](diagram.png)

How to Use the Sample Content
-----------------------------

- [Video recording](https://www.dropbox.com/s/pkbaoqbijwhqoeq/Data_triggers_howto_part1.mov?dl=0) showcasing the use case
- Open the [channel with content based on weather](http://localhost:4502/editor.html/content/screens/screens-howto/channels/data-trigger-accuweather/channel.edit.html)
- Switch to _Targeting_ mode
- Select _Screens HowTo_ brand and _Weather Based Campaign_ activity and hit the _Start Targeting_ button
- Explore the different content variations by switch the different _Audiences_
- Content can be edited per variation
- Switch then to _Preview_ mode
- Expand the _ContextHub_ which shows the current weather information: content displayed in the channel is the content defined for the current weather. For now, no simulation is possible and location is hardcoded to be Basel.

---

Technical Details
-----------------

### Compatibility

AEM version|Compatibility     |Comments
-----------|------------------|--------
6.3        |:white_check_mark:|
6.4        |:heavy_exclamation_mark:|While weather based content should be working, some bugs have been raised regarding segments authoring et ContextHub administration.

### Features built upon

The solution uses:
- the ContextHub
- the Segmentation engine
- the Content Targeting UI
- a standard Sequence Channel

### Manual installation

This module requires HowTo project and is part of the install process. Follow [instructions here](../../README.md).

If you still want to install the module individually, you can run:

```
mvn clean install content-package:install
```

Sample Content Links
--------------------

+ Content
    + [Content based on weather channel in DCC](http://localhost:4502/screens.html/content/screens/screens-howto/channels/data-trigger-accuweather)
    + [Content based on weather - edition](http://localhost:4502/editor.html/content/screens/screens-howto/channels/data-trigger-accuweather/channel.edit.html)
+ Rules / Segments (access: Navigation > Personalization > Audiences)
    + [Rainy weather](http://localhost:4502/editor.html/etc/segmentation/contexthub/screens-howto/screens-howto-rainy.html)
    + [Sunny weather](http://localhost:4502/editor.html/etc/segmentation/contexthub/screens-howto/screens-howto-sunny.html)
+ ContextHub (access: Tools > Sites > ContextHub > screens-howto > ContextHub Configuration)
    + [AccuWeather store](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub.html)
    + [AccuWeather store config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/accuweather.edit.html) - the config contains the Accuweather id of the Basel location. this could be changed to another location (_path_ property of the config).
    + [AccuWeather module](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui.html)
    + [AccuWeather module config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui/accuweather.edit.html)
