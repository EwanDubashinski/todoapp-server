cd /www
wget -O build.zip /www https://gitlab.com/api/v4/projects/40920491/jobs/artifacts/master/download?job=build&private_token=${GLTOKEN}
unzip /www/build.zip