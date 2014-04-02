(ns worktime-manager.components.tabs
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [put!]]
            [worktime-manager.components.selectable :refer [dropdown]]))

(defn active? [is-active]
  (if is-active
    {:class "active"}
    {}))

(defn change-value [app owner type new-val old-val]
  (when (not= new-val old-val)
    (let [ch (om/get-shared owner :nav-chan)
          year (get-in @app [:current-date :year])
          week (get-in @app [:current-date :week])]
      (om/update! app [:current-date type] new-val)
      (case type
        :year (put! ch [new-val week])
        :week (put! ch [year new-val])))))

(defn tabs [app owner]
  (reify
    om/IRender
    (render [_]
      (html [:ul.nav.nav-tabs
             [:li (active? (= (:route app) :home))
              [:a {:href "#/"} "Home"]]
             [:li (active? (= (:route app) :stats))
              [:a {:href "#/statistics"} "Statistics"]]
             [:li.dropdown
              [:a.dropdown-toggle {:data-toggle "dropdown"} "Year " [:span.caret]]
              (om/build dropdown app {:opts {:value-key :valid-years
                                             :on-change-fn (partial change-value app owner :year)}
                                      :init-state {:selected-val (get-in app [:current-date :year])}})]
             [:li.dropdown
              [:a.dropdown-toggle {:data-toggle "dropdown"} "Week " [:span.caret]]
              (om/build dropdown app {:opts {:value-key :valid-weeks
                                             :on-change-fn (partial change-value app owner :week)}
                                      :init-state {:selected-val (get-in app [:current-date :week])}})]]))))
