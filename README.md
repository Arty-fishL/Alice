# Alice 2.4 Extended Source Project (AXSP)
A project that aims to take a forked version of Alice 2.2 and:
1. Reformat the code to look nicer.
2. Update the Java environment to Java 7.
3. Remove as many code warnings as possible (just 3000 to go!).
4. Update the code to match the Alice 2.4 source code.

The Alice 2.4 source code has not been made public, 
however we can work it out:
1. Take original Alice 2.2 source code from first commit of this repository.
2. Compile the code to a jar.
3. Download the latest Alice 2.4 source code.
4. Get the alice.jar out of it.
5. Use JD-GUI (Java Decompiler) to decompile each jar to a folder.
6. Ensure JD-GUI had line and meta comments turned off in preferences.
7. Open both decompiled sources as Eclipse projects.
8. Use Eclipse to format entire source code for each project.
9. Use Meld (file diffing tool) on both project folders.
10. See what has been updated in Alice 2.4.
11. Add new files to this source code.
12. Apply all remaining changes.

I need this code for a small project of mine (that may never actually see the light of day).
