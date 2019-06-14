---
name: Release QA check list
about: Check list for performing QA of a release candidate
title: 'QA: check-list for vX.Y.Z'
labels: testing
assignees: ''

---

# App

- [ ] verify that version numbers are reasonable
- [ ] verify that settings are default

# Websites test

- [ ] verify that settings are default
- [ ] run the test in foreground without specifying any option
    - [ ] check that the time is decreasing
- [ ] run the test in foreground and then put the app in background
    - [ ] make sure that the time is making progress
    - [ ] make sure that you receive a notification
    - [ ] make sure that the test performed work in background
- [ ] run the test with just https://ooni.torproject.org/ and https://expired.badssl.com/
- [ ] run the test with only some categories
    - [ ] make sure you did only actually test these categories
- [ ] run the test with a very short test runtime
- [ ] run the test without uploading measurements and then perform a background upload
    - [ ] make sure that the upload counter behaves properly
- [ ] results analysis of all the above
    - [ ] check that the summary shows some number blocked some accessible
    - [ ] check that the blocked sites are displayed first
    - [ ] check that the icons are displayed next to each measurement
    - [ ] check that the data usage shows a sane number, ex 6.3MB up, 5.8MB down
    - [ ] check that the total runtime shows a sane number, ex. 257s
    - [ ] check specific measurements (at least one okay, one blocked)
        - [ ] check that everything is displayed properly in this screen
        - [ ] check that the runtime is sane ex 2.71s
        - [ ] check that the data makes sense
        - [ ] check that the logs make sense


# IM tests

- [ ] verify that settings are default
- [ ] run the test in foreground without specifying any option
    - [ ] check that the time is decreasing
- [ ] run the test in foreground and then put the app in background
    - [ ] make sure that the time is making progress
    - [ ] make sure that you receive a notification
    - [ ] make sure that the test performed work in background
- [ ] run just one of the tests and verify the others didn't
- [ ] results analysis of all the above
    - [ ] check that the total count on top matches
    - [ ] check that the total counts on top match
    - [ ] check that the data usage shows a sane number, ex 36KB up, 18KB down
    - [ ] check that the total runtime shows a sane number, ex. 2.16s
    - [ ] check specific measurements
        - [ ] check that everything is displayed properly in this screen
        - [ ] check that the runtime is sane ex 2.71s
        - [ ] check that the data makes sense
        - [ ] check that the logs make sense

# Middlebox test

- [ ] verify that settings are default
- [ ] run the test in foreground without specifying any option
    - [ ] check that the time is decreasing
- [ ] run the test in foreground and then put the app in background
    - [ ] make sure that the time is making progress
    - [ ] make sure that you receive a notification
    - [ ] make sure that the test performed work in background
- [ ] run just one of the tests and verify the others didn't
- [ ] results analysis of all the above
    - [ ] check that the data usage shows a sane number, ex 8KB up, 4KB down
    - [ ] check that the total runtime shows a sane number, ex. 5s
    - [ ] check specific measurements
        - [ ] check that everything is displayed properly in this screen
        - [ ] check that the runtime is sane ex 2.71s
        - [ ] check that the data makes sense
        - [ ] check that the logs make sense

# Performance test

- [ ] verify that settings are default
- [ ] run the test in foreground without specifying any option
    - [ ] check that the time is decreasing
- [ ] run the test in foreground and then put the app in background
    - [ ] make sure that the time is making progress
    - [ ] make sure that you receive a notification
    - [ ] make sure that the test performed work in background
- [ ] run just one of the tests and verify the others didn't
- [ ] results analysis of all the above
    - [ ] check that you see upload, download and video speed in the summary
    - [ ] check individual results and see whether they make sense
    - [ ] check that the data usage shows a sane number, ex 22MB up, 104MB down
    - [ ] check that the total runtime shows a sane number, ex. 54s
    - [ ] check specific measurements
        - [ ] check that everything is displayed properly in this screen
        - [ ] check that the runtime is sane ex 30s
        - [ ] check that the data makes sense
        - [ ] check that the logs make sense

# Results visualization

- [ ] try to adjust the filter and validate that only the filtered results appear
- [ ] tap the result row
    - [ ] check that the last run test is highlighted
    - [ ] check that the date & time of the test are correct and displayed in the timezone of the phone
    - [ ] check that the country & network are resolved properly
- [ ] tap the measurement row
    - [ ] check that the country & network are resolved properly
    - [ ] check that it’s possible to view the log
    - [ ] check that it’s possible to view data
    - [ ] check that the report_id is not null, unless publish result is disabled
    - [ ] check that the test_start_time in the measurement is using GMT

# Settings

- [ ] notifications
    - [ ] try to enable notifications and ensure that it asks you to enable it in the system settings (assumes you have disabled it in the system settings)
- [ ] sharing
    - [ ] disable publish results. Run a test and verify that the result does not include your report_id
    - [ ] disable “include network info”, “include country code”, “include my IP address”. Run a test and validate that the expected information appears in the results
- [ ] advanced
    - [ ] enable debug logs and run a test. Check that the logs are at debug level verbosity.
- [ ] About
    - [ ] check that all the links in the about page work as expected

# OONI Run

- [ ] Check that tappng tapping on the following web_connectivity OONI Run link works as expected:
    - [ ] no inputs: https://run.ooni.io/nettest?tn=web_connectivity&mv=2.0.0 
    - [ ] empty inputs: https://run.ooni.io/nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%5D%7D&mv=2.0.0 [XXX]
    - [ ] partial input: https://run.ooni.io/nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%22http%3A%2F%2F%22%5D%7D&mv=2.0.0
    - [ ] valid URLs: https://run.ooni.io/nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%22http%3A%2F%2Fwww.google.it%22%2C%22https%3A%2F%2Frun.ooni.io%2F%22%5D%7D&mv=2.0.0 
     - [ ] WC Crash: https://run.ooni.io/nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%22http%3A%2F%2Fwww.google.it%22%2C%22https%3A%2F%2Frun.ooni.io&mv=2.0.0 [XXX]
- [ ] check it’s possible to run NDT via OONI Run: https://run.ooni.io/nettest?tn=ndt&mv=2.0.0
- [ ] check it’s possible to run Dash https://run.ooni.io/nettest?tn=dash&mv=2.0.0
- [ ] check it’s possible to run HIRL https://run.ooni.io/nettest?tn=http_invalid_request_line&mv=2.0.0
- [ ] check it’s possible to run HHFM https://run.ooni.io/nettest?tn=http_header_field_manipulation&mv=2.0.0
- [ ] Check that the minimum version is being verified Min version error https://run.ooni.io/nettest?tn=ndt&mv=15.0.0
- [ ] check that an error is shown when a non-existent Test name is selected https://run.ooni.io/nettest?tn=fake_test_name&mv=2.0.0
