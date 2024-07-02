# W and M Casino

## Description
We created a casino simulator consisting of 2 games - BlackJack and European Roulette. The project uses TCP Client-Server communication, each game that player begins to play is a separate thread.
Every player's balance change is saved to database as well as the player's login and password. Players may be connected to the server on specified port and IP and use chat to talk with each other.

## Installation
### Requirements
- Java 21 or newer
- Maven
### Development setup
```shell
git clone https://github.com/xVilly/kasyno.git
# Client:
cd kasyno/kasynoclient
mvn clean install
mvn javafx:run

# Server:
cd kasyno/kasynoserver
cd src/main/java/com/example
java Server.java
```
### Deployment (JAR)
- Use within kasynoclient or kasynoserver project
```shell
mvn clean package
```
