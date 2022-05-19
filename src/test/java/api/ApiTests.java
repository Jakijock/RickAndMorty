package api;

import api.models.Character;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;


public class ApiTests {
    public static final RequestSpecification REQ_SPEC =
            new RequestSpecBuilder()
                    .setBaseUri("https://rickandmortyapi.com/api")
                    .setContentType(ContentType.JSON)
                    .build();

    @Test
    public void checkMortyInfo() {
        Character character =
                given().spec(REQ_SPEC)
                        .param("name", "Morty Smith")
                        .when()
                        .get("/character")
                        .then()
                        .extract()
                        .body().jsonPath().getList("results", Character.class).get(0);
        int colEpisodeCharacter = (new JSONArray(character.getEpisode()).length() - 1);
        int numberLastEpisodeWithCharacter = Integer.parseInt(new JSONArray(character.getEpisode()).get(colEpisodeCharacter).toString().replaceAll("[^0-9]", ""));

        Response lastEpisodeWithCharacterInfo =
                given().spec(REQ_SPEC)
                        .when()
                        .get("/episode/" + numberLastEpisodeWithCharacter)
                        .then()
                        .extract()
                        .response();
        int colCharacterOnEpisode = (new JSONObject(lastEpisodeWithCharacterInfo.getBody().asString()).getJSONArray("characters").length() - 1);
        int lastCharacterOnEpisode = Integer.parseInt(new JSONObject(lastEpisodeWithCharacterInfo.getBody().asString()).getJSONArray("characters").get(colCharacterOnEpisode).toString().replaceAll("[^0-9]", ""));

        Response lastPersonOnEpisodeInfo =
                given().spec(REQ_SPEC)
                        .when()
                        .get("/character/" + lastCharacterOnEpisode)
                        .then()
                        .extract()
                        .response();
        String location = (new JSONObject(lastPersonOnEpisodeInfo.getBody().asString()).getJSONObject("location").get("name").toString());
        String species =  (new JSONObject(lastPersonOnEpisodeInfo.getBody().asString()).get("species").toString());
        Assert.assertEquals("Разное местонахождение", character.getLocation().getName(), location);
        Assert.assertEquals("Разные рассы", character.getSpecies(), species);
    }

}



