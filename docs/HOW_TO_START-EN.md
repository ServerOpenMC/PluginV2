# How to start the OpenMC server locally?

### **Information** : This tutorial is currently based on IntelliJ Idea and was written on a computer with a Linux operating system. It is possible that the different ways of getting the result may not work. In this case go ask on the [Discord](discord.openmc.fr) if people can help you.

### During this tutorial there will be two operating methods: a **simple** one (Solution 1) and a **"hard" one for beginners** (Solution 2).

### 1st step :
**Install** [IntelliJ Idea](https://www.jetbrains.com/idea/download/)

### 2nd step :
Open IntelliJ Idea in the project root folder.

**Solution 1:**
Look in the sidebar for a sort of elephant.

![Image of the Gradle extension](images/gradle-extension.png)

Then, go to the Tasks section; then to the Run Paper section. You will see that four options are available to you.

- cleanPaperCache (allows deleting the generated cache **except** the plugins cache)
- cleanPaperPluginsCache (allows deleting the generated plugins cache)
- runDevBundleServer (I don't know but apparently it's to launch a server and test it (not interesting in our case))
- runServer (start the server)

The last one interests us more particularly; it allows launching the server. So to start it double-click on it or Right Click then Run

![Image of the button to launch the server](images/scructure-gradle.png)

**Solution 2:**

Open a command terminal in the project root and run this :

``` ./gradlew runServer ```

### 3rd step
**Solution 1 and 2:**
Open Minecraft in 26.3 and connect to the server :
``` localhost:25565 ```

![Image of connection to Minecraft](images/connexion-in-minecraft.png)

### From then on you will be on your local OpenMC instance !

## Startup troubles and bugs
| Error | Reason | How to solve it |
|---|---|---|
|Unrecognized VM option | An argument that Java does not recognize | Go to [build.gradle](../build.gradle#L162)
|error: Java 25 not resolved | Java 25 is not installed | Manually download JDK 25
|This list can be improved | Contribute to this one | [Contribute on GitHub](https://github.com/ServerOpenMC/Plugin-V2)