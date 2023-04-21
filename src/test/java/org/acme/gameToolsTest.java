package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class gameToolsTest {

    @Test
    public void testNoKeyEndpoint() {
        given()
          .when().post("/games/delete")
          .then()
             .statusCode(401);
    }

    @Test
    public void testNoGameEndpoint() {
        given()
          .formParam("key", "test")
          .formParam("uid", "0")
          .when().post("/games/delete")
          .then()
             .statusCode(500);
    }

    @Test
    public void testAddEndpoint() {
        given()
          .formParam("key", "test")
          .formParam("date", "2000-01-01")
          .formParam("studio", "test")
          .when().post("/games/add")
          .then()
             .statusCode(400);
    }

    @Test
    public void testUpdateEndpoint() {
        given()
          .formParam("key", "test")
          .formParam("attr", "date")
          .formParam("var", "2000-01-01")
          .when().post("/games/update")
          .then()
             .statusCode(400);
    }

    @Test
    public void testUpdateEndpoint2() {
        given()
          .formParam("key", "test")
          .formParam("attr", "date")
          .formParam("val", "2000-01-01")
          .formParam("uid", "6")
          .when().post("/games/update")
          .then()
             .statusCode(200);
    }
}
