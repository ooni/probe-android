#!/bin/bash
for i in $(ls app/src/main/assets/test_lists/); do wget -q "https://raw.githubusercontent.com/citizenlab/test-lists/master/lists/$i" -O "app/src/main/assets/test_lists/$i"; done
