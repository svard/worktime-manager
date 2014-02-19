(ns worktime-manager.handler
  (:use compojure.core)
  (:require [worktime-manager.db :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [monger.joda-time]
            [monger.json]
            [ring.middleware.format :as middleware]
            [ring.util.response :refer [response header]]
            [clojure.java.io :refer [resource]]))

(defn json-response [body]
  (-> body
      (response)
      (header "Content-Type" "application/json")))

(defn timereport [body]
 (let [result (insert-report body)]
   (if (nil? result)
     {:status 500}
     {:status 201
      :headers {"Location" (str (:_id result))}})))

(defn get-timereports [year-str week-str]
  (let [year (read-string year-str)
        week (dec (read-string week-str))]
    (json-response (get-reports-by-week year week))))

(defn update-timereport [id body]
  (let [result (update-report id body)]
    (if (nil? result)
      {:status 500}
      {:status 200})))

(defroutes api-routes
  (POST "/timereport" request (timereport (:body-params request)))
  (PUT "/timereport/:id" {params :params body :body-params} (update-timereport (:id params) body))
  (GET "/timereport/:year/:week" [year week] (get-timereports year week))
  (GET "/stats" [] (json-response (get-stats))))

(defroutes app-routes
  (context "/api" [] api-routes)
  (GET "/" [] (resource "public/index.html"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-restful-format :formats [:json-kw :edn])))
