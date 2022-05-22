package stepdefs;

import api.models.Character;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import static io.restassured.RestAssured.given;

public class TestStepdefs {

    public static final RequestSpecification REQ_SPEC =
            new RequestSpecBuilder()
                    .setBaseUri("https://rickandmortyapi.com/api")
                    .setContentType(ContentType.JSON)
                    .build();

    @Когда("найден персонаж Морти Смит")
    public Character findCharacter() {
        Character character =
                given().spec(REQ_SPEC)
                        .param("name", "Morty Smith")
                        .when()
                        .get("/character")
                        .then()
                        .extract()
                        .body().jsonPath().getList("results", Character.class).get(0);
        return character;
    }

    @Тогда("находим последний эпизод с персонажем")
    public int getLastEpisodeOfChatacter() {
        int colEpisodeCharacter = (new JSONArray(findCharacter().getEpisode()).length() - 1);
        int numberLastEpisodeWithCharacter = Integer.parseInt(new JSONArray(findCharacter().getEpisode()).get(colEpisodeCharacter).toString().replaceAll("[^0-9]", ""));
        return numberLastEpisodeWithCharacter;
    }

    @И("находим последнего персонажа в этом эпизоде")
    public int getLastCharacterOnEpisode() {
        Response lastEpisodeWithCharacterInfo =
                given().spec(REQ_SPEC)
                        .when()
                        .get("/episode/" + getLastEpisodeOfChatacter())
                        .then()
                        .extract()
                        .response();
        int colCharacterOnEpisode = (new JSONObject(lastEpisodeWithCharacterInfo.getBody().asString()).getJSONArray("characters").length() - 1);
        int lastCharacterOnEpisode = Integer.parseInt(new JSONObject(lastEpisodeWithCharacterInfo.getBody().asString()).getJSONArray("characters").get(colCharacterOnEpisode).toString().replaceAll("[^0-9]", ""));
        return  lastCharacterOnEpisode;
    }

    @Тогда("сравниваем местонахождение этого персонажа и Морти Смита")
    public void checkCharactersLocation() {
        Response lastPersonOnEpisodeInfo =
                given().spec(REQ_SPEC)
                        .when()
                        .get("/character/" + getLastCharacterOnEpisode())
                        .then()
                        .extract()
                        .response();
        String location = (new JSONObject(lastPersonOnEpisodeInfo.getBody().asString()).getJSONObject("location").get("name").toString());
        Assert.assertEquals("Разное местонахождение", findCharacter().getLocation().getName(), location);

    }


    @Тогда("сравниваем рассу этого персонажа и Морти Смита")
    public void checkCharactersSpecies() {
        Response lastPersonOnEpisodeInfo =
                given().spec(REQ_SPEC)
                        .when()
                        .get("/character/" + getLastCharacterOnEpisode())
                        .then()
                        .extract()
                        .response();
        String species = (new JSONObject(lastPersonOnEpisodeInfo.getBody().asString()).get("species").toString());
        Assert.assertEquals("Разные рассы", findCharacter().getSpecies(), species);
    }
}
