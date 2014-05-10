(ns worktime-manager.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [worktime-manager.components.table :as tbl]
            [worktime-manager.components.pager :as pgr]
            [worktime-manager.components.tabs :as tab]
            [worktime-manager.stats :refer [stats-table]]
            [worktime-manager.utils :as utils]
            [worktime-manager.xhr :refer [xhr]]
            [cljs.core.async :refer [<! chan] :as async]
            [secretary.core :as secretary :include-macros true :refer [defroute]]
            [goog.events :as events])
  (:import [goog.date DateTime]
           [goog History]
           [goog.history EventType]))

(enable-console-print!)

(def app-state (atom {:reports []
                      :stats []
                      :current-date {:week (utils/get-week-number (DateTime.))
                                     :year (.getYear (DateTime.))}
                      :route :home}))

(defroute "/"
  []
  (swap! app-state assoc :route :home))

(defroute "/statistics"
  []
  (swap! app-state assoc :route :stats))

(def history (History.))

(events/listen history EventType.NAVIGATE
  (fn [e]
    (secretary/dispatch! (.-token e))))

(.setEnabled history true)

(def nav-chan (chan))

(defn table-title-view
  [date owner]
  (om/component
    (html [:h4.text-center (str "Showing w" (:week date) " " (:year date))])))

(defn statistics-view
  [app owner]
  (reify
    om/IRender
    (render [_]
      (html [:div {:class "row"}
             [:h4.text-center "Statistics"]
             (om/build stats-table (:stats app))]))))

(defn table-view
  [app owner]
  (reify
    om/IRender
    (render [_]
      (html [:div.row
             (om/build table-title-view (:current-date app))
             (om/build tbl/table app)
             (om/build pgr/pager (:current-date app) {:init-state {:nav-chan nav-chan}})]))))

(defmulti routing (fn [app] (:route app)))

(defmethod routing :home
  [app owner]
    (table-view app owner))

(defmethod routing :stats
  [app owner]
    (statistics-view app owner))

(defn entry-view
  [app owner]
  (reify
    om/IRenderState
    (render-state [_ _]
      (om/build routing app))))

(defn tabs
  [route owner]
  (om/component
    (om/build tab/tabs route)))

(go
  (let [year (get-in @app-state [:current-date :year])
        week (get-in @app-state [:current-date :week])]
    (xhr {:url (str utils/base-url year "/" week)
          :method :get
          :content "application/json"
          :on-complete (fn [resp]
                         (swap! app-state assoc :reports resp))})
    (om/root tabs app-state {:target (. js/document (getElementById "nav-tabs"))
                             :shared {:nav-chan nav-chan}})
    (om/root entry-view app-state {:target (. js/document (getElementById "content"))})))

(go
 (loop []
   (let [[year week] (<! nav-chan)
         url (str utils/base-url year "/" week)]
     (xhr {:url url
           :method :get
           :content "application/json"
           :on-complete (fn [resp]
                          (swap! app-state assoc :reports resp))})
   (recur))))

