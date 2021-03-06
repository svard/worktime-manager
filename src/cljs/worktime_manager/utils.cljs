(ns worktime-manager.utils
  (:import [goog.date DateTime]))

(def ^:const base-url "/api/timereport/")

(def month-name ["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"])

(defn str->date [date-str]
  (let [jsdate (js/Date. date-str)
        datetime (DateTime.)]
    (->> (.getTime jsdate)
         (.setTime datetime))
    datetime))

(defn display-date [date]
  (let [month (inc (.getMonth date))
        day (.getDate date)]
    (str (.getYear date) "-" (if (< month 10) (str "0" month) month) "-" (if (< day 10) (str "0" day) day))))

(defn display-short-date [date]
  (let [month (.getMonth date)
        day (.getDate date)]
    (str (get month-name month) " " day)))

(defn display-time [date]
  (let [hours (.getHours date)
        minutes (.getMinutes date)
        seconds (.getSeconds date)]
    (str (if (< hours 10) (str "0" hours) hours) ":" (if (< minutes 10) (str "0" minutes) minutes) ":" (if (< seconds 10) (str "0" seconds) seconds))))

(defn seconds->hours [time]
  (let [hours (/ time 3600)]
    (-> (.round js/Math (* hours 100))
        (/ 100))))

(defn get-week-number [date]
  (.setFirstDayOfWeek date 0)
  (.getWeekNumber date))

(defn diff-dates [d1 d2]
  (/ (- (.getTime d1) (.getTime d2)) 1000))

(defn disabled [disable classes]
  (if disable
    (str classes " disabled")
    classes))

(defn format-cell [column report]
  (let [from (str->date (:arrival report))
        to (str->date (:leave report))]
    (cond
      (= column :date) (display-date from)
      (= column :from) (display-time from)
      (= column :to) (display-time to)
      (= column :lunch) (seconds->hours (:lunch report))
      (= column :total) (seconds->hours (:total report)))))
