package b_api_prep;


import a_constants.Constant;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetAPIPrep implements Constant {

    public String requestGetAPI() {
        Response response = RestAssured
                .given()
                .queryParam("lat","35").queryParam("lon","139").queryParam("appid","ba91e931326e40738534796eef2330ba").log().all()
                .when()
                .get(url1+context+version+resourceA)
                .then()
                .assertThat().statusCode(200).extract().response();
        return response.prettyPrint();

// below way by using baseURI

//        RestAssured.baseURI = "http://api.openweathermap.org/data/2.5/weather";
//        Response response = RestAssured
//                .given()
//                .queryParam("lat","35")
//                .queryParam("lon","139")
//                .queryParam("appid","ba91e931326e40738534796eef2330ba").log().all()
//                .get();
//        response.prettyPrint();


//        if authentication is necessary:
//        .given().auth().preemptive().basic(user id, password)
//         .auth().preemptive().oauth2(user id)

    }
}
