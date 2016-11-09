(defproject clojuredocs-docset "0.2.0"
  :description "Dash docset generator for ClojureDocs.org"
  :url "http://github.com/niamu/clojuredocs-docset"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.jsoup/jsoup "1.8.3"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [commons-io/commons-io "2.5"]]
  :clean-targets ^{:protect false} ["resources/clojure-docs.docset"]
  :main clojuredocs-docset.core)
