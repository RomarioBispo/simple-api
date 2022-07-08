(ns servico-clj.http-server
  (:require
    [io.pedestal.http :as http]
    [io.pedestal.interceptor :as i]
    [io.pedestal.test :as test]
    [com.stuartsierra.component :as component]
    ))

(defrecord Server [database, routes]
  component/Lifecycle
  (start [this]

    (defn assoc-store [context]
      (update context :request assoc :store (:store database))
      )

    (def db-interceptor
      {:name :db-interceptor
       :enter assoc-store})

    (def service-map-base {::http/routes (:endpoint routes)
                           ::http/port   9999
                           ::http/type   :jetty
                           ::http/join?  false
                           })
    (def service-map
      (-> service-map-base
          (http/default-interceptors)
          (update ::http/interceptors conj (i/interceptor db-interceptor))
          ))

    (defonce server (atom nil))

    (defn stop-server []
      (http/stop @server))

    (defn start-server []
      (reset! server (http/start (http/create-server service-map))))

    (defn restart-server []
      (stop-server)
      (start-server))

    (defn test-request [verb url]
      (test/response-for (::http/service-fn @server) verb url))

    (start-server)

    (assoc this :test-request test-request)
    )
  (stop [this]
    (assoc this :test-request nil)))

(defn create []
  (-> Server))

