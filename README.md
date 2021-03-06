Evolutionary Algorithms Traveling Salesman Problem
==========

This is the Evolutionairy Algorithm Demo for the Traveling Salesman Problem that I use to demo the simplicity and power of Evolutionairy Algoritms.

To get it, just clone this repository by using your favorite git gui or by executing 

```
git clone https://github.com/bknopper/TSPEvolutionaryAlgorithmsDemo.git
```

### DISCLAIMER
This is a demo, and as such, I've used the prototyping approach. The domain part of the backend has been constructed with thought...all else was built to work. As fast as possible.


### Running the Demo - Backend
Because this demo uses [Gradle](https://gradle.org/) as package manager / build automation system and has the Gradle wrapper incorporated you can run the backend of this demo by simply executing the gradle wrapper.

For example, from the `TSPEADemo` directory, run:

```bash
./gradlew run
```
This should start resolving all the dependency and start the backend of the demo for you using Spring Boot.

### Running the Demo - Frontend
Since the frontend is based on angular, we need some form of http serving. My favorite is the python SimpleHttpServer and you can let it start serving by running the following from the `TSPEADemo/frontend/app` dir.

For Python 2:

```bash
python -m SimpleHTTPServer 8001
```

For Python 3:

```bash
python -m http.server 8001
```

### Storing runs using Firebase
If you have a Firebase account and would like to store runs and show the 10 latest best, you can add a `demo.properties` file in the `frontend/app` dir with the following content:

```json
{
  "useFirebase": true,
  "firebaseURL": "https://yoururlhere.firebaseio.com/algorithmResults"
}
```

### Have fun!
That's it! Now you can navigate to `http://localhost:8001` to start playing. 

Have fun!

For the talk I did on J-Fall where this demo is used, see: [Evolutionairy Algorithms J-Fall talk](http://www.youtube.com/watch?v=5LUqjnwbp5c)
