(ns worktime-manager.components.table
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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

(defn handle-change [app e]
  (prn (.. e -target -value)))

(defn table-row [report owner]
  (reify
    om/IRender
    (render [_]
      (let [total (format-cell "total" report)]
        (dom/tr #js {:className (if (< total 7.75) "danger" "success")}
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "date" report)
                            :onChange #(handle-change report %)}))
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "from" report)
                            :onChange #(handle-change report %)}))
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "to" report)
                            :onChange #(handle-change report %)}))
          (dom/td nil
            (dom/input #js {:type "text" :value (format-cell "lunch" report)
                            :onChange #(handle-change report %)}))
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
