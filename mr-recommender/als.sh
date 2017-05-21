#!/bin/bash
#this script is compute user based recommender

#delete output folder
hadoop fs -rmr output2
hadoop fs -rmr recommendations
#delete temp folder
hadoop fs -rmr tmp

#execute command
mahout parallelALS --input input --output output2 --lambda 0.1 --implicitFeedback true --alpha 0.8 --numFeatures 2 --numIterations 5  --numThreadsPerSolver 1 --tempDir tmp

mahout recommendfactorized --input output2/userRatings/ --userFeatures output2/U/ --itemFeatures output2/M/ --numRecommendations 1 --output recommendations --maxRating 1



hadoop fs -cat recommendations/*