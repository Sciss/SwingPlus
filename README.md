# SwingPlus

## statement

SwingPlus tries to fill in the holes left behind by Scala-Swing. From missing `.width` and `.height` methods to missing components such as `Spinner` to additional components such `GroupPanel`.

SwingPlus is (C)opyright 2013&ndash;2014 Hanns Holger Rutz and released under the [GNU Lesser General Public License](https://raw.github.com/Sciss/SwingPlus/master/LICENSE) v2.1+ and comes with absolutely no warranties. To contact the author, send an email to `contact at sciss.de`.

It contains some classes (e.g. `ComboBox`) derived from the original Scala-Swing package, (C)opyright 2007-2013, LAMP/EPFL, which was released under a BSD style license.

## requirements / installation

This project currently compiles against Scala 2.11, 2.10 using sbt 0.13.

To use the library in your project:

    "de.sciss" %% "swingplus" % v

The current stable version `v` is `"0.1.2"`

## related

Please also check out the [ScalaSwingContrib](https://github.com/benhutchison/ScalaSwingContrib) project which has similar goals.

Also check out the [TreeTable](https://github.com/Sciss/TreeTable) project which provides a very powerful component, combining tree and table. Ideally this will also go into the mix, but API needs to be properly developed and should negotiate common interface with Ken Scambler's tree implementation.

Finally, there is [Desktop](https://github.com/Sciss/Desktop), which aims rather at an application framework based on Swing.
