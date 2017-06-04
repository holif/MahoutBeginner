#!/bin/bash

#add hadoop user
echo '***Add hadoop user***'
groupadd hadoop  
useradd -g hadoop hadoop
echo '***Input new password by hadoop user***'
passwd hadoop
echo 'hadoop  ALL=(ALL)       ALL' >>/etc/sudoers

cd /opt/

#install jdk
echo '***Install JDK***'
wget "http://mirrors.linuxeye.com/jdk/jdk-8u112-linux-x64.tar.gz"
tar zxvf jdk-8u112-linux-x64.tar.gz
#set java environment variable
echo 'export JAVA_HOME=/opt/jdk1.8.0_112' >> /etc/profile
echo 'export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar' >> /etc/profile
echo "export JAVA_OPTS='-Xms128m -Xmx256m'" >> /etc/profile


#install hadoop
echo '***Install Hadoop***'
wget  http://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-2.8.0/hadoop-2.8.0.tar.gz 
tar zxvf hadoop-2.8.0.tar.gz 
mv hadoop-2.8.0 hadoop
#download hadoop config file
wget  https://raw.githubusercontent.com/holif/MahoutBeginner/master/mr-recommender/core-site.xml
mv core-site.xml /opt/hadoop/etc/hadoop/core-site.xml

wget  https://raw.githubusercontent.com/holif/MahoutBeginner/master/mr-recommender/hdfs-site.xml
mv hdfs-site.xml /opt/hadoop/etc/hadoop/hdfs-site.xml

sed -i 's#${JAVA_HOME}#/opt/jdk1.8.0_112#g' /opt/hadoop/etc/hadoop/hadoop-env.sh

chown -R hadoop:hadoop ./hadoop
echo 'export HADOOP_HOME=/opt/hadoop' >> /etc/profile
echo 'export HADOOP_HOME_WARN_SUPPRESS=not_null' >> /etc/profile
echo 'export CLASSPATH=$($HADOOP_HOME/bin/hadoop classpath):$CLASSPATH' >> /etc/profile


#install mahout
wget https://mirrors.tuna.tsinghua.edu.cn/apache/mahout/0.12.2/apache-mahout-distribution-0.12.2.tar.gz
tar zxvf apache-mahout-distribution-0.12.2.tar.gz
mv apache-mahout-distribution-0.12.2 mahout
echo 'export MAHOUT_HOME=/opt/mahout' >> /etc/profile
echo 'export MAHOUT_LOCAL=true' >> /etc/profile
echo 'export MAHOUT_CONF_DIR=$MAHOUT_HOME/conf' >> /etc/profile

echo 'export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$MAHOUT_HOME/bin:$MAHOUT_HOME/conf' >> /etc/profile
chown -R hadoop:hadoop ./mahout
source /etc/profile

su - hadoop<<EOF

ssh-keygen -t rsa
ssh-copy-id localhost

java -version
echo '\n'

/opt/hadoop/bin/hdfs namenode -format
/opt/hadoop/sbin/start-dfs.sh
hadoop version
echo '\n'

mahout


