Subsequences Local Content
==========================

This module contains a sample on how to leverage subsequences to manage branch-specific content in a Screens project.
It will use a master channel for the whole project with a placeholder that runs branch-specific content using a subsequence component.

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
    + [Master Channel](http://localhost:4502/screens.html/content/screens/screens-howto/channels/local-content-subsequence/master)
    + [Master Channel - edition](http://localhost:4502/editor.html/content/screens/screens-howto/channels/local-content-subsequence/master.html)
    + [Default Schedule](http://localhost:4502/screens/dashboard/schedule.html/content/screens/screens-howto/schedules/local-content--default-schedule)
    + [Branch locations](http://localhost:4502/screens.html/content/screens/screens-howto/locations/local-content-subsequence)
        + [Local Channel 1](http://localhost:4502/screens.html/content/screens/screens-howto/locations/local-content-subsequence/branch-office-1/local)
        + [Local Channel 1 - edition](http://localhost:4502/editor.html/content/screens/screens-howto/locations/local-content-subsequence/branch-office-1/local.html)
        + [Local Channel 2](http://localhost:4502/screens.html/content/screens/screens-howto/locations/local-content-subsequence/branch-office-2/local)
        + [Local Channel 2 - edition](http://localhost:4502/editor.html/content/screens/screens-howto/locations/local-content-subsequence/branch-office-2/local.html)
