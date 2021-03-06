(ns worktime-manager.db
  (:require [monger.core :refer [connect! set-db! get-db]]
            [monger.collection :refer [insert-and-return find-one aggregate update-by-id]]
            [monger.joda-time]
            [clj-time [coerce :refer [from-long]]]
            [clojure.java.io :refer [resource]])
  (:import [org.bson.types ObjectId]))

(def mongo-db (read-string (slurp (resource "db.conf"))))

(connect! mongo-db)

(set-db! (get-db "worktime_manager"))

(defn insert-report [{:keys [workTime arrivalTime leaveTime lunchTime] :as report}]
  (let [arrival-date (from-long arrivalTime)
        leave-date (from-long leaveTime)]
    (insert-and-return "reports" {:_id (ObjectId.) :total workTime :lunch lunchTime :arrival arrival-date :leave leave-date})))

(defn get-report [id]
  (find-one "reports" {:_id (ObjectId. ^String id)}))

(defn get-reports-by-week [year week]
  (aggregate "reports" [{"$project" {:total "$total" :arrival "$arrival" :leave "$leave" :lunch "$lunch" :week {"$week" "$arrival"} :year {"$year" "$arrival"}}}
                        {"$match" {:week week :year year}}
                        {"$project" {:total "$total" :arrival "$arrival" :leave "$leave" :lunch "$lunch"}}
                        {"$sort" {"arrival" 1}}]))

(defn update-report [id {:keys [total arrival leave lunch] :as report}]
  (let [arrival-date (from-long arrival)
        leave-date (from-long leave)]
    (update-by-id "reports" (ObjectId. ^String id) {:total total :lunch lunch :arrival arrival-date :leave leave-date})))

(defn get-stats []
  (aggregate "reports" [{"$project" {:year {"$year" "$arrival"} :total "$total" :arrival "$arrival"}}
                        {"$sort" {"total" 1}}
                        {"$group" {:_id "$year" :sum {"$sum" "$total"} :avg {"$avg" "$total"} :max {"$max" "$total"} :min {"$min" "$total"} :shortest {"$first" "$arrival"} :longest {"$last" "$arrival"}}}
                        {"$project" {:sum "$sum" :avg "$avg" :longest {:time "$max" :date "$longest"} :shortest {:time "$min" :date "$shortest"}}}
                        {"$sort" {"_id" 1}}]))
