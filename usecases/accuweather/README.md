# Entry points
  


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
- Modify the global content in the master sequence by editing the [Master Channel](http://localhost:4502/screens.html/content/screens/screens-howto/channels/local-content-subsequence/master)
- Modify the location specific content by editing [Local Channel 1](http://localhost:4502/screens.html/content/screens/screens-howto/locations/local-content-subsequence/branch-office-1/local) or [Local Channel 2](http://localhost:4502/screens.html/content/screens/screens-howto/locations/local-content-subsequence/branch-office-2/local)
- Modify the assignments by editing the [Default Schedule](http://localhost:4502/screens/dashboard/schedule.html/content/screens/screens-howto/schedules/local-content--default-schedule)

---

Technical Details
-----------------

### Compatibility

AEM version|Compatibility     |Comments
-----------|------------------|--------
6.3        |:white_check_mark:|
6.4        |::|

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
    + [AccuWeather store config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/accuweather.edit.html)
    + [AccuWeather module](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui.html)
    + [AccuWeather module config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui/accuweather.edit.html)
