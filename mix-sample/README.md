Kiki Mix(Install/Update) Sample
---
In This sample, you can see the sample for a multi-flavor project which have different flavors for app stores and web 
to handle update in both flavors, due to app stores limitation (about update rules)

With KIKI install library in web flavor, you can handle your update by downloading your package, 
and install it by KIKI install library. or you can handle it by send the user to browser, to download 
your apk and install it via browser. 

with update library you can have a backup plan for your app store version without any special permission.
(In a install library kiki grants REQUEST_INSTALL_PACKAGES)

There is a `web` flavor and a `market` flavor.
in web flavor we use install library and in market flavor we use update library. 

no-op libs
----
There are no op libraries for update and install. 
The no op library has the same api with the normal one but it has no implementations, and do nothing.
So you can use it to build the project without any source code management for other flavours. 