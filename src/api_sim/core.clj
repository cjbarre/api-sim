(ns api-sim.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.walk :as walk]
            [bidi.ring :as bidi]
            [ring.util.response :as r]))

(def api-spec (json/read-str (slurp "v1.json") :key-fn keyword))

(defn make-endpoint
  [endpoint-spec]
  (let [headers (walk/stringify-keys (:headers endpoint-spec))
        {:keys [name status body]} endpoint-spec]
    {name (fn
           [request]
           {:status status
            :body body
            :headers headers})}))

(defn make-endpoints
  [api-spec]
  ["/"
   (->> (map make-endpoint api-spec) (reduce merge))])

(def api-sim
  (bidi/make-handler (make-endpoints api-spec)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (jetty/run-jetty api-sim {:port 3000}))
