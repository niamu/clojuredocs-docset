(ns clojuredocs-docset.core
  (:require [clojure.java.jdbc :as j]
            [clojure.java.shell :refer [sh]]
            [clojure.java.io :refer [file resource]])
  (:import [org.jsoup Jsoup]
           [org.apache.commons.io FileUtils])
  (:gen-class))

(def user-dir (System/getProperty "user.dir"))
(def conf (read-string (slurp (resource "config.clj"))))

(defn file-ref
  [file-name]
  (file user-dir file-name))

(defn resource-copy
  [src dest]
  (FileUtils/copyURLToFile (resource src) (file-ref dest)))

(def sqlite-db {:dbtype "sqlite"
                :dbname (:db-file-path conf)})

(defn print-progress
  [percent text]
  (let [x (int (/ percent 2))
        y (- 50 x)]
    (print "\r" (apply str (repeat 100 " "))) ;; clear existing text
    (print "\r" "["
           (apply str (concat (repeat x "=") [">"] (repeat y " ")))
           (str "] " percent "%") text)
    (when (= 100 percent) (println))
    (flush)))

(defn mirror-clojuredocs
  []
  (print-progress 15 "Mirroring clojuredocs.org/core-library")
  (.mkdirs (file-ref (:docset-template conf)))
  (apply sh (conj (:httrack conf)
                  (:docset-template conf))))

(defn create-docset-template
  []
  (print-progress 40 "Creating docset template")
  (resource-copy "assets/icon.png" "resources/clojure-docs.docset/icon.png")
  (resource-copy "assets/Info.plist" "resources/clojure-docs.docset/Contents/Info.plist"))

(defn clear-search-index
  []
  (print-progress 60 "Clearing index")
  (j/db-do-commands sqlite-db
                    ["DROP TABLE IF EXISTS searchIndex"
                     "CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT)"
                     "CREATE UNIQUE INDEX anchor ON searchIndex (name, type, path)"]))

(defn populate-search-index
  [rows]
  (j/insert-multi! sqlite-db :searchIndex rows))

(defn search-index-attributes
  [element]
  {:name (.text element)
   :type "Function"
   :path (str "clojuredocs.org" (subs (.attr element "href") 2))})

(defn generate-search-index
  []
  (print-progress 75 "Generating index")
  (let [html-content (slurp (str user-dir "/" (:index-html-path conf)))
        document (Jsoup/parse html-content)
        rows (map
              search-index-attributes
              (.select (.getAllElements document) ".col-sm-10 a"))]
    (populate-search-index rows)))

(defn generate-docset
  []
  (mirror-clojuredocs)
  (create-docset-template)
  (clear-search-index)
  (generate-search-index)
  (print-progress 100 "Done."))

(defn -main
  [& args]
  (alter-var-root #'*read-eval* (constantly false)) ;; change dangerous defaults
  (generate-docset)
  (shutdown-agents))
