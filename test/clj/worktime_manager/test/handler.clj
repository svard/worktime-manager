(ns worktime-manager.test.handler
  (:use clojure.test
        ring.mock.request
        worktime-manager.handler
        worktime-manager.db))

(deftest test-routes
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-db-functions
  (testing "successfull insert"
    (let [insert-req (with-redefs [insert-report (constantly {:_id 1})]
                       (timereport {:arriveTime 1 :leaveTime 2 :workTime 3}))]
      (is (= (:status insert-req) 201))
      (is (= (:headers insert-req) {"Location" "1"}))))

  (testing "failed insert"
    (let [insert-req (with-redefs [insert-report (constantly nil)]
                       (timereport {:arriveTime 1 :leaveTime 2 :workTime 3}))]
      (is (= (:status insert-req) 500))))

  (testing "getting timereport"
    (let [fetch (with-redefs [get-reports-by-week (constantly {:total 100 :arrive 100 :leave 100})]
                  (get-timereports "2014" "6"))]
      (is (= (:status fetch) 200))
      (is (= (:headers fetch) {"Content-Type" "application/json"}))
      (is (= (:body fetch) "{\"total\":100,\"leave\":100,\"arrive\":100}")))))
