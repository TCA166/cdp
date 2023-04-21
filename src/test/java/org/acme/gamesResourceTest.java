package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class gamesResourceTest {

    @Test
    public void testGamesEndpoint() {
        given()
          .when().get("/games")
          .then()
             .statusCode(200);
    }

    @Test
    public void testGamesPostEndpoint() {
        given()
          .formParam("key", "4shrg654ccI=")
          .when().post("/games")
          .then()
             .statusCode(200);
    }

    @Test
    public void failGamesPostEndpoint() {
        given()
          .formParam("key", "")
          .when().post("/games")
          .then()
             .statusCode(200)
             .body(is("[]"));
    }

}