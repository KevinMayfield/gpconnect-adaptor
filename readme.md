In this directory

mvn install 

docker build . -t gpc-adaptor

docker tag gpc-adaptor thorlogic/gpc-adaptor

docker push thorlogic/gpc-adaptor

docker run -d -p 8182:8182 gpc-adaptor

