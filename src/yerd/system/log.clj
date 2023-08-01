(ns yerd.system.log
  (:require
   [taoensso.timbre :as timbre]))

(defmacro debug
  [& args]
  `(timbre/debug ~@args))

(defmacro info
  [& args]
  `(timbre/info ~@args))

(defn cli-output
  [& args]
  (prn args))
