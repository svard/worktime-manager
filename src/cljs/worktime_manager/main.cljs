(ns worktime-manager.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.components.table :as tbl]
            [worktime-manager.components.pager :as pgr]
            [worktime-manager.utils :as utils]
            [cljs.core.async :refer [<! chan]]
            [cljs-http.client :as http]
            [secretary.core :as secretary :include-macros true :refer [defroute]]
            [goog.events :as events])
  (:import [goog.date DateTime]
           [goog History]
           [goog.history EventType]))

(enable-console-print!)

(def app-state (atom {:reports []
                      :current-date {:week (utils/get-week-number (DateTime.))
                                     :year (.getYear (DateTime.))}
                      :view :table}))

(defroute "/" [] (swap! app-state assoc :view :table))

(defroute "/statistics" [] (swap! app-state assoc :view :stats))

(def history (History.))

(events/listen history EventType.NAVIGATE
  (fn [e]
    (secretary/dispatch! (.-token e))))

(.setEnabled history true)

(defn load-data [app year week]
  (go
    (let [url (str utils/base-url year "/" week)
          response (<! (http/get url))]
      (when (= (:status response) 200)
        (om/update! app :reports (:body response))))))

(defn table-title-view [date owner]
  (om/component
   (dom/h4 #js {:className "text-center"} (str "Showing w" (:week date) " " (:year date)))))

(defn statistics-view [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/h3 nil "Statistics"))))

(defn table-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:nav-chan (chan)})
    om/IWillMount
    (will-mount [_]
      (go
        (let [nav-chan (om/get-state owner :nav-chan)]
          (loop []
            (let [[year week] (<! nav-chan)]
              (load-data app year week)
              (recur))))))
    om/IRenderState
    (render-state [_ {:keys [nav-chan]}]
      (dom/div #js {:className "row"}
        (om/build table-title-view (:current-date app))
        (om/build tbl/table app)
        (om/build pgr/pager (:current-date app) {:init-state {:nav-chan nav-chan}})))))

(defmulti routing (fn [app] (:view app)))

(defmethod routing :table
  []
  (fn [app owner]
    (table-view app owner)))

(defmethod routing :stats
  []
  (fn [app owner]
    (statistics-view app owner)))

(defn entry-view [app owner]
  (om/component
    (let [view (routing app)]
      (om/build view app))))

(go
  (let [year (get-in @app-state [:current-date :year])
        week (get-in @app-state [:current-date :week])
        response (<! (http/get (str utils/base-url year "/" week)))]
    (swap! app-state assoc :reports (:body response))
    (om/root entry-view app-state {:target (. js/document (getElementById "content"))})))
