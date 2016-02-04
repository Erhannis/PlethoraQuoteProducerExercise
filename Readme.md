Plethora Technical Exercise
===

The aim of this exercise is to test your ability to write clean, well structured code. Feel free to choose any imperative object oriented language that is freely available. 
 
You are given the task of automating the quoting for parts to be made with a 2 axis laser cutting machine. When a user submits their design for laser cutting, it automatically gets converted to a 2D object which represents the profile of the design's extrusion. 

Profile Representation
---

A profile contains a set of **edges**, each of which derive from a type of curve. Edges adjoin to one another at **vertices** so that each edge has one or two vertex endpoints. Each edge and vertex element is keyed by a unique integer, *id*. A profile is stored in a JSON file that is organized like [Schema.json](https://gist.github.com/o8ruza8o/1e066a602fb0649b668c#file-schema-json).
 
We will consider two types of curve in this exercise, straight line segments and circular arcs. While a line segment is completely defined via its vertices, circular arcs contain the additional *Center* and *ClockwiseFrom* fields. The *ClockwiseFrom* field references the vertex from which the circular arc may be drawn clockwise until it reaches the other vertex for that edge.
 
All units are in inches.

Quoting
---

Main considerations that should be taken into account when quoting a part are material costs and machine cost.  
 
Material costs are proportional to the area of stock used for the part in optimal orientation. Stock is pre cut into rectangular shape where, to consider kerf thickness from the laser, additional padding is added to the design's bounds in each dimension to define stock size. 
 
Machine costs are proportional to the time laser spends cutting. It may be considered that the speed of the laser traveling in a straight line is the maximal laser cutting speed, `v_max`, while for a circular arc of nonzero radius, `R`, it is given by `v_max * exp(-1/R)`.

Task
---

  (1) Write code to deserialize extrusion profiles so that it can be represented in memory.
  
  (2) Write a program that takes a profile and produces a quote. Assume:
  
    - Padding: 0.1in
    
    - Material Cost: $0.75/in^2
    
    - Maximal laser cutter speed: 0.5 in/s
    
    - Machine Time Cost: $0.07/s
  
  (3) Keep all of your progress in a git repository and when you are done, push your repository and send an email letting me know you're done.
  
  (4) Include a brief description of how to use your code and what you would do to improve it if you had more time. Please, make sure to reference any external code used.

Examples
---

Three example JSON files are provided for you to test your code:

  (1) [Rectangle.json](https://gist.github.com/o8ruza8o/1e066a602fb0649b668c#file-rectangle-json) - a simple 3in x 5in rectangle.
  
  Your program should output: `14.10 dollars`
  
  (2) [ExtrudeCircularArc.json](https://gist.github.com/o8ruza8o/1e066a602fb0649b668c#file-extrudecirculararc-json) - a 2in x 1in rectangle with semicircle added onto one of the 1in sides.
  
  Your program should output: `4.47 dollars`
  
  (3) [CutCircularArc.json](https://gist.github.com/o8ruza8o/1e066a602fb0649b668c#file-cutcirculararc-json) - a 2in x 1in rectangle with semicircle cut into one of the 1in sides.
  
  Your program should output: `4.06 dollars`



Individual Explanation
---

I've done the tasks listed, and also included a GUI, which will allow you to view profiles (and their bounding box), edit/create profiles, and save them.

To run it, after building it (probably in NetBeans, and with Java 8, but however you get it to compile works just as well), do

java -jar PlethoraQuoteProducer.jar ../Rectangle.json

to get the number alone.  If you want to see the gui, append "ui", like

java -jar PlethoraQuoteProducer.jar ../Rectangle.json ui

and it'll bring up the ui.  There's a help dialog.

It works basically by generating a convex hull of the profile, and then tries bounding rectangle parallel to each edge of the hull.  This is based on my (unproven) assertion that one of these edges runs parallel to a minimum bounding rectangle.  I've run a number of tests, and so far it seems to hold up so far, except in the case of, say, a convex shape comprised mostly of arcs (consider a leaf shape), where the true hull has few or no straight edges.  In such a case, my program may be slightly inaccurate (but still seems to tend towards the right answer).

As far as what I'd improve, I've already spent the time to do most of the things I could think of to do.  However, it does bug me that there are a few cases it won't handle quite right.  Other approaches I could try, possibly in conjunction with my current one, include using an optimization algorithm on the function from rotation to cost, and simply reduce it as much as possible like that.  It'd be susceptible to local minima, but in combination with the other algorithm, it might improve handling of the edge cases.  I could also use a more efficient convex hull algorithm.

Things I looked up:
* Parsing JSON in Java - used GSON
* Algorithms for finding convex hulls - wikipedia.  Modified the gift-wrapping algorithm.
* Looked up the rotation matrix formula on wikipedia.
* Looked up how to format dollar amounts properly - http://stackoverflow.com/a/13791420/513038
* Looked up some of the Java API
* Looked up convenient ways of making a checkbox list element; ended up using a table - http://stackoverflow.com/a/19796/513038
