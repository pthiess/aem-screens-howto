# Screens HowTo project

The Screens HowTo project consists in a collection of demo use cases illustrating how to implement them with an AEM Screens project.

# Get the code

Run the commands below to get the source code, build it and install everything you need on your running AEM 6.3 instance ([http://locahost:4502](http://locahost:4502)).
This will install:
- the HowTo project
- all the use cases

```
git clone git@git.corp.adobe.com:Screens/screens-howto.git
cd screens-howto
mvn clean install -PautoInstallSnapshot
```

# Entry points

+ Content
    + [HowTo project in DCC](http://localhost:4502/screens.html/content/screens/screens-howto)
+ ContextHub (access: Tools > Sites > ContextHub > screens-howto > ContextHub Configuration)
    + [Stores](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub.html)
    + [Modules](http://localhost:4502/etc/cloudsettings/screens-howto/contexthub/ui.html)
 
