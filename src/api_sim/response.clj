(ns api-sim.response
  (:require [clojure.walk :as walk]
            [clojure.data.json :as json]))

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
  (cond
    (nil? body) {:body ""}
    (map? body) {:body (-> body (json/write-str :key-fn name) (fill-template (walk/stringify-keys (merge route-params params))))}
    :else {:body (-> body (fill-template (walk/stringify-keys (merge route-params params))))}))

(defn status [{:keys [status] :as endpoint-spec}]
  (if-not status {:status 200}
    {:status status}))

(defn response [status body headers]
  (merge status body headers))
