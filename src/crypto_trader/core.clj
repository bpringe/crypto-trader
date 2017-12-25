(ns crypto-trader.core
  (:require 
    [pandect.algo.sha512 :refer :all]
    [cheshire.core :refer :all]
    [clj-http.client :as http]
    [environ.core :refer [env]]
    [clj-time.core :as t]))

(def config {:api-base-url "https://api.gdax.com"})

(defn get-ticker
  [product-id]
  (-> (str (:api-base-url config) "/products/" product-id "/ticker")
      (http/get {:as :json})
      :body))

(defn get-products
  []
  (-> (str (:api-base-url config) "/products")
      (http/get {:as :json})
      :body))

(defn get-order-book
  ([product-id]
   	(-> (str (:api-base-url config) "/products/" product-id "/book")
       	(http/get {:as :json})
       	:body))
  ([product-id level]
   	(-> (str (:api-base-url config) "/products/" product-id "/book?level=" level)
        (http/get {:as :json})
        :body)))

(defn get-trades
  [product-id]
  (-> (str (:api-base-url config) "/products/" product-id "/trades")
      (http/get {:as :json})
      :body))

(defn build-historic-rates-url
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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
