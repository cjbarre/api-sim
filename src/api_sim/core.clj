(ns api-sim.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [bidi.ring :as bidi]
            [ring.util.response :as r]
            [ring.middleware.params :as p]
            [clojure.string :as string]
            [api-sim.endpoint :refer [make-route]]))

(def api-spec (json/read-str (slurp "api-spec.json") :key-fn keyword))

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

(defn make-endpoint
  [{:keys [name status headers body method] :as endpoint-spec}]
  (let [route (make-route name)
        handler-fn (fn [{:keys [route-params params] :as request}]
                    {:status status
                     :body (fill-template body (merge (walk/stringify-keys route-params) params))
                     :headers (walk/stringify-keys headers)})
        built-route {route handler-fn}]
    (if-not method built-route
      {(keyword method) built-route})))



(defn make-endpoints [{:keys [root endpoints] :as api-spec}]
  [(if root root "/")
   (->> (map make-endpoint endpoints) (reduce merge))])

(def api-sim
  (bidi/make-handler (make-endpoints api-spec)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (jetty/run-jetty (p/wrap-params api-sim) {:port 3000}))
