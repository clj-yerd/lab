(ns yerd.app.cli
  (:require
   [taoensso.timbre :as timbre]))

(defn set-cli-log!
  []
  (timbre/merge-config!
   {:min-level :info
    :output-fn (fn [{:keys [vargs] :as _data}]
                 (apply pr-str vargs))}))

(defn init!
  []
  (set-cli-log!))
