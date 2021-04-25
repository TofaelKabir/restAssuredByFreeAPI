package b_api_prep;


import a_constants.Constant;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class PostAPIPrep implements Constant {
    public String requestPostAPI(String payload) {
        Response response = RestAssured
                .given()
                    .queryParam("key","qaclick123")
                    .contentType(ContentType.JSON).body(payload)
                    .log().all()
                .when()
                    .post(url2+path)
                .then()
                    .assertThat().statusCode(200).extract().response();
        return response.prettyPrint();

    }
}
