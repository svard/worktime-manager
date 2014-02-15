(ns worktime-manager.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.components.table :as tbl]
            [worktime-manager.components.pager :as pgr]
            [worktime-manager.utils :as utils]
            [cljs.core.async :refer [<! chan]]
            [cljs-http.client :as http])
  (:import [goog.date DateTime]))

(enable-console-print!)

(def app-state (atom {:reports []
                      :current-date {:week (utils/get-week-number (DateTime.))
                                     :year (.getYear (DateTime.))}}))

(defn load-data [app year week]
  (go
    (let [url (str utils/base-url year "/" week)
          response (<! (http/get url))]
      (when (= (:status response) 200)
        (om/transact! app [:reports] (fn [_] (:body response)))))))

(defn table-title-view [date owner]
  (om/component
   (dom/h4 #js {:className "text-center"} (str "Showing w" (:week date) " " (:year date)))))

(defn table-view [app owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (let [nav-chan (om/get-shared owner :nav-chan)]
        (go
          (loop []
            (let [[year week] (<! nav-chan)]
              (load-data app year week)
              (recur))))))
    om/IRender
    (render [_]
      (dom/div #js {:className "row"}
        (om/build table-title-view (:current-date app))
        (om/build tbl/table app)
        (om/build pgr/pager app)))))

(go
  (let [year (get-in @app-state [:current-date :year])
        week (get-in @app-state [:current-date :week])
        response (<! (http/get (str utils/base-url year "/" week)))]
    (swap! app-state assoc :reports (:body response))
    (om/root table-view app-state {:target (. js/document (getElementById "content"))
                                   :shared {:nav-chan (chan)}})))
