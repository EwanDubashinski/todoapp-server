mkdir -p /www
cd /www
url="https://gitlab.com/api/v4/projects/40920491/jobs/artifacts/master/download?job=build&private_token="$GLTOKEN
wget -O build.zip $url
unzip /www/build.zip
cd ..
java -jar /todoapp-srv-1.0.0.jar