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

(defn prepare-route-param
  [route-piece]
  (let [regexp #"\{[a-zA-Z]+\}"
        param? (re-find regexp route-piece)]
    (if-not param? (str route-piece "/")
      (keyword (clojure.string/replace route-piece #"\{|\}" "")))))

(defn make-route
  [endpoint-name]
  (->> (clojure.string/split endpoint-name #"/")
       (map prepare-route-param)
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
