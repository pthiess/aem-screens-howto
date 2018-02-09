ASCII code data-triggers
========================

This module contains a sample on how to leverage ASCII code data-triggers to run targeted content in a Screens project.

Installation
------------

This module is requires HowTo project and is part of the install process. Follow [instructions here](../../README.md).

If you still want to install the module individually, you can run:

```
mvn clean install content-package:install
```

Entry points
------------

+ [Video recording](https://www.dropbox.com/s/pkbaoqbijwhqoeq/Data_triggers_howto_part1.mov?dl=0) showcasing the use case
+ Content
    + [Content based on ascii code channel in DCC](http://localhost:4502/screens.html/content/screens/screens-howto/channels/data-trigger-asciicodes)
    + [Content based on ascii code - edition](http://localhost:4502/editor.html/content/screens/screens-howto/channels/data-trigger-asciicodes/channel.edit.html)
+ Rules / Segments (access: Navigation > Personalization > Audiences)
    + [ASCII code A](http://localhost:4502/editor.html/etc/segmentation/screens-howto/screens-howto-ascii-a.html)
    + [ASCII code B](http://localhost:4502/editor.html/etc/segmentation/screens-howto/screens-howto-ascii-b.html)
+ ContextHub (access: Tools > Sites > ContextHub > screens-howto > ContextHub Configuration)
    + [ASCII code store](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub.html)
    + [ASCII code store config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/asciicodes.edit.html)
    + [ASCII code module](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui.html)
    + [ASCII code module config](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui/asciicodes.edit.html)  
