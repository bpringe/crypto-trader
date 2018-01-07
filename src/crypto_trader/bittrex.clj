(ns crypto-trader.core
  (:require 
    [pandect.algo.sha512 :refer :all]
    [cheshire.core :refer :all]
    [clj-http.client :as http]
    [environ.core :refer [env]]))

(def config {
             :getbalances-url "https://bittrex.com/api/v1.1/account/getbalances"
             :getticker-url "https://bittrex.com/api/v1.1/public/getticker"
             :buylimit-url "https://bittrex.com/api/v1.1/market/buylimit"
             :bittrex-api-key (env :bittrex-api-key)
             :bittrex-api-secret (env :bittrex-api-secret)})

(defn generate-nonce
  []
  (System/currentTimeMillis))

(defn generate-sha512-hmac
  [url]
  (sha512-hmac url (:bittrex-api-secret config)))

(defn append-query-params
  [url params]
  (reduce 
    (fn [string kvp]
      (-> (if (clojure.string/includes? string "?")
           (str string "&")
           (str string "?"))
       (str (name (first kvp)) "=" (second kvp))))
    url
    (seq params)))

(defn append-auth-params
  [url]
  (append-query-params 
    url 
    {:apikey (:bittrex-api-key config), :nonce (generate-nonce)}))

(defn make-auth-request
  ([url]
   (let [url (-> url
                 append-auth-params)
             hmac (generate-sha512-hmac url)]
     (http/get url {:headers {"apisign" hmac} :as :json})))
  ([url params]
   (let [url (-> url
                 append-auth-params
                 (append-query-params params))
             hmac (generate-sha512-hmac url)]
     (http/get url {:headers {"apisign" hmac} :as :json}))))

(defn make-request
  ([url]
   (http/get url {:as :json}))
  ([url params]
   (-> url
       (append-query-params params)
       http/get {:as :json})))

(defn get-ticker
  "Get the ticker for a market. e.g. USDT-ETH"
  [market]
  (make-request 
    (:getticker-url config)
    {:market market}))

(defn get-balances
  []
  (make-auth-request (:getbalances-url config)))

(defn buy-limit
  [market quantity rate]
  (make-auth-request 
    (:buylimit-url config) 
    {:market market,
     :quantity quantity
     :rate rate}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
