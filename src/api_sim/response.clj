(ns api-sim.response
  (:require [clojure.walk :as walk]))

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


(defn headers [{:keys [headers] :as endpoint-spec}]
  (if-not headers {:headers {}}
    {:headers (walk/stringify-keys headers)}))

(defn body [{:keys [body] :as endpoint-spec} {:keys [route-params params] :as request}]
  (if-not body {:body ""}
    {:body (fill-template body (merge (walk/stringify-keys route-params) params))}))

(defn status [{:keys [status] :as endpoint-spec}]
  (if-not status {:status 200}
    {:status status}))

(defn response [status body headers]
  (merge status body headers))
