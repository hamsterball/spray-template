package com.example.service

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.http.MediaTypes._
import spray.routing.Directive.pimpApply

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class CustomerServiceActor extends Actor with CustomerService with AjaxService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

trait AjaxService extends HttpService {
  val ajaxRoutes =
    path("search" / Segment) { query =>
      get {
        complete {
          s"success ${query}"
        }
      }
    }
}

// this trait defines our service behavior independently from the service actor
trait CustomerService extends HttpService {

  val customerRoutes =
    path("addCustomer") {
      post {
        complete {
          //insert customer information into a DB
          "Success"
        }
      }
    } ~
      path("getCustomer" / Segment) { customerId =>
        get {
          complete {
            //get customer from DB
            s"success ${customerId}"
          }
        }

      }

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }
}