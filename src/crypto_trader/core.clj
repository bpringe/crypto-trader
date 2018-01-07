(ns crypto-trader.core
  (:require 
    [pandect.algo.sha512 :refer :all]
    [cheshire.core :refer :all]
    [clj-http.client :as http]
    [environ.core :refer [env]]
    [clj-time.core :as t]
    [clj-time.coerce :as c]
    [clj-time.format :as f]))

(def config {:api-base-url "https://api.gdax.com"
             :granularities {:1m 60
                             :5m 300
                             :15m 900
                             :1h 3600
                             :6h 21600
                             :1d 86400}
              :api-key (env :api-key)
              :api-secret (env :api-secret)
              :api-passphrase (env :api-passphrase)})

(defn get-products
  []
  (-> (str (:api-base-url config) "/products")
      (http/get {:as :json})
      :body))

(defn get-order-book
  ([product-id]
   (get-order-book product-id 1))
  ([product-id level]
   (-> (str (:api-base-url config) "/products/" product-id "/book?level=" level)
      (http/get {:as :json})
      :body)))

(defn get-ticker
  [product-id]
  (-> (str (:api-base-url config) "/products/" product-id "/ticker")
      (http/get {:as :json})
      :body))

(defn get-trades
  [product-id]
  (-> (str (:api-base-url config) "/products/" product-id "/trades")
      (http/get {:as :json})
      :body))

(defn- build-historic-rates-url
  [product-id start end granularity]
  (str (:api-base-url config)
       "/products/"
       product-id
       "/candles?start="
       start
       "&end="
       end
       "&granularity="
       granularity))

;; Remember to use (t/today-at 00 00) to avoid sending time ahead of server time
(defn get-historic-rates
  [product-id start end granularity]
  (-> (build-historic-rates-url product-id start end granularity)
      (http/get {:as :json})
      :body))
      
(defn get-product-stats
  [product-id]
  (-> (str (:api-base-url config) "/products/" product-id "/stats")
      (http/get {:as :json})
      :body))

(defn get-currencies
  []
  (-> (str (:api-base-url config) "/currencies")
      (http/get {:as :json})
      :body))

(defn get-time
  []
  (-> (str (:api-base-url config) "/time")
      (http/get {:as :json})
      :body))

(defn- sign-request
  [timestamp method path body]
  (let [secret (:api-secret config)]))

(defn- make-signed-request 
  [method path & [opts]]
  (let [timestamp (:epoch (get-time))
        signature (sign-request timestamp method path (:body opts))]
    (http/request
      (merge {:method method
              :url (str (:api-base-url config) path)
              :as :json
              :headers {"CB-ACCESS-KEY" (:api-key config)
                        "CB-ACCESS-SIGN" signature
                        "CB-ACCESS-TIMESTAMP" timestamp
                        "CB-ACCESS-PASSPHRASE" (:api-passphrase config)}}
             opts))))
             
              

(http/request
   {:method "GET" :url (str (:api-base-url config) "/time") :as :json})

(defn get-accounts []
  (make-signed-request "GET" "/accounts"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
