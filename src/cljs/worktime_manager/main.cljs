(ns worktime-manager.main
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.components.table :as tbl]
            [worktime-manager.components.pager :as pgr]
            [worktime-manager.utils :as utils]))

(enable-console-print!)

(def app-state (atom {:reports []}))

(defn display-date-title [date-str]
  (let [date (utils/str->date date-str)
        year (.getYear date)]
    (str "Showing w" (utils/get-week-number date) " " year)))

(defn table-title-view [report owner]
  (om/component
   (dom/h4 #js {:className "text-center"} (display-date-title (:arrival report)))))

(defn list-view [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "row"}
        (om/build table-title-view (first (:reports app)))
;;         (om/build tbl/table (:reports app))
        (om/build tbl/table app)
        (om/build pgr/pager app)))))

(om/root app-state list-view (. js/document (getElementById "content")))
