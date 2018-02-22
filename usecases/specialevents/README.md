Special Events Content
======================

This module contains a sample on how to leverage channel priorities to manage local content overrides in a Screens project.
It will use a master channel for the whole project with local channels having higher priorities at specific times of the day.

Installation
------------

This module requires HowTo project and is part of the install process. Follow [instructions here](../../README.md).

If you still want to install the module individually, you can run:

```
mvn clean install content-package:install
```

Entry points
------------

+ Content
    + [Master Channel](http://localhost:4502/screens.html/content/screens/screens-howto/channels/special-event-channel-priority/master-channel)
    + [Master Channel - edition](http://localhost:4502/editor.html/content/screens/screens-howto/channels/special-event-channel-priority/master-channel.html)
    + [Branch locations](http://localhost:4502/screens.html/content/screens/screens-howto/locations/special-event-channel-priority)
        + [Local Channel 1](http://localhost:4502/screens.html/content/screens/screens-howto/locations/special-event-channel-priority/branch-office-1/local)
        + [Local Channel 1 - edition](http://localhost:4502/editor.html/content/screens/screens-howto/locations/special-event-channel-priority/branch-office-1/local.html)
        + [Local Channel 2](http://localhost:4502/screens.html/content/screens/screens-howto/locations/special-event-channel-priority/branch-office-2/local)
        + [Local Channel 2 - edition](http://localhost:4502/editor.html/content/screens/screens-howto/locations/special-event-channel-priority/branch-office-2/local.html)
