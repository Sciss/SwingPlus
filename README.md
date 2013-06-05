# SwingPlus

## statement

SwingPlus tries to fill in the holes left behind by Scala-Swing. From missing `.width` and `.height` methods to missing components such as `Spinner`. SwingPlus is released under the [GNU Lesser General Public License](https://raw.github.com/Sciss/SwingPlus/master/LICENSE) v2.1+ and comes with absolutely no warranties. To contact the author, send an email to `contact at sciss.de`.

## requirements / installation

This project currently compiles against Scala 2.10 using sbt 0.12.

To use the library in your project:

    "de.sciss" %% "swingplus" % v

The current version `v` is `"0.0.+"`

## related

Please also check out the [ScalaSwingContrib](https://github.com/benhutchison/ScalaSwingContrib) project which has similar goals. SwingPlus doesn't implement anything that's covered by ScalaSwingContrib and will probably inject its contributions to that project at some point. At the moment I'm overworked and I need rapid update access to this codebase, so I cannot deal with a collaborative project right now, but eventually I will.

Also check out the [TreeTable](https://github.com/Sciss/TreeTable) project which provides a very powerful component, combining tree and table. Ideally this will also go into the mix, but API needs to be properly developed and should negotiate common interface with Ken Scambler's tree implementation.

Finally, there is [Desktop](https://github.com/Sciss/Desktop), which aims rather at an application framework based on Swing.