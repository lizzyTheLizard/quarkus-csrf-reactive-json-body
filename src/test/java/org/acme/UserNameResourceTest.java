package org.acme;

import io.quarkus.csrf.reactive.runtime.CsrfRequestResponseReactiveFilter;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(UserNameResource.class)
class UserNameResourceTest {
  private final static String COOKIE_NAME="csrf-token";
  private final static String HEADER_NAME = "X-CSRF-TOKEN";


  @Test
  public void form(){
    final var token = RestAssured
        .when().get("/csrfTokenForm")
        .then().statusCode(200).cookie(COOKIE_NAME)
        .extract().cookie(COOKIE_NAME);
    final var encoderConfig = EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data",ContentType.TEXT);
    final var restAssuredConfig = RestAssured.config().encoderConfig(encoderConfig);

    //no token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .config(restAssuredConfig)
          .formParam("name", "testName")
          .contentType( ContentType.URLENC)
        .when().post("csrfTokenForm")
        .then().statusCode(400);

    //wrong token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .config(restAssuredConfig)
          .formParam(COOKIE_NAME, "WRONG")
          .formParam("name", "testName")
          .contentType( ContentType.URLENC)
        .when().post("csrfTokenForm")
        .then().statusCode(400);

    //valid token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .config(restAssuredConfig)
          .formParam(COOKIE_NAME, token)
          .formParam("name", "testName")
          .contentType( ContentType.URLENC)
        .when().post("csrfTokenForm")
        .then().statusCode(200).body(Matchers.equalTo("testName"));
  }

  @Test
  public void json_no_body(){
    final var token = RestAssured
        .when().get("/csrfTokenForm")
        .then().statusCode(200).cookie(COOKIE_NAME)
        .extract().cookie(COOKIE_NAME);

    // no token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
        .when().post("csrfTokenPost")
        .then().statusCode(400);

    //wrong token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .header(HEADER_NAME, "WRONG")
        .when().post("csrfTokenPost")
        .then().statusCode(400);

    //valid token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .header(HEADER_NAME, token)
        .when().post("csrfTokenPost")
        .then().statusCode(200).body(Matchers.equalTo("no user"));
  }

  @Test
  public void json_body(){
    final var token = RestAssured
        .when().get("/csrfTokenForm")
        .then().statusCode(200).cookie(COOKIE_NAME)
        .extract().cookie(COOKIE_NAME);

    // no token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .body("{\"name\":\"testName\"}")
          .contentType( ContentType.JSON)
        .when().post("csrfTokenPostBody")
        .then().statusCode(400);

    //wrong token
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .header(HEADER_NAME, "WRONG")
          .body("{\"name\":\"testName\"}")
          .contentType( ContentType.JSON)
        .when().post("csrfTokenPostBody")
        .then().statusCode(400);

    //valid token => This test fails but should work
    RestAssured
        .given()
          .cookie(COOKIE_NAME, token)
          .header(HEADER_NAME, token)
          .body("{\"name\":\"testName\"}")
          .contentType( ContentType.JSON)
        .when().post("csrfTokenPostBody")
        .then().statusCode(200).body(Matchers.equalTo("testName"));
  }
}