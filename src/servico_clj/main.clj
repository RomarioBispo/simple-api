(ns servico-clj.main
  (:require [servico-clj.http-server :as app-server]
            [com.stuartsierra.component :as component]
            [servico-clj.database :as db]
            [servico-clj.routes :as routes])
  (:use [clojure.pprint])
  )

(defn my-component-system []
    (component/system-map
      :db (db/create)
      :routes (routes/create)
      :server (component/using
                (app-server/create)
                 [:db :routes])))
(def component-result (component/start (my-component-system)))
(pprint component-result)
(def test-request (-> component-result :servidor :test-request))

;(test-request :get "/hello")
;(test-request :post "/tarefa?name=fazer-cafe&status=feito")
;(test-request :get "/tarefa")
;(test-request :delete "/tarefa/3a8c6426-0ebc-4ff0-90a0-e7921915cc8a")
;(test-request :post "/tarefa?name=fazer-miojo&status=pendente")
;(test-request :patch "/tarefa/14e6d833-9895-42fb-adf1-7e0bd2069256?name=fazer-miojo&status=feito")

;(println (test-request :get "/hello"))
;(println (test-request :post "/tarefa?name=fazer-miojo&status=pendente"))
;(println (test-request :post "/tarefa?name=fazer-cafe&status=feito"))
;(println (test-request :get "/tarefa"))
