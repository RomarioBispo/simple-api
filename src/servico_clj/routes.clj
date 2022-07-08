(ns servico-clj.routes
   (:require [io.pedestal.http.route :as route]
             [com.stuartsierra.component :as component]))

(defrecord Routes []
  component/Lifecycle
  (start [this]
    (defn criar-map [uuid name status]
      {:uuid uuid :name name :status status})

    (defn atualizar-tarefa [request]
      (let [tarefa-id (get-in request [:path-params :id])
            tarefa-id-uuid (java.util.UUID/fromString tarefa-id)
            name (get-in request [:query-params :name])
            status (get-in request [:query-params :status])
            body (criar-map tarefa-id-uuid name status)
            store (:store request)]
        (swap! store assoc tarefa-id-uuid body)
        {:status 200 :body {:mensagem "tarefa atualizada com sucesso" :tarefa body}}
        )
      )
    (defn remover-tarefa [request]
      (let [store (:store request)
            tarefa-id (get-in request [:path-params :id])]
        (swap! store dissoc (java.util.UUID/fromString tarefa-id)))
      {:status 200 :body {:mensagem "removido com sucesso"}})

    (defn listar-tarefas [request]
      {:status 200 :body @(:store request)})

    (defn criar-tarefa [request]
      (let [uuid (java.util.UUID/randomUUID)
            name (get-in request [:query-params :name])
            status (get-in request [:query-params :status])
            body (criar-map uuid name status)
            store (:store request)]
        (swap! store assoc uuid body)
        {:status 200 :body {:mensagem "tarefa criada com sucesso" :tarefa body}}
        ))

    (defn funcao-hello [request]
      (println request)
      {:status 200 :body (str "hello world " (get-in request [:query-params :name] " everybody "))}
      )

    (def routes (route/expand-routes
                  #{["/hello" :get funcao-hello :route-name :hello-world]
                    ["/tarefa" :post [criar-tarefa] :route-name :criar-tarefa]
                    ["/tarefa" :get [listar-tarefas] :route-name :listar-tarefas]
                    ["/tarefa/:id" :delete [remover-tarefa] :route-name :remover-tarefa]
                    ["/tarefa/:id" :patch [atualizar-tarefa] :route-name :atualizar-tarefa]
                    }))
    (assoc this :endpoints routes))
  (stop [this]
    (assoc this :endpoints nil)))

(defn create []
  (-> Routes))