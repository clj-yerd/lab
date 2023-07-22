(ns yerd.inspect.tap.core
  "Entity and functions for tapping a value with a locaiton."
  (:require
   [clojure.core            :as core]
   [yerd.system.plugin         :as plug]
   [yerd.code.location.core :as loc]
   [yerd.inspect.tap.schema :refer [Type Tap]]
   [yerd.ontology           :as sc])
  (:refer-clojure :exclude [intern]))
#_ (remove-ns (ns-name *ns*))

;; TODO parse containing fn?

(defn create
  "Return a tap entity that has a `location` and a `value`."
  [location value]
  (-> [location value]
      (sc/with-type Type)))

(defmacro tap
  [body]
  (let [loc  (loc/create *file* *ns* (meta &form))
        syms (->> &env keys (into []))]
    `(let [result# ~body
           loc#    (merge ~loc
                          {:body '~body
                           :env  (zipmap '~syms ~syms)})]
       ;; tap hook to run plugins on final value
       (-> loc#
           loc/tap-link
           (create result#)
           (plug/run-hooks Tap)
           tap>)

       ;; Return the result.
       result#)))

(comment
  (let [a 1
        b 2]
    (tap (+ a b)))

  (require '[vlaaad.reveal :as r])
  (tap> (r/submit 1))
  (tap 1)
  (tap> 1)
  (let [data {:foo :bar}]
    (r/submit (r/as data (r/raw-string (str data) {:fill :util}))))

  (tap> (let [data {:foo :bar}]
          (r/as data (r/raw-string (str data) {:fill :util}))))
  :end)


;; Disabled for now
#_ (plug/require plug/Reveal)


(comment
  ;; TAGGED LITERAL EXPERIMENT
  ;; TLDR tagged literals don't support line numbers.

  (defn read-form
    "data reader for use with tagged literals"
    [form]
    `(tap ~form))

  ;; Dynamically add a reader tag.
  (require '[shed.data-reader :as dr])
  (dr/add-data-reader! 'tap #'read-form)

  (read-string "#tap 123")
  ;; #tap :blue

  (dr/add-data-reader! 'shed.tap #'read-form)

  ;; if has "." in symbol name then tries to read a record
  (read-string "#shed.tap 123")
  ;; #shed.tap :red

  (dr/add-data-reader! 'shed/tap #'read-form)
  (read-string "#shed/tap 123")
  ;; #shed/tap :red

  :end)

(defn intern
  "Intern `tap` into `clojure.core.`"
  []
  ;; Ref: https://stackoverflow.com/questions/20831029/how-is-it-possible-to-intern-macros-in-clojure
  (core/intern 'clojure.core
               (with-meta 'tap {:macro true})
               @#'tap))

;; ? put this in start?
;; ? how to remove it?  (ns-unmap 'user 'foo)
(intern)
