(ns crypto-trader.core
  (:require 
    [pandect.algo.sha512 :refer :all]
    [cheshire.core :refer :all]
    [clj-http.client :as http]
    [environ.core :refer [env]]))

(defn get-ticker
  "Get the ticker for a currency pair. e.g. USDT-ETH"
  [pair]
  (-> (str "https://bittrex.com/api/v1.1/public/getticker?market=" pair)
    http/get
    :body
    (parse-string true)))

(defn generate-getbalances-url
  []
  (str "https://bittrex.com/api/v1.1/account/getbalances?apikey="
    (env :api-key) "&nonce=" (System/currentTimeMillis)))

(defn generate-hmac
  [url]
  (sha512-hmac url (env :api-secret)))

(defn get-balances
  []
  (let [url (generate-getbalances-url)
        hmac (generate-hmac url)]
  	(-> (http/get url {:headers {"apisign" hmac}})
         :body
         (parse-string true))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
