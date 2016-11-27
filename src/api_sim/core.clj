(ns api-sim.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [bidi.ring :as bidi]
            [ring.util.response :as r]
            [ring.middleware.params :as p]
            [clojure.string :as string]
            [api-sim.endpoint :refer [active? make-endpoints]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [api-spec (json/read-str (slurp "api-spec.json") :key-fn keyword)
        api-sim (bidi/make-handler (make-endpoints api-spec))]
    (jetty/run-jetty (p/wrap-params api-sim) {:port 3000})))
