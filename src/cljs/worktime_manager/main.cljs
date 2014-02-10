(ns worktime-manager.main
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.components.table :as tbl]
            [worktime-manager.components.pager :as pgr]
            [worktime-manager.utils :as utils])
  (:import [goog.date DateTime]))

(enable-console-print!)

(def app-state (atom {:reports []
                      :current-date {:week (utils/get-week-number (DateTime.))
                                     :year (.getYear (DateTime.))}}))

(defn table-title-view [date owner]
  (om/component
   (dom/h4 #js {:className "text-center"} (str "Showing w" (:week date) " " (:year date)))))

(defn list-view [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "row"}
        (om/build table-title-view (:current-date app))
        (om/build tbl/table app)
        (om/build pgr/pager app)))))

(om/root app-state list-view (. js/document (getElementById "content")))
