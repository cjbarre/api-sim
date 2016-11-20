(ns api-sim.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.walk :as walk]
            [bidi.ring :as bidi]
            [ring.util.response :as r]
            [ring.middleware.params :as p]
            [clojure.string :as string]))

(def api-spec (json/read-str (slurp "api-spec.json") :key-fn keyword))

(defmulti make-endpoint
  "Make an endpoint handler."
  ;; Either static or dynamic
  (fn [endpoint-spec] (:behavior endpoint-spec)))

(defn fill-template
  [template parameters]
  (if (empty? parameters) template
    (->> (map #(str "\\{" (key %) "\\}") parameters)
         (clojure.string/join "|")
         re-pattern
         ((fn [pattern]
           (clojure.string/replace template pattern
             (fn [match]
               (->> (clojure.string/replace match #"\{|\}" "")
                    (get parameters)))))))))


(defmethod make-endpoint "static"
  [endpoint-spec]
  (let [headers (walk/stringify-keys (:headers endpoint-spec))
        {:keys [name status body]} endpoint-spec]
    {name (fn
           [request]
           {:status status
            :body body
            :headers headers})}))

(defmethod make-endpoint "dynamic"
  [endpoint-spec]
  (let [headers (walk/stringify-keys (:headers endpoint-spec))
        {:keys [name status body]} endpoint-spec]
    {name (fn
           [request]
           {:status status
            :body (fill-template body (:query-params request))
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
  (jetty/run-jetty (p/wrap-params api-sim) {:port 3000}))
