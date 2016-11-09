## [Dash][dash] docset generator for [ClojureDocs.org][clojuredocs]

Performs the following:

* Mirror clojuredocs.org/clojure_core (around 53mb) using [HTTrack][httrack]
* Copy html content to default Dash docset template
* Parse all functions from http://clojuredocs.org/core-library/vars
* Populate searchIndex in docSet.dsidx (sqlite db)

## Usage

- Install `httrack`:

        $ brew install httrack

- Clone source and optionally customize [HTTrack][httrack] in config.clj and run:

        $ lein run

Import the generated clojure-docs.docset into [Dash][dash].

## License

Copyright © 2016 Brendon Walsh

Distributed under the Eclipse Public License, the same as Clojure.

[clojuredocs]: http://clojuredocs.org
[dash]: http://kapeli.com/dash
[httrack]: http://www.httrack.com
