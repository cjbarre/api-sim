(ns api-sim.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.edn :as edn]
            [bidi.ring :as bidi]
            [ring.util.response :as r]
            [ring.middleware.params :as p]
            [taoensso.timbre :as log]
            [api-sim.endpoint :refer [make-endpoints]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [spec (edn/read-string (slurp "spec.edn"))
        endpoints (make-endpoints spec)
        handler (bidi/make-handler endpoints)
        _ (log/debug endpoints)]
    (jetty/run-jetty (p/wrap-params handler) {:port 3000})))
