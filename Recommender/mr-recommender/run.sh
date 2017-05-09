#!/bin/bash

#delete step1 output
hadoop fs -rmr step1smalldata

hadoop jar step1/Step1.jar Step1 input step1smalldata
hadoop fs -cat step1smalldata/*

#delete step2 output
hadoop fs -rmr step2smalldata

hadoop jar step2/Step2.jar Step2  step1smalldata step2smalldata
hadoop fs -cat step2smalldata/*

#delete step31 output
hadoop fs -rmr step31smalldata
hadoop jar step3/1/Step31.jar Step31  step1smalldata step31smalldata
hadoop fs -cat step31smalldata/*

#delete step31 output
hadoop fs -rmr step32smalldata
hadoop jar step3/2/Step32.jar Step32  step2smalldata step32smalldata
hadoop fs -cat step32smalldata/*


#delete step4 output
hadoop fs -rmr step4smalldata

#delete step4 input and create new input
hadoop fs -rmr step4input
hadoop fs -mkdir step4input

#get step31 and step32 output put to step4input
hadoop fs -get step31smalldata/part-r-00000
mv part-r-00000 part-r-00001
hadoop fs -put part-r-00001 step4input

rm -rf part-r-00001

hadoop fs -get step32smalldata/part-r-00000
hadoop fs -put part-r-00000 step4input
rm -rf part-r-00000

#step4
hadoop jar step4/Step4.jar Step4  step4input step4smalldata
hadoop fs -cat step4smalldata/*





