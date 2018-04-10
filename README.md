# SwingPlus

[![Build Status](https://travis-ci.org/Sciss/SwingPlus.svg?branch=master)](https://travis-ci.org/Sciss/SwingPlus)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/swingplus_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/swingplus_2.11)

## statement

SwingPlus tries to fill in the holes left behind by Scala-Swing. From missing `.width` and `.height` methods to
missing components such as `Spinner` to additional components such `GroupPanel`. It also fixes the incompatibility
of certain components with Java 7.

SwingPlus is (C)opyright 2013&ndash;2018 Hanns Holger Rutz and released under
the [GNU Lesser General Public License](https://raw.github.com/Sciss/SwingPlus/master/LICENSE) v2.1+ and comes
with absolutely no warranties. To contact the author, send an email to `contact at sciss.de`.

It contains some classes (e.g. `ComboBox`) derived from the original Scala-Swing package, (C)opyright 2007-2013,
LAMP/EPFL, which was released under a BSD style license.

## requirements / installation

This project currently compiles against Scala 2.12, 2.11, using sbt.

To use the library in your project:

    "de.sciss" %% "swingplus" % v

The current version `v` is `"0.3.0"`

To _compile_ the project, you must currently use JDK 7 or newer. This is because some Java Swing classes were
retrofitted with generics, something that the Scala compiler chokes on when using JDK 6. Note however, that
SwingPlus __can be used both under JDK 6 and newer__ (that is the entire point of providing our own versions
of `ComboBox` and `ListView`).

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## documentation

All classes and methods reside in package `de.sciss.swingplus`.

### additional standard components

- `GroupPanel` is a panel with a group-layout. I departed from Andreas Flierl's approach and simplified or changed the API, reducing the amount of complex conversions. I think I have arrived at a reasonable simple to use component.
- `OverlayPanel` is a panel with an overlay-layout.
- `ToolBar` wraps the corresponding javax component.
- `Separator` wraps the corresponding javax component.
- `Spinner` wraps the corresponding javax component. Currently it still relies on javax's `SpinnerModel`. A future version might seem a more Scala'ish wrapper for the model, too.
- `PopupMenu` wraps the corresponding javax component. Note that Scala 2.11 _does_ have a popup menu now, but you can use this one for compatibility between Scala 2.10 and 2.11.
- `ComboBox` does away with the Scala-Swing version that has problems compiling under JDK 7 due to the retrofitting of generics in Swing. It achieves this by hiding the peer type, at the same time adding a few missing methods and therefore making it usually unnecessary to gain access to the `JComboBox` peer type. I have also added a proper `Model` wrapper.
- `ListView` has the same problem as `ComboBox` in standard Scala-Swing. The version provided here is also usable in project that want to allows compilation both in JDK 6 and 7. It also has a proper `Model` wrapper. Note that it fires events in `swingplus.event` instead of `scala.swing.event`.
- `ScrollBar` fixes the Scala-Swing version by dispatching `ValueChanged` events.
- `DropMode` is a type-safe enumeration wrapping the javax constants. It is currently used for the `ListView`.

### additional utility components

- `DoClickAction` is an action that causes a visual button press
- `Labeled` is a useful class for presenting fully typed objects in a combo-box, providing an alternative string representation
- `PaddedIcon` adds an extra margin to an existing `Icon`
- `SpinningProgressBar` is a small indefinite progress bar that can be hidden. Some look-and-feels (e.g. Aqua) support rendering a spinning icon instead of a bar.
- `SpinnerComboBox` is a combo-box with a `Spinner` for editing numbers.

### extension methods

They are imported through `swingplus.Implicits._`.

- `UIElement`. Methods `width` and `height` are provided, so one does not have to create a `Dimension` via `size` first.
- `Component`. Method `baseline` is provided. This is currently using the `Int` type of javax. A future version might use a type-safe enumeration instead. Method `clientProps` creates a lightweight wrapper for the component's client properties, providing a subset of methods familiar from Scala's `collection.mutable.Map`.
- `Frame`. Method `defaultCloseOperation` is provided with the type-safe enumeration type `CloseOperation`.
- `Action.wrap` allows one to wrap a javax `Action` as a Scala-Swing `Action`.

## related

Please also check out the [ScalaSwingContrib](https://github.com/benhutchison/ScalaSwingContrib) project which has similar goals.

Also check out the [TreeTable](https://github.com/Sciss/TreeTable) project which provides a very powerful component, combining tree and table. Ideally this will also go into the mix, but API needs to be properly developed and should negotiate common interface with Ken Scambler's tree implementation.

Finally, there is [Desktop](https://github.com/Sciss/Desktop), which aims rather at an application framework based on Swing.
