(ns worktime-manager.db
  (:require [monger.core :refer [connect! set-db! get-db]]
            [monger.collection :refer [insert-and-return find-one aggregate]]
            [monger.joda-time]
            [clj-time [coerce :refer [from-long]]]
            [clojure.java.io :refer [resource]])
  (:import [org.bson.types ObjectId]))

(def mongo-db (read-string (slurp (resource "db.conf"))))

(connect! mongo-db)

(set-db! (get-db "worktime_manager"))

(defn insert-report
  [{:keys [workTime arrivalTime leaveTime lunchTime] :as report}]
  (let [arrival-date (from-long arrivalTime)
        leave-date (from-long leaveTime)]
    (insert-and-return "reports" {:_id (ObjectId.) :total workTime :lunch lunchTime :arrival arrival-date :leave leave-date})))

(defn get-report
  [id]
  (find-one "reports" {:_id (ObjectId. ^String id)}))

(defn get-reports-by-week
  [year week]
  (aggregate "reports" [{"$project" {:total "$total" :arrival "$arrival" :leave "$leave" :lunch "$lunch" :week {"$week" "$arrival"} :year {"$year" "$arrival"}}}
                        {"$match" {:week week :year year}}
                        {"$project" {:total "$total" :arrival "$arrival" :leave "$leave" :lunch "$lunch"}}]))
