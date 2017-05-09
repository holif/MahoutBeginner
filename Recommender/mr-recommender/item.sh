#!/bin/bash
#this script is compute item based recommender

#delete output folder
hadoop fs -rmr mahoutout1

#delete temp folder
hadoop fs -rmr tmp

#execute command
hadoop jar /opt/mahout/mahout-mr-0.12.2-job.jar org.apache.mahout.cf.taste.hadoop.item.RecommenderJob --input input --output mahoutout1 --tempDir tmp --similarityClassname org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.LoglikelihoodSimilarity

hadoop fs -cat mahoutout1/*