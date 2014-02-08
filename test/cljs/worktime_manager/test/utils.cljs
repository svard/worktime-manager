(ns worktime-manager.test.utils
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest testing)])
  (:require [cemerick.cljs.test :as t]
            [worktime-manager.utils :as utils])
  (:import [goog.date DateTime]))

(deftest str->date
  (testing "should create a date object from a string"
    (let [date (utils/str->date "2014-02-03T17:10:04.000+01:00")]
      (is (instance? DateTime date))
      (is (= (.getYear date) 2014))
      (is (= (.getMonth date) 1))
      (is (= (.getDate date) 3))
      (is (= (.getHours date) 17))
      (is (= (.getMinutes date) 10))
      (is (= (.getSeconds date) 4)))))

(deftest display-date
  (testing "should format a date string"
    (let [date (DateTime.)]
      (.setTime date 1387034721000)
      (is (= (utils/display-date date) "2013-12-14"))))

  (testing "should add leading zeros to month and day"
    (let [date (DateTime.)]
      (.setTime date 1391411032000)
      (is (= (utils/display-date date) "2014-02-03")))))

(deftest display-time
  (testing "should format a time string"
    (let [date (DateTime.)]
      (.setTime date 1391527521000)
      (is (= (utils/display-time date) "16:25:21"))))

  (testing "should add leading zeros to hour, minute and second"
    (let [date (DateTime.)]
      (.setTime date 1391411042000)
      (is (= (utils/display-time date) "08:04:02")))))

(deftest seconds->hours
  (testing "should convert seconds to hours"
    (is (= (utils/seconds->hours 27451) 7.63))
    (is (= (utils/seconds->hours 0) 0))
    (is (= (utils/seconds->hours -5) 0))))
