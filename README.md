BadGraphicsLibrary
==================

*A university practical assignment from 1999. Do not code like this!*

<br />

3d Graphics
-----------

<br />
This is graded coursework from an honours level computer science module I completed back in 1999. It shows some interesting applications of algebra on three dimensional cartesian co-ordinates i.e. how to scale, transform and rotate 3d objects. It also shows how to calculate the vector perpendicular to the surface of a polygon and work out how much light should impact upon it given a particular lightsource. Again, an interesting application of mathematics with a nice visual representation.

<br />
However...

<br />
This code was written by a teenager. I don't even know if I'd fully grasped the difference between a reference and value type by this stage of my programming career. Therefore, I have committed several heinous sins.

1. I (badly) implemented mail sort instead of using a quick sort library. This is because I had recently independently discovered mail sort so thought I should use in places regardless of appropriability.
2. My variable method naming is inconsistent and gives no clue as to what they're used for.
3. My methods are badly decomposed i.e. they're not decomposed into sensibly concise methods.
4. The class structures aren't single purpose and don't have a clear interface e.g. they allow public access to what should be internal data structures. Such adhere to SOLID mnemonic.
5. Constant numbers used throughout the code instead of being defined once in a config settings class (or equivalent).
6. The user interface sucks royally.

<br />

I'm putting this on GitHub because...?
--------------------------------------

<br />
In spite of all of its faults, it's a working bit of code that does something quite cool. It's also nice to remember the little bits and pieces I did as a student. But most importantly it's good to see how far I've come as a programmer - I may not be the best coder ever but I can solve problems and do so in a way that others can understand. It's nice to see the progress.

<br />

For those still not convinced
-----------------------------

Tidying up the code would have required practically a rewrite, however, I dropped the custom mail sort for a built-in quick sort; added some double-buffering; put in a mouse click n' drag listener and mouse wheel listener for performing the rotation and zooming; and finally switched the two state buttons for one radio button. Have a <a href="http://lifebeyondfife.com/applets/PolarBear/">Polar Bear</a>. Much nicer :)
