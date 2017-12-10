(ns crypto-trader.core
  (:require 
    [pandect.algo.sha512 :refer :all]
    [cheshire.core :refer :all]
    [clj-http.client :as http]
    [environ.core :refer [env]]))

(def config {
  :getbalances-url "https://bittrex.com/api/v1.1/account/getbalances",
  :getticker-url "https://bittrex.com/api/v1.1/public/getticker",
  :buylimit-url "https://bittrex.com/api/v1.1/market/buylimit"
})

(defn parse-body
  [response]
  (-> response :body (parse-string true)))

(defn generate-nonce
  []
  (System/currentTimeMillis))

(defn generate-hmac
  [url]
  (sha512-hmac url (env :api-secret)))

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
    {:apikey (env :api-key), :nonce (generate-nonce)}))

(defn make-auth-request
  ([url]
    (let [url (-> url
                  append-auth-params)
                  hmac (generate-hmac url)]
      (->> (http/get url {:headers {"apisign" hmac}})
         parse-body)))
  ([url params]
    (let [url (-> url
                  append-auth-params
                  (append-query-params params))
                  hmac (generate-hmac url)]
      (->> (http/get url {:headers {"apisign" hmac}})
           parse-body))))

(defn make-request
  ([url]
    (-> url
        http/get
        parse-body))
  ([url params]
    (-> url
        (append-query-params params)
        http/get
        parse-body)))

(defn get-ticker
  "Get the ticker for a market. e.g. USDT-ETH"
  [market]
  (make-request 
    (:getticker-url config)
    {:market market}))

(defn get-balances
  []
  (make-auth-request (:getbalances-url config) ))

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
