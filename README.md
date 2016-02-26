<!-- the line below needs to be an empty line C: (its because kramdown isnt
     that smart and dearly wants an empty line before a heading to be able to
     display it as such, e.g. website) -->

# Video rental implemented in Java

## Introduction

For a video rental store we want to create a system for managing the rental administration.

We want three primary functions:

 - Have an inventory of films
 - Calculate the price for rentals
 - Keep track of the customers “bonus” points

## Build

`mvn clean install` should do the job. Hint: you need Maven 3.1.1 and JDK1.8.0_60 to build and run the project.

## Usage

You can have a look at all the integration test suites, especially at `RentalIntegrationTest`.

Following REST URLs are provided:

 - `POST    /customer/create (com.casumo.interview.videorental.resources.CustomerResource)`
 - `GET     /customer/getAll (com.casumo.interview.videorental.resources.CustomerResource)`
 - `POST    /film/create (com.casumo.interview.videorental.resources.FilmResource)`
 - `GET     /film/getAll (com.casumo.interview.videorental.resources.FilmResource)`
 - `GET     /film/getAvailable (com.casumo.interview.videorental.resources.FilmResource)`
 - `GET     /film/getWithPrefix/{prefix} (com.casumo.interview.videorental.resources.FilmResource)`
 - `POST    /rental/finishRental (com.casumo.interview.videorental.resources.RentalResource)`
 - `GET     /rental/getByCustomerId (com.casumo.interview.videorental.resources.RentalResource)`
 - `POST    /rental/startRental (com.casumo.interview.videorental.resources.RentalResource)`

You can start Jetty after having built by issuing following command: `java -jar target/video-rental-1.0.0-SNAPSHOT.jar`.

## Example

To create a user:

`curl -H "Content-Type: application/json" -X POST --data-binary @customer.json http://localhost:8080/customer/create`

Returned:

`{"id":1,"name":"Malin Svensson","bonus":0,"balance":0}`