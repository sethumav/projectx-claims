import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
 * Performance test for the Claim entity.
 */
class ClaimGatlingTest extends Simulation {

    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    // Log all HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
    // Log failed HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

    val baseURL = Option(System.getProperty("baseURL")) getOrElse """http://localhost:8080"""
//    val baseKeycloakURL = Option(System.getProperty("baseKeycloakURL")) getOrElse """http://localhost:9080"""

    val httpConf = http
        .baseURL(baseURL)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

    val headers_http = Map(
        "Accept" -> """application/json"""
    )

    val headers_http_authenticated = Map(
        "Accept" -> """application/json"""
        //"X-XSRF-TOKEN" -> "${xsrf_token}"
    )

    val keycloakHeaders = Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Upgrade-Insecure-Requests" -> "1"
    )

    val scn = scenario("Test the Claim entity")
        .exec(http("Authentication")
            .get("/login")
            .headers(keycloakHeaders)
            .check(css("#kc-form-login", "action").saveAs("kc-form-login"))
        ).exitHereIfFailed
        .pause(1,10)
        .exec(http("Authenticate")
        .post("${kc-form-login}")
        .headers(keycloakHeaders)
        .formParam("username", "user")
        .formParam("password", "user")
        .formParam("submit", "Login")
        .check(status.is(200))).exitHereIfFailed
        .pause(1)
        .exec(http("Authenticated request")
        .get("/api/account")
        .headers(headers_http_authenticated)
        .check(status.is(200)))
        .pause(1, 10)
        .repeat(2) {
            exec(http("Get all claims")
            .get("/claims/api/claims")
            .headers(headers_http_authenticated)
            .check(status.is(200)))
            .pause(10 seconds, 20 seconds)
            .exec(http("Create new claim")
            .post("/claims/api/claims")
            .headers(headers_http_authenticated)
            .body(StringBody("""{"id":, "identifier":"SAMPLE_TEXT"}""")).asJSON
            .check(status.is(201))
            .check(headerRegex("Location", "(.*)").saveAs("new_claim_url"))).exitHereIfFailed
            .pause(1, 10)
            .repeat(5) {
                exec(http("Get created claim")
                .get("/claims${new_claim_url}")
                .headers(headers_http_authenticated))
                .pause(1, 10)
            }
            .exec(http("Delete created claim")
            .delete("/claims${new_claim_url}")
            .headers(headers_http_authenticated))
            .pause(1, 10)
        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(
            rampUsers(Integer.getInteger("users", 200)) over (Integer.getInteger("ramp", 200) seconds),
                constantUsersPerSec(200) during(30 minutes)
        )
    ).protocols(httpConf)
}
