(ns api-sim.endpoint
  (:require [api-sim.route :refer [make-route]]
            [api-sim.response :refer [fill-template]]
            [clojure.walk :as walk]))

(defn active?
  [endpoint]
  (let [active-flag (:active endpoint)]
    (= true active-flag)))

(defn- make-endpoint
  [{:keys [name status headers body method] :as endpoint-spec}]
  (let [route (make-route name)
        handler-fn (fn [{:keys [route-params params] :as request}]
                    {:status status
                     :body (fill-template body (merge (walk/stringify-keys route-params) params))
                     :headers (walk/stringify-keys headers)})
        built-route {route handler-fn}]
    (if-not method built-route
      {(keyword method) built-route})))

(defn- handler [{:keys [status headers body] :as endpoint-spec}]
  (fn [{:keys [route-params params] :as request}]
    {:status status
     :body (fill-template body (merge (walk/stringify-keys route-params) params))
     :headers (walk/stringify-keys headers)}))

(defn- route [{:keys [name] :as endpoint-spec}]
  (make-route name))

(defn- method [{:keys [method] :as endpoint-spec}]
  (if-not method nil
    (-> method
        clojure.string/lower-case
        keyword)))

(defn- endpoint [method route handler]
  (if-not method {route handler}
    {method {route handler}}))

(defn- make-endpoint
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
