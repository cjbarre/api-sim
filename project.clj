(defproject api-sim "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-gorilla "0.3.6"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [org.clojure/data.json "0.2.6"]
                 [compojure "1.5.1"]
                 [bidi "2.0.14"]]
  :main ^:skip-aot api-sim.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
