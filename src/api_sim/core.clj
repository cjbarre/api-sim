(ns api-sim.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [bidi.ring :as bidi]
            [ring.util.response :as r]
            [ring.middleware.params :as p]
            [clojure.string :as string]))

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

(defn split-route
  [route-string]
  (clojure.string/split route-string #"\{|\}"))

(defn keywordify-route
  [route-coll]
  (doall (map #(if-not (re-find #"/" %) (keyword %) %) route-coll)))

(defn make-route
  [endpoint-name]
  (->> endpoint-name
       split-route
       keywordify-route
       (into [])))

(defn make-endpoint
  [endpoint-spec]
  (let [headers (walk/stringify-keys (:headers endpoint-spec))
        {:keys [name status body]} endpoint-spec]
    {(make-route name)
     (fn [request]
         {:status status
          :body (fill-template body (merge (walk/stringify-keys (:route-params request)) (:params request)))
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
