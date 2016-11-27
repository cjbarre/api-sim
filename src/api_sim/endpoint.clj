(ns api-sim.endpoint
  (:require [api-sim.route :refer [make-route]]
            [api-sim.response :refer [response status headers body]]
            [clojure.walk :as walk]))

(defn active?
  [endpoint]
  (let [active-flag (:active endpoint)]
    (= true active-flag)))

(defn handler [endpoint-spec]
  (fn [request]
    (response
      (status endpoint-spec)
      (headers endpoint-spec)
      (body endpoint-spec request))))

(defn route [{:keys [name] :as endpoint-spec}]
  (make-route name))

(defn method [{:keys [method] :as endpoint-spec}]
  (if-not method nil
    (-> method
        clojure.string/lower-case
        keyword)))

(defn endpoint [method route handler]
  (if-not method {route handler}
    {method {route handler}}))

(defn make-endpoint
  [endpoint-spec]
  (endpoint
    (method endpoint-spec)
    (route endpoint-spec)
    (handler endpoint-spec)))

(defn make-endpoints [{:keys [root endpoints] :as api-spec}]
  [(if root root "/")
   (->> (filter active? endpoints)
        (map make-endpoint)
        (reduce merge))])
