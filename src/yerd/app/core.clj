(ns yerd.app.core)

;; TODO can this be merged with system.core?
;; - Note: donut is not compatible with bb
;; - ? try use closeable map?

(defn add-shutdown-hook
  [thunk]
  (-> (Runtime/getRuntime)
      (.addShutdownHook (Thread. thunk))))

(defn exit-on-interrupt
  []
  (let [interrupted (promise)]
    (add-shutdown-hook #(deliver interrupted true))
    @interrupted
    (shutdown-agents)
    (System/exit 0)))
