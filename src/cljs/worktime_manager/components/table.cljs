(ns worktime-manager.components.table
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.utils :as utils]
            [worktime-manager.components.editable :refer [editable]]
            [clojure.string :as string]
            [cljs-http.client :as http]
            [goog.json :as json])
  (:import [goog.date DateTime]))

(def ENTER_KEY 13)

(defn total-worktime [reports]
  (-> (reduce #(+ % (:total %2)) 0 reports)
      (utils/seconds->hours)))

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
    (om/update! report [:lunch] seconds)
    (om/transact! report [:total] (fn [] (- (utils/diff-dates (js/Date. (:leave @report)) (js/Date. (:arrival @report))) seconds)))
    (update @report)))

(defn table-row [report owner]
  (reify
    om/IRender
    (render [_]
      (let [total (utils/format-cell :total report)]
        (dom/tr #js {:className (if (< total 7.75) "danger" "success")}
          (dom/td nil (utils/format-cell :date report))
          (om/build editable report {:opts {:column :from
                                            :end-edit end-edit-from}})
          (om/build editable report {:opts {:column :to
                                            :end-edit end-edit-to}})
          (om/build editable report {:opts {:column :lunch
                                            :end-edit end-edit-lunch}})
          (dom/td nil (utils/format-cell :total report)))))))

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
