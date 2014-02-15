(ns worktime-manager.components.table
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.utils :as utils]
            [clojure.string :as string]
            [cljs-http.client :as http]
            [goog.json :as json])
  (:import [goog.date DateTime]))

(def ENTER_KEY 13)

(defn format-cell [cell report]
  (let [from (utils/str->date (:arrival report))
        to (utils/str->date (:leave report))]
    (cond
      (= cell "date") (utils/display-date from)
      (= cell "from") (utils/display-time from)
      (= cell "to") (utils/display-time to)
      (= cell "lunch") (utils/seconds->hours (:lunch report))
      (= cell "total") (utils/seconds->hours (:total report)))))

(defn total-worktime [reports]
  (-> (reduce #(+ % (:total %2)) 0 reports)
      (utils/seconds->hours)))

(defn handle-change [app e]
  nil)

(defn update [body]
  (let [url (str utils/base-url (:_id body))]
    (http/put url {:body (dissoc body :_id) :headers {"Content-Type" "application/edn"}})))

(defn end-edit-from [report new-time]
  (let [[hours minutes seconds] (string/split new-time #":")
        old-time (js/Date. (:arrival @report))]
    (.setHours old-time hours)
    (.setMinutes old-time minutes)
    (.setSeconds old-time seconds)
    (om/transact! report [:arrival] (fn [] (js/Date. (.getTime old-time))))
    (om/transact! report [:total] (fn [] (- (utils/diff-dates (js/Date. (:leave @report)) old-time) (:lunch @report))))
    (update @report)))

(defn end-edit-to [report new-time]
  (let [[hours minutes seconds] (string/split new-time #":")
        old-time (js/Date. (:leave @report))]
    (.setHours old-time hours)
    (.setMinutes old-time minutes)
    (.setSeconds old-time seconds)
    (om/transact! report [:leave] (fn [] (js/Date. (.getTime old-time))))
    (om/transact! report [:total] (fn [] (- (utils/diff-dates old-time (js/Date. (:arrival @report))) (:lunch @report))))
    (update @report)))

(defn end-edit-lunch [report new-time]
  (let [seconds (* new-time 3600)]
    (om/update! report assoc :lunch seconds)
    (om/transact! report [:total] (fn [] (- (utils/diff-dates (js/Date. (:leave @report)) (js/Date. (:arrival @report))) seconds)))
    (update @report)))

(defn table-row [report owner]
  (reify
    om/IRender
    (render [_]
      (let [total (format-cell "total" report)]
        (dom/tr #js {:className (if (< total 7.75) "danger" "success")}
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "date" report)}))
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "from" report)
                            :onChange #(handle-change report %)
                            :onKeyUp #(when (== (.-keyCode %) ENTER_KEY)
                                        (end-edit-from report (.. % -target -value)))}))
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "to" report)
                            :onChange #(handle-change report %)
                            :onKeyUp #(when (== (.-keyCode %) ENTER_KEY)
                                        (end-edit-to report (.. % -target -value)))}))
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "lunch" report)
                            :onChange #(handle-change report %)
                            :onKeyUp #(when (== (.-keyCode %) ENTER_KEY)
                                        (end-edit-lunch report (.. % -target -value)))}))
          (dom/td nil
            (dom/input #js {:type "text" :value total})))))))

(defn table [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "table-responsive"}
        (dom/table #js {:className "table table-striped"}
          (dom/thead nil
            (dom/tr nil
              (dom/th nil "Date")
              (dom/th nil "From")
              (dom/th nil "To")
              (dom/th nil "Lunch")
              (dom/th nil "Total")))
          (apply dom/tbody nil
            (map #(om/build table-row %) (:reports app)))
          (dom/tr nil
            (dom/td nil "")
            (dom/td nil "")
            (dom/td nil "")
            (dom/td nil "")
            (dom/td nil (total-worktime (:reports app)))))))))
