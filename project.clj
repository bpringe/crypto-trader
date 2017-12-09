(defproject crypto-trader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [pandect "0.6.1"]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]
                 [environ "1.1.0"]]
  :main ^:skip-aot crypto-trader.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-gorilla "0.4.0"]
            [lein-environ "1.1.0"]])
