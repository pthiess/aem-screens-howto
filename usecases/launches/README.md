Updating a channel content using AEM launches
=============================================

This module contains a sample of using AEM launches for updatting content of Screens channels. A launch named 'Content Update' was created to update a content of 'Idle Channel'. To trigger an actual content update simply promote the launch.

Official documentation on AEM launches: https://helpx.adobe.com/experience-manager/6-3/sites/authoring/using/launches.html

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
    + [The launch for updating 'Idle Channel'](http://localhost:4502/crx/de/index.jsp#/content/launches/2018)
