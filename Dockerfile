#We're using alpine because Java is fat enough.
FROM alpine:latest
#The Maven implemntation for Alpine is dependent on  
#OpenJDK8, which (surprisingly) gives us a full SDK
#which means it also gives us a JRE so we don't need
#anything else.
RUN apk add maven
#Taimi sucks and this is her crappy house.
WORKDIR /goblin-hole
#We're going to throw all of our computer garbage
#into the goblin hole. She hates it.
COPY . /goblin-hole/
#This correctly names your Environment file
RUN mv env .env
#This builds the actual bot. Sadly we must do this.
RUN mvn package
#This will move your envionrment file (you did fill
#out your envionrment file, didn't you?) to the 
#directory the actual bot is in.
RUN cp .env ./target/.env
#Run Taimi, run. Justice is coming for your disgusting
#peg toothed head.
ENTRYPOINT [ "java", "-jar", "./target/GW2-Raid-Bot-1.0-SNAPSHOT.jar" ]