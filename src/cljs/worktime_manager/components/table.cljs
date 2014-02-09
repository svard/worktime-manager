(ns worktime-manager.components.table
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [worktime-manager.utils :refer [to-week]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [worktime-manager.utils :as utils])
  (:import [goog.date DateTime]))

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

(defn table-row [report owner]
  (om/component
    (dom/tr nil
      (dom/td nil (format-cell "date" report))
      (dom/td nil (format-cell "from" report))
      (dom/td nil (format-cell "to" report))
      (dom/td nil (format-cell "lunch" report))
      (dom/td nil (format-cell "total" report)))))

(defn table [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:total-time 0})
    om/IWillMount
    (will-mount [_]
      (go
        (let [year (.getYear (DateTime.))
              week (utils/get-week-number (DateTime.))
              url (str "/api/timereport/" year "/" week)
              response (<! (http/get url))]
          (when (= (:status response) 200)
            (om/transact! app [:reports] (fn [_] (:body response)))
            (om/update! app assoc :current-week (to-week (:arrival (first (:body response)))))
            (om/set-state! owner :total-time (total-worktime (:body response)))))))
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
              (dom/th nil "Total hours")))
          (apply dom/tbody nil
            (map #(om/build table-row %) (:reports app)))
;;             (map #(om/build table-row %) reports))
          (dom/tr nil
            (dom/td nil "")
            (dom/td nil "")
            (dom/td nil "")
            (dom/td nil "")
            (dom/td nil (om/get-state owner :total-time))))))))
