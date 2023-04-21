package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class keyToolsTest {
    
    @Test
    public void testValidEndpoint() {
        given()
          .when().post("/key/valid")
          .then()
             .statusCode(200)
             .body(is("false"));
    }

    @Test
    public void testValidEndpoint2() {
        given()
          .formParam("key", "test")
          .when().post("/key/valid")
          .then()
             .statusCode(200)
             .body(is("true"));
    }

    @Test
    public void testAdminEndpoint() {
        given()
          .formParam("key", "test")
          .when().post("/key/valid/admin")
          .then()
             .statusCode(200)
             .body(is("true"));
    }

    //Test disabled because it polluted the db 
    /*@Test 
    public void testGetEndpoint() {
        given()
          .formParam("login", "test")
          .formParam("pass", "1234")
          .formParam("expiry", "auto")
          .when().post("/key/get")
          .then()
             .statusCode(200);
    }*/

    @Test
    public void testGetEndpoint2() {
        given()
          .formParam("login", "test")
          .formParam("pass", "")
          .formParam("expiry", "auto")
          .when().post("/key/get")
          .then()
             .statusCode(400);
    }

    @Test
    public void testGetEndpoint3() {
        given()
          .formParam("login", "test")
          .formParam("pass", "1234")
          .when().post("/key/get")
          .then()
             .statusCode(401);
    }

    @Test
    public void testGetEndpoint4() {
        given()
          .formParam("login", "test")
          .formParam("pass", "")
          .formParam("expiry", "12")
          .when().post("/key/get")
          .then()
             .statusCode(400);
    }

}
