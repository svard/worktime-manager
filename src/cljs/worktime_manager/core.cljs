(ns worktime-manager.core
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secretary.core :as secretary :include-macros true :refer [defroute]])
  (:import [goog History]
           [goog.history EventType]
           [goog.date Date]))

(enable-console-print!)

;; (def time (Date.))

(def app-state (atom {:login {:text "Hello login!"}
                      :main {:text "Hello main!" :time (Date.)}}))

(defroute "/" [] (swap! app-state assoc :route :main))

(defroute "/login" [] (swap! app-state assoc :route :login))

(def history (History.))

(events/listen history EventType.NAVIGATE
  (fn [e]
    (secretary/dispatch! (.-token e))))

(.setEnabled history true)

(defn login-view [content owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (dom/h2 nil (:text content))))))

(defn main-view [content owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (dom/h2 nil (:text content))
        (dom/p nil (str "This is week " (.getWeekNumber (:time content))))))))

(defmulti select-view (fn [app _] (:route app)))

(defmethod select-view :login
  [app owner]
  (login-view (:login app) owner))

(defmethod select-view :main
  [app owner]
  (main-view (:main app) owner))

(defn view [app owner]
  (reify
    om/IRender
    (render [_]
      (om/build select-view app))))

(om/root app-state view (. js/document (getElementById "container")))
