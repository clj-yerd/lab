(ns yerd.mermaid.cli
  "Watch, render and view mermaid files."
  (:require
   [babashka.fs         :as fs]
   [babashka.pods       :as pods]
   [babashka.process    :as p]
   [clojure.java.browse :refer [browse-url]]
   [clojure.string      :as str]
   [yerd.app.core       :as app]
   [yerd.app.cli        :as cli]
   [yerd.system.log     :refer [info]]))

;; To install:
;; bbin install yerd/yerd-lab --local/root ~/yerd-lab --main-opts='["-m" "yerd.mermaid.cli"]' --as mermaid
;; BABASHKA_BBIN_DIR

;; TODO move to fs watcher
(pods/load-pod 'org.babashka/fswatcher "0.0.3")
(require '[pod.babashka.fswatcher :as fw])

(defn render
  [mermaid-file]
  (info "rendering" mermaid-file)
  (let [png-file (-> mermaid-file fs/strip-ext (str ".png"))
        cmd ["mmdc" "-i" mermaid-file "-o" png-file "--width" "2000"]]
    (info cmd)
    (try
      (apply p/shell cmd)
      png-file
      (catch Exception _
        nil))))

;; TODO simplify to monitor an extension
(defn on-change
  [{:keys [type path] :as _event}]
  #_ (debug _event)
  (when (and (#{:create :write :write|chmod} type)
             (#{"mermaid" "mmd"} (fs/extension path))
             ;; ignore emacs save files
             (not (str/starts-with? (fs/file-name path) ".#")))
    ;; Use some-> to skip browsing when render returns nil.
    (some-> path
            render
            browse-url)))

(defn -main
  []
  (cli/init!)
  (let [dir "."]
    (fw/watch dir on-change {:recursive true})
    (info "watching dir:" (str (fs/canonicalize dir))))
  (app/exit-on-interrupt))

#_(main)
