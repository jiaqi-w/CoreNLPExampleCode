# $1: decompress package of stanford-corenlp.
# $2: folder that contain the retrain files.
# Example:
# ./__retrain.sh ~/stanford-corenlp retrain-model
# ----------------------------------------
lib=$1
CLASS_PATH=${lib}/stanford-corenlp-3.6.0.jar:${lib}/stanford-corenlp-3.6.0-models.jar:${lib}/stanford-corenlp-3.6.0-javadoc.jar:${lib}/ejml-0.23.jar:${lib}/slf4j-api.jar:${lib}/slf4j-simple.jar:${lib}/joda-time.jar:${lib}/jollyday.jar
echo ${CLASS_PATH}

retrain_model_folder=$2
echo "Please prepare the train.txt, dev.txt, and test.txt in folder ${retrain_model_folder}"

echo "Convert the custome phrase file to ptd format..."
java -cp ${CLASS_PATH} -Xmx2G edu.stanford.nlp.sentiment.BuildBinarizedDataset -input ${retrain_model_folder}/train.txt -sentimentModel edu/stanford/nlp/models/sentiment/sentiment.ser.gz > ${retrain_model_folder}/train_ptd.txt

java -cp ${CLASS_PATH} -Xmx2G edu.stanford.nlp.sentiment.BuildBinarizedDataset -input ${retrain_model_folder}/dev.txt -sentimentModel edu/stanford/nlp/models/sentiment/sentiment.ser.gz > ${retrain_model_folder}/dev_ptd.txt

echo "Retrain the ptd files with Stanford Sentiment Treebank..."
java -cp ${CLASS_PATH} -Xmx2G edu.stanford.nlp.sentiment.SentimentTraining -numHid 25 -trainPath ${retrain_model_folder}/train_ptd.txt -devPath ${retrain_model_folder}/dev_ptd.txt -train -model ${retrain_model_folder}/retrain_model.ser.gz

# Remove intermedia models
rm ${retrain_model_folder}/retrain_model-*.ser.gz

echo "Predict with retrain model..."
java -cp ${CLASS_PATH} -Xmx2G edu.stanford.nlp.sentiment.SentimentPipeline -file ${retrain_model_folder}/test.txt -sentimentModel ${retrain_model_folder}/retrain_model.ser.gz -output pennTrees > ${retrain_model_folder}/retrain_predict.txt
echo "Predict with original model..."
java -cp ${CLASS_PATH} -Xmx2G edu.stanford.nlp.sentiment.SentimentPipeline -file ${retrain_model_folder}/test.txt -sentimentModel edu/stanford/nlp/models/sentiment/sentiment.ser.gz -output pennTrees > ${retrain_model_folder}/origin_predict.txt
